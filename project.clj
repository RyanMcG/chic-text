(defproject chic-text "0.2.0"
  :description "Helpful functions for formatting text into tables."
  :url "https://github.com/RyanMcG/chic-text"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [jline "2.12"]]
  :profiles {:dev {:plugins [[lein-repack "0.2.7"]]
                   :dependencies [[incise "0.5.0"]
                                  [com.ryanmcg/incise-codox "0.2.0"]
                                  [com.ryanmcg/incise-vm-layout "0.5.0"]]
                   :aliases {"incise" ^:pass-through-help ["run" "-m" "incise.core"]}}
             :test {:dependencies [[org.clojure/tools.cli "0.3.1"]
                                   [fixturex "0.3.0"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0-alpha5"]]}}
  :repack [{:type :clojure
            :levels 2
            :path "src"
            :standalone #{"core"}}])
