(ns chic-text.terminal
  (:require [chic-text.core :refer [table table-of]])
  (:import [jline TerminalFactory]))

(defn get-terminal-width [] (.getWidth (TerminalFactory/create)))

(defn terminal-table [& more] (apply table (get-terminal-width) more))
(defn terminal-table-of [& more] (apply table-of (get-terminal-width) more))

(def print-table (comp println terminal-table))
(def print-table-of (comp println terminal-table-of))
