(ns chic-text.core
  (:require [clojure.string :as s])
  (:import [jline TerminalFactory]))

(defn spaces [n] (apply str (repeat n \space)))
(defn spaces-cell [space-count] [(spaces space-count)])
(defn spaces-col [space-count] [(spaces-cell space-count)])
(defn get-terminal-width [] (.getWidth (TerminalFactory/create)))

(defn pad [padding a-str]
  {:pre [(>= padding (count a-str))]
   :post [(= padding (count %))]}
  (str a-str (spaces (- padding (count a-str)))))

(defn wrap-to-width
  "Turn a string into a vector of strings where each string is less than or
  equal to the given column limit."
  [width a-str]
  {:pre [(string? a-str)]}
  (let [words (s/split a-str #"\s+")]
    (loop [line (first words)
           words (next words)
           lines (transient [])]
      (if words
        (let [nword (first words)]
          (if (<= (+ (count line) (count nword)) width)
            (recur (str line \space nword)
                   (next words)
                   lines)
            (recur nword
                   (next words)
                   (conj! lines line))))
        (persistent! (conj! lines line))))))

(defn max-count [coll] (->> coll (map count) (apply max)))

(defn- extend-with [padding-fn width-from-coll]
  (fn pad-up-to [num-pads]
    (fn extend-coll [coll]
      (concat coll
              (repeat (- num-pads (count coll))
                      (padding-fn (width-from-coll coll)))))))

(def ^:private extend-cell   (extend-with spaces max-count))
(def ^:private extend-column (extend-with spaces-cell
                                          (fn max-width-in-column [col]
                                            (apply max (map max-count col)))))

(defn- transpose [coll]
  (let [size (count coll)]
    (if (> size 1)
      (->> coll
           (apply interleave)
           (partition (count coll)))
      coll)))

(defn pad-cell [cell]
  (let [max-line-len (max-count cell)]
    (map (partial pad max-line-len) cell)))

(defn- row-cells->str
  [cells]
  (->> cells
       (map pad-cell)
       (map (extend-cell (max-count cells)))
       (transpose)
       (map (partial apply str))
       (map s/trimr)
       (s/join "\n")))

(defn columns->str [& cols]
  "Takes an arbitrary number of colls (each representing one column)."
  (->> cols
       (map (extend-column (max-count cols)))
       (transpose)
       (map row-cells->str)
       (s/join "\n")))
