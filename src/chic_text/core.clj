(ns chic-text.core
  (:require [clojure.string :as s])
  (:import [jline TerminalFactory]))

(defn max-count [coll] (apply max (map count coll)))

(defprotocol ICell
  (lines [this] "Return a sequential collection of lines.")
  (line-count [this] "Return the number of lines in a cell."))

(defprotocol IRow
  (cells [this] "Return a sequential collection of cells that make up the row.")
  (cell-count [this] "Return the number of cells in a row."))

(defrecord Cell [contents]
  ICell
  (lines [this] (:contents this))
  (line-count [this] (count (lines this))))

(defrecord Row [contents]
  IRow
  (cells [this] (:contents this))
  (cell-count [this] (count (cells this))))

(defn make-cell [lines]
  {:pre [(every? string? lines)]}
  (Cell. (vec lines)))

(defn make-row [cells] (Row. (vec cells)))

(defn spaces [n] (apply str (repeat n \space)))
(defn spaces-cell [space-count] (make-cell [(spaces space-count)]))
(defn get-terminal-width [] (.getWidth (TerminalFactory/create)))

(def empty-line "")
(def empty-cell (make-cell [empty-line]))

(defn empty-of [make empty-thing]
  (fn make-n-empty [n] (make (repeat n empty-thing))))

(def make-empty-cell (empty-of make-cell empty-line))
(def make-empty-row (empty-of make-row empty-cell))

(defn pad [padding a-str]
  {:pre [(>= padding (count a-str))]
   :post [(= padding (count %))]}
  (str a-str (spaces (- padding (count a-str)))))

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

(defn make-cell-with-width [width content]
  (make-cell
    (->> content
         lines
         (mapcat #(s/split % #"\n"))
         (map (partial wrap-line-to-width width))
         (apply concat)
         (map (partial pad width)))))

(defn- max-counter [select count]
  (fn a-max-count [coll]
    (->> coll select (map count) (apply max))))

(defn- extend-with [make count contents padder]
  (fn pad-up-to [pad-to-n]
    (fn extend-coll [extendable]
      (assert (>= pad-to-n (count extendable))
              "padding count must be greater than or equal to the extendable's count")
      (make (concat (contents extendable)
                    (contents (padder (- pad-to-n (count extendable)))))))))

(def ^:private extend-cell   (extend-with make-cell line-count lines
                                          make-empty-cell))
(def ^:private extend-column (extend-with make-row cell-count cells
                                          make-empty-row))

(defn- transpose [n things]
  (->> things
       (apply interleave)
       (partition n)))

(def max-cell-string-length (max-counter lines count))
(def max-row-line-count (max-counter cells line-count))

(defn pad-cell-lines [cell]
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
       (transpose (cell-count row))
       (map (partial apply str))
       (map s/trimr)
       (s/join "\n")))

(def things [{:name "hjjjjjjjjj"
              :description "kasdjkas dkj sa"}
             {:name "batman"
              :description "Hye this is nto a v jdkf dkfjfkd jf sdkjf skj fjkdsjkfjfjsk fjk sdkf sdk fks f sdkf jsdfksjdf ksjd jfd ksf js fk sdj"}])

(defn- normalize-column-to-width [width column]
  (->> column
       cells
       (map (partial make-cell-with-width width))))

(defn widths+columns->str [widths columns]
  {:pre [(= (count widths) (count columns))
         (every? integer? widths)
         (every? (partial every? string?) columns)]}
  (->> [widths (->> columns
                    (map (partial map (comp make-cell vector)))
                    (map make-row)
                    (map (extend-column (max-count columns))))]
       (transpose 2)
       (map (partial apply normalize-column-to-width))
       (transpose (count columns))
       (map make-row)
       (map row->str)
       (s/join "\n")))

(defn column-width [column]
  (->> column
       (mapcat #(s/split % #"\n"))
       (max-count)))

(defn- column-widths [total-width columns]
  (let [columns (butlast columns)
        widths (vec (map column-width columns))
        last-width (- total-width (apply + widths))]
    (assert (>= last-width 0)
            (str "total width of columns is more than given "
                 \( total-width \)))
    (conj widths last-width)))

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

(defn terminal-table [& more] (apply table (get-terminal-width) more))
(defn terminal-table-of [& more] (apply table-of (get-terminal-width) more))
(def print-table (comp println terminal-table))
(def print-table-of (comp println terminal-table-of))
