(ns hangman.core.games-test
  (:import (java.util UUID))
  (:use [midje.sweet]
        [korma.core])
  (:require [hangman.core.games :refer :all :as games]
            [hangman.core.entities :refer :all :as e]))


(defn count-substring [txt sub]
  (count (re-seq (re-pattern sub) txt)))

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
       (facts "Initiate new game with random word"
              (fact "Assign new game a UUID"
                    (type (UUID/fromString (get (create-game) :game_uuid)))
                    =>
                    UUID)
              (fact "Assign new game a valid word id"
                    (let [word-id (get (create-game) :word_id)]
                      (empty? (select e/word_dictionary (where {:word_id word-id})))
                      =>
                      false))
              (fact "Give 11 tries for a new game"
                    (let [no-tries (get (create-game) :tries)]
                      no-tries
                      =>
                      0M))
              (fact "Replace all the characters of the word with dots in guessed word column"
                    (let [game (create-game)
                          guessed-word (game :guessed_word)
                          actual-word ((first (select e/word_dictionary (where {:word_id (game :word_id)}))) :word)]
                      (count guessed-word)
                      =>
                      (count actual-word)

                      (count actual-word)
                      =>
                      (count-substring guessed-word ".")
                      ))
              (fact "Set Game status to in progress"
                    (let [game-status (get (create-game) :game_status)]
                      game-status
                      =>
                      "IN_PROGRESS")))
       )