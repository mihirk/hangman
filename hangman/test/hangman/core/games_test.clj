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
              (with-state-changes [(after :facts (dorun (delete e/games)))]
                                  (defonce test-game (create-game))
                                  (fact "A row was added to the games table"
                                        (count (select e/games))
                                        =>
                                        1)
                                  (fact "Assign new game a UUID"
                                        (type (UUID/fromString (get test-game :game_uuid)))
                                        =>
                                        UUID)
                                  (fact "Assign new game a valid word id"
                                        (let [word-id (get test-game :word_id)]
                                          (empty? (select e/word_dictionary (where {:word_id word-id})))
                                          =>
                                          false))
                                  (fact "Give 11 tries for a new game"
                                        (let [no-tries (get test-game :tries)]
                                          no-tries
                                          =>
                                          0M))
                                  (fact "Replace all the characters of the word with dots in guessed word column"
                                        (let [game test-game
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
                                        (let [game-status (get test-game :game_status)]
                                          game-status
                                          =>
                                          "IN_PROGRESS"))))
       )

(defn create-n-games [n]
  (loop [n n]
    (create-game)
    (if (= 1 n)
      nil
      (recur (dec n)))
    ))

(facts "Get all games"
       (with-state-changes [(after :facts (dorun (delete e/games)))]
                           (create-n-games 11)
                           (fact "Get equal number of games as created"
                                 (count (get-all-games))
                                 =>
                                 11)
                           (fact "Return 0 if no games are created"
                                 (count (get-all-games))
                                 =>
                                 0)))