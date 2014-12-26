(ns hangman.core.utils-test
  (:use [midje.sweet])
  (:require [hangman.core.utils :refer :all :as utils]))

(facts "Check whether a given sequence contains the given element of not"
       (fact "Return nil for nil input"
             (utils/seq-contains? nil nil)
             =>
             nil)
       (fact "Return nil for nil and empty input"
             (utils/seq-contains? [] nil)
             =>
             nil)
       (fact "Return nil for wrong input"
             (utils/seq-contains? [] [])
             =>
             nil)
       (fact "Return nil for no matching target in collection"
             (utils/seq-contains? [1 2 3] 4)
             =>
             nil)
       (fact "Return true for single matching target in collection"
             (utils/seq-contains? [1 2 3 4] 4)
             =>
             true)
       (fact "Return true for multiple matching target in collection"
             (utils/seq-contains? [1 2 3 4] 4)
             =>
             true))

(facts "Get all indices of an element in a collection"
       (fact "Return empty for nil"
             (count (utils/indices-of nil nil))
             =>
             0)
       (fact "Return empty for no match"
             (count (utils/indices-of "H" ["M" "K" "C"]))
             =>
             0)
       (fact "Return indices for multiple occurances"
             (utils/indices-of "H" ["H" "E" "H"])
             =>
             '(0 2)))

(facts "Replace elements at given indices from a collection with the given value"
       (fact "Return empty for nil"
             (count (utils/replace-at-indices nil nil nil))
             =>
             0)
       (fact "Return default collection for empty index collection"
             (utils/replace-at-indices nil '() '(1 2 3))
             =>
             '(1 2 3))
       (fact "Return default collection for empty index collection"
             (utils/replace-at-indices 4 '() '(1 2 3))
             =>
             '(1 2 3))
       (fact "Return default collection if no match of index found"
             (utils/replace-at-indices 4 '(4) '(1 2 3))
             =>
             '(1 2 3))
       (fact "Replace the given element at the given index"
             (utils/replace-at-indices 4 '(0) '(1 2 3))
             =>
             '(4 2 3))
       (fact "Replace the given element at the given index"
             (utils/replace-at-indices 4 '(0 1 2) '(1 2 3))
             =>
             '(4 4 4))
       )