(ns chic-text.core-test
  (:require (clojure [string :as s]
                     [test :refer :all])
            (fixturex [context :refer [deftest-ctx
                                       testing-ctx]])
            [chic-text.core :refer :all]))

(deftest test-pad
  (is (= (pad 5 "hey") "hey  "))
  (testing "shorter padding than str"
    (is (= (pad 2 "boom bam bao")
            "boom bam bao"))))

(deftest-ctx test-cell-with-width [:width 10]
  (letfn [(wraps-like [in-s & cell-lines]
            (is (= (lines (cell-with-width width (make-cell [in-s])))
                   cell-lines)))]
    (testing "wrapping a short line"
      (wraps-like "hey"
                  "hey       "))
    (testing "wrapping a long line"
      (wraps-like "hey there more than ten"
                  "hey there "
                  "more than "
                  "ten       "))
    (testing-ctx "an already shorter wrapped line" [:width 4]
      (wraps-like "abcd abcd abc\ne"
                  "abcd"
                  "abcd"
                  "abc "
                  "e   "))
    (testing-ctx "with too small of a width" [:width 5]
      (wraps-like "hey 0123456789"
                  "hey  "
                  "0123456789"))))

(defn- join [& strs] (s/join "\n" strs))
(defn- looks-like [actual & strs]
  (is (= actual (apply join strs))))

(deftest test-table
  (looks-like (table 30
                     ["big" "bigger" "biggest"]
                     [" - " " ~ " " * " " = "]
                     ["something long"
                      "something very long"
                      "something extremely long"])
              "big     - something long"
              "bigger  ~ something very long"
              "biggest * something extremely"
              "          long"
              "        ="))

(deftest-ctx test-table-of [:things [["big" "something long"]
                                     ["bigger" "something very long"]
                                     ["biggest" "something extremely long"]]
                            :page-width 40
                            :tbo (partial table-of page-width things)
                            :table-of-things (partial tbo
                                                      (constantly "  ") first
                                                      " - " second)]
  (looks-like (table-of-things)
              "  big     - something long"
              "  bigger  - something very long"
              "  biggest - something extremely long")
  (testing-ctx "with 1 blank thing" [:things [[]]]
    (looks-like (table-of-things) "   -"))
  (testing-ctx "with 0 things" [:things []]
    (looks-like (table-of-things) "")
    (testing "and 0 selectors"
      (looks-like (tbo) ""))))
