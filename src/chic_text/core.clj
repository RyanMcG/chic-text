(ns chic-text.core
  (:require [clojure.string :as s]))

;; Protocols, records and constructors
(defprotocol ICell
  (lines [this] "Return a sequential collection of lines."))

(defprotocol IRow
  (cells [this] "Return a sequential collection of cells that make up the row."))

(defprotocol ILength
  (length [this] "Return the number of elements in the container."))

(defrecord Cell [contents]
  ICell
  (lines [this] (:contents this))
  ILength
  (length [this] (count (lines this))))

(defrecord Row [contents]
  IRow
  (cells [this] (:contents this))
  ILength
  (length [this] (count (cells this))))

(def ^:private will-satisfy? #(partial satisfies? %))
(def ^:private ilength? (will-satisfy? ILength))
(def cell? (every-pred (will-satisfy? ICell) ilength?))
(def row? (every-pred (will-satisfy? IRow) ilength?))

(defn make-cell [lines]
  {:post [(cell? %)]}
  (->Cell (vec lines)))

(defn make-row [cells]
  {:pre [(every? cell? cells)]
   :post [(row? %)]}
  (->Row (vec cells)))

;; Padding functions
(defn spaces [n] (apply str (repeat n \space)))

(defn- really-pad [padding a-str]
  {:pre [(>= padding (count a-str))]
   :post [(= padding (count %))]}
  (str a-str (spaces (- padding (count a-str)))))

(defn pad [padding a-str]
  (if (> (count a-str) padding)
    a-str
    (really-pad padding a-str)))

(defn- wrap-line-to-width
  "Turn a string into cell where string lengths are less than or equal to the
  given column limit."
  [width a-str]
  {:pre [(string? a-str)]}
  (let [words (s/split a-str #"\s+")]
    (loop [line (first words)
           words (next words)
           lines (transient [])]
      (if words
        (let [nword (first words)]
          (if (< (+ (count line) (count nword)) width)
            (recur (str line \space nword)
                   (next words)
                   lines)
            (recur nword
                   (next words)
                   (conj! lines line))))
        (persistent! (conj! lines line))))))

(defn cell-with-width [width cell]
  (make-cell
    (->> cell
         lines
         (map str)
         (mapcat #(s/split % #"\n"))
         (map (partial wrap-line-to-width width))
         (apply concat)
         (map (partial pad width)))))

(defn- extend-with [make contents padder]
  (fn pad-up-to [pad-to-n]
    (fn extend-coll [extendable]
      (assert (>= pad-to-n (length extendable))
              "padding count must be greater than or equal to the extendable's count")
      (make (concat (contents extendable)
                    (contents (padder (- pad-to-n (length extendable)))))))))


(def ^:private empty-line "")
(def ^:private empty-cell (make-cell [empty-line]))

(defn- empty-of [make empty-thing]
  (fn make-n-empty [n] (make (repeat n empty-thing))))

(def ^:private make-empty-cell (empty-of make-cell empty-line))
(def ^:private make-empty-row (empty-of make-row empty-cell))
(def ^:private extend-cell   (extend-with make-cell lines make-empty-cell))
(def ^:private extend-column (extend-with make-row cells make-empty-row))

(defn- mix [sources]
  (case (count sources)
    0 []
    1 (first sources)
    (apply interleave sources)))

(defn- transpose [n things]
  (->> things
       mix
       (partition n)))

(defn- max-counter [select count]
  (fn a-max-count [coll]
    (->> coll select (map count) (apply max))))

(def ^:private max-cell-string-length (max-counter lines count))
(def ^:private max-row-line-count (max-counter cells length))

(defn- pad-cell-lines [cell]
  (->> cell
       lines
       (map (partial pad (max-cell-string-length cell)))
       make-cell))

(defn- row->str
  [row]
  (->> row
       cells
       (map (extend-cell (max-row-line-count row)))
       (map pad-cell-lines)
       (map lines)
       (transpose (length row))
       (map (partial apply str))
       (map s/trimr)
       (s/join "\n")))

(defn- normalize-column-to-width [width column]
  (->> column
       cells
       (map (partial cell-with-width width))))

(defn- max-count [coll] (apply max (map count coll)))

(defn- columns->rows-of-cells [columns]
  {:pre [(sequential? columns)
         (every? sequential? columns)]}
  (->> columns
       (map (partial map (comp make-cell vector)))
       (map make-row)
       (map (extend-column (max-count columns)))))

(defn widths+columns->str [widths columns]
  {:pre [(sequential? widths)
         (sequential? columns)
         (every? integer? widths)
         (= (count widths) (count columns))]}
  (if (or (empty? columns) (some empty? columns))
    ""
    (->> [widths (columns->rows-of-cells columns)]
         (transpose 2)
         (map (partial apply normalize-column-to-width))
         (transpose (count columns))
         (map make-row)
         (map row->str)
         (s/join "\n"))))

(defn- column-width [column]
  (if (empty? column)
    0
    (->> column
         (map str)
         (mapcat #(s/split % #"\n"))
         (max-count))))

(defn- column-widths [total-width columns]
  {:pre [(integer? total-width)
         (sequential? columns)
         (every? sequential? columns)]}
  (if (empty? columns)
    []
    (let [columns (butlast columns)
          widths (vec (map column-width columns))
          last-width (- total-width (apply + widths))]
      (assert (>= last-width 0)
              (str "total width of columns is more than given "
                   \( total-width \)))
      (conj widths last-width))))

(defn columns->str [total-width columns]
  (widths+columns->str (column-widths total-width columns)
                       columns))

(defn table [width & columns] (columns->str width columns))
(defn table-of [width things & selectors]
  (columns->str
    width
    (for [selector selectors]
      (cond (string? selector) (repeat (count things) (str selector))
            :else (map selector things)))))

(def print-table (comp println table))
(def print-table-of (comp println table-of))
