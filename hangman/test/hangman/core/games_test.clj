(ns hangman.core.games-test
  (:use [midje.sweet]
        [korma.core])
  (:require [hangman.core.games :refer :all :as games]
            [hangman.core.entities :refer :all :as e]))

(facts "Create new game"
       (facts "Random word selection"
              (fact "Should only return one word"
                    (let [selected-word-seq (select e/word_dictionary
                                                    (where {:word ((games/get-random-word) :word)}))]
                      (count selected-word-seq)
                      =>
                      1))
              (fact "Selected word should be in the database"
                    (let [selected-word-seq (select e/word_dictionary
                                                    (where {:word ((games/get-random-word) :word)}))]
                      (nil? selected-word-seq)
                      =>
                      false)))
       (facts "")
       )