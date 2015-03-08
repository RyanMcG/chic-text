# chic-text [![Build Status](https://travis-ci.org/RyanMcG/chic-text.svg?branch=master)](https://travis-ci.org/RyanMcG/chic-text)

Helpful functions for formatting text into tables.
This should be useful for command line applications.

Add to dependencies:

```clojure
[chic-text "0.2.0"]
```
###### [**Source**][source]
###### [**API Documentation**][api]

## Example usages

```clojure
(def things [{:name "Cat"
              :desc "Cats are similar in anatomy to the other felids, with strong, flexible bodies, quick reflexes, sharp retractable claws, and teeth adapted to killing small prey."}
             {:name "Dog"
              :desc "The domestic dog (Canis lupus familiaris) is a canid that is known as man's best friend. The dog was the first domesticated animal and has been widely kept as a working, hunting, and pet companion."}])
(require '[chic-text.core :as chic])
(chic/print-table-of 50 things "  " :name " - " :desc) ; prints ↓
```

```
  Cat - Cats are similar in anatomy to the other
        felids, with strong, flexible bodies,
        quick reflexes, sharp retractable claws,
        and teeth adapted to killing small prey.
  Dog - The domestic dog (Canis lupus familiaris)
        is a canid that is known as man's best
        friend. The dog was the first domesticated
        animal and has been widely kept as a
        working, hunting, and pet companion.
```

```clojure
(require '[chic-text.terminal :as term])
;; My terminal's current width is 72 columns
(term/print-table-of things "  " :name " - " :desc) ; prints ↓
```

```
  Cat - Cats are similar in anatomy to the other felids, with strong,
        flexible bodies, quick reflexes, sharp retractable claws, and
        teeth adapted to killing small prey.
  Dog - The domestic dog (Canis lupus familiaris) is a canid that is
        known as man's best friend. The dog was the first domesticated
        animal and has been widely kept as a working, hunting, and pet
        companion.
```

Note the word wrapping.

## [`clojure.tools.cli`][tools.cli] integration

The namespace [`chic-text.tools.cli`][cli] defines functions for interacting with [`clojure.tools.cli`][tools.cli].

### `summary`

Can be used with `parse-opts` from `clojure.tools.cli`.

```clojure
(require '[clojure.tools.cli :refer [parse-opts]])
(require '[chic-text.tools.cli :refer [summary]])

(def opts [["-h" "--help" "Print this help."]
           ["-t" "--thread-count THREAD_COUNT"
            "The number of threads for the development server to use."
            :default 4]
           [nil "--stuff" "More stuff. Lots of stuff."
            :default {:stuff 1}
            :default-desc "stuff"]
           ["-i" "--in-dir INPUT_DIRECTORY"
            "The directory to get source from"]])

(-> (parse-opts [] opts :summary-fn summary)
    :summary
    println)
```

Prints something like:

```
  -h, --help                      – Print this help.
  -t, --thread-count THREAD_COUNT – The number of threads
                                    for the development
                                    server to use. [default:
                                    4]
  --stuff                         – More stuff. Lots of
                                    stuff. [default: stuff]
  -i, --in-dir INPUT_DIRECTORY    – The directory to get
                                    source from
```

## License

Copyright © 2014 Ryan V McGowan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[source]: https://github.com/RyanMcG/chic-text
[api]: http://www.ryanmcg.com/chic-text/api/
[tools.cli]: https://github.com/clojure/tools.cli
[cli]: https://github.com/RyanMcG/chic-text/blob/master/src/chic_text/tools/cli.clj
