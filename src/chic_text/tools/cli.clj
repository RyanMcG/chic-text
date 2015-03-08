(ns chic-text.tools.cli
  "Useful functions for making clojure.tools.cli chic."
  (:require [chic-text.terminal :refer [terminal-table-of]]))

(defn summary
  "Takes specs like those defined by #'clojure.tools.cli/compile-option-specs.
  This can be used as a summary-fn for #'clojure.tools.cli/parse-opts."
  [specs]
  (terminal-table-of specs
                     "  "
                     (fn [{:keys [short-opt long-opt required]}]
                       (str (if short-opt (str short-opt ", "))
                            long-opt
                            (if required (str " " required))))
                     " – "
                     (fn [{:keys [desc validate-msg default default-desc]}]
                       (str desc
                            (if validate-msg (str " " validate-msg))
                            (if default (str " [default: "
                                             (or default-desc default)
                                             \]))))))
