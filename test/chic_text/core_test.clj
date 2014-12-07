(ns chic-text.core-test
  (:require (clojure [string :as s]
                     [test :refer :all])
            (fixturex [context :refer [deftest-ctx
                                       testing-ctx]])
            [chic-text.core :refer :all]))

(def ex-columns
  [[[" "]]
   [["a"]
    ["e"
     "f"]]
   [["  "]]
   [["b"
     "c"
     "d"]
    ["g "]]])

(defn- join [& lines] (s/join "\n" lines))

(def ^:private ex-text
  (join "a  b"
        "   c"
        "   d"
        "e  g"
        "f"))

(deftest test-pad
  (is (= (pad 5 "hey") "hey  "))
  (testing "shorter padding than str"
    (is (thrown? AssertionError (pad 2 "boom bam bao")))))

(deftest-ctx test-wrap-to-width [:width 10]
  (letfn [(wraps-like [in-s cell-lines]
            (is (= (lines (wrap-to-width width in-s)) cell-lines)))]
    (testing "wrapping a short line"
      (wraps-like "hey" ["hey"]))
    (testing "wrapping a long line"
      (wraps-like "hey there more than ten"
                  ["hey there" "more than" "ten"]))
    (testing "an already shorter wrapped line"
      (wraps-like ex-text
                  ["a  b" "   c" "   d" "e  g" "f"]))))
