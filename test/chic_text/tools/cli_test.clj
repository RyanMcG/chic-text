(ns chic-text.tools.cli-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            (fixturex [core :refer [deftest-fx]]
                      [higher :refer [redefs]])
            [clojure.tools.cli :refer [parse-opts]]
            [chic-text.tools.cli :refer :all]))

(deftest-fx test-summary [(redefs chic-text.terminal/get-terminal-width
                                  (constantly 60))]
  (let [opts [["-h" "--help" "Print this help."]
              ["-t" "--thread-count THREAD_COUNT"
               "The number of threads for the development server to use."
               :default 4]
              [nil "--stuff" "More stuff. Lots of stuff."
               :default {:stuff 1}
               :default-desc "stuff"]
              ["-i" "--in-dir INPUT_DIRECTORY"
               "The directory to get source from"]]]

    (is (= (s/split (:summary (parse-opts [] opts :summary-fn summary)) #"\n")
           ["  -h, --help                      – Print this help."
            "  -t, --thread-count THREAD_COUNT – The number of threads"
            "                                    for the development"
            "                                    server to use. [default:"
            "                                    4]"
            "  --stuff                         – More stuff. Lots of"
            "                                    stuff. [default: stuff]"
            "  -i, --in-dir INPUT_DIRECTORY    – The directory to get"
            "                                    source from"])
      "generates the expected summary")))
