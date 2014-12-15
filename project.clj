(defproject chic-text "0.1.0"
  :description "Helpful functions for formatting text into tables."
  :url "https://github.com/RyanMcG/chic-text"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [jline "2.12"]]
  :profiles {:dev {:plugins [[lein-repack "0.2.5"]]}
             :test {:dependencies [[fixturex "0.2.1"]]}}
  :repack [{:type :clojure
            :levels 1
            :path "src"
            :standalone #{"core"}}])
