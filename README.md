# chic-text [![Build Status](https://travis-ci.org/RyanMcG/chic-text.svg?branch=master)](https://travis-ci.org/RyanMcG/chic-text)

Helpful functions for formatting text into tables.
This should be useful for command line applications.

Add to dependencies:

```clojure
[chic-text "0.1.0"]
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

## License

Copyright © 2014 Ryan V McGowan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[source]: https://github.com/RyanMcG/chic-text
[api]: http://www.ryanmcg.com/chic-text/api/
