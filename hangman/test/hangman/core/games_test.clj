(ns hangman.core.games-test
  (:import (java.util UUID))
  (:use [midje.sweet]
        [korma.core]
        [hangman.core.utils])
  (:require [hangman.core.games :refer :all :as games]
            [hangman.core.entities :refer :all :as e]))


(defn count-substring [txt sub]
  (count (substring txt sub)))


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

(facts "Get particular game"
       (with-state-changes [(after :facts (dorun (delete e/games)))]
                           (fact "Get game by uuid"
                                 (let [test-game (create-game)
                                       fetched-game (get-game (test-game :game_uuid))]
                                   (fetched-game :game_id)
                                   =>
                                   (test-game :game_id)
                                   (fetched-game :word_id)
                                   =>
                                   (test-game :word_id)
                                   (fetched-game :tries)
                                   =>
                                   (test-game :tries)
                                   (fetched-game :game_status)
                                   =>
                                   (test-game :game_status)
                                   (fetched-game :game_uuid)
                                   =>
                                   (test-game :game_uuid))
                                 )
                           (fact "Return nil if game not found"
                                 (get-game "random string")
                                 =>
                                 nil)))

(facts "Replace characters in the guessed word with new character"
       (fact "Don't replace anything if match is not found and increment tries"
             (replace-char "Hello World" {:guessed_word "..ll. ...l." :tries 2} "i")
             =>
             {:guessed_word "..ll. ...l." :tries 3})
       (fact "Replace all occurances replace anything if match is not found"
             (replace-char "Hello World" {:guessed_word "..ll. ...l." :tries 2} "o")
             =>
             {:guessed_word "..llo .o.l." :tries 2})
       (fact "Keep default as default"
             (replace-char "Hello World" {:guessed_word "Hello World" :tries 2} "o")
             =>
             {:guessed_word "Hello World" :tries 2}))

(facts "Game is won"
       (fact "Less than 11 tries and word is equal"
             (is-won {:tries 10 :guessed_word "ok mister"} "ok mister")
             =>
             true)
       (fact "11 tries and word is equal"
             (is-won {:tries 11 :guessed_word "ok mister"} "ok mister")
             =>
             false)
       (fact "12 tries and word is equal"
             (is-won {:tries 12 :guessed_word "ok mister"} "ok mister")
             =>
             false)
       (fact "12 tries and word is not equal"
             (is-won {:tries 12 :guessed_word "ok mister"} "okister")
             =>
             false)
       (fact "2 tries and word is not equal"
             (is-won {:tries 2 :guessed_word "ok mister"} "okister")
             =>
             false)
       (fact "2 tries and word is equal"
             (is-won {:tries 2 :guessed_word "ok mister"} "ok mister")
             =>
             true))

(facts "Game is lost"
       (fact "Less than 11 tries and word is equal"
             (is-lost {:tries 10 :guessed_word "ok mister"} "ok mister")
             =>
             false)
       (fact "11 tries and word is equal"
             (is-lost {:tries 11 :guessed_word "ok mister"} "ok mister")
             =>
             true)
       (fact "12 tries and word is equal"
             (is-lost {:tries 12 :guessed_word "ok mister"} "ok mister")
             =>
             true)
       (fact "12 tries and word is not equal"
             (is-lost {:tries 12 :guessed_word "ok mister"} "okister")
             =>
             true)
       (fact "2 tries and word is not equal"
             (is-lost {:tries 2 :guessed_word "ok mister"} "okister")
             =>
             false)
       (fact "2 tries and word is equal"
             (is-lost {:tries 2 :guessed_word "ok mister"} "ok mister")
             =>
             false))

(facts "Get game status"
       (fact "Less than 11 tries and word is equal"
             (get-game-status {:tries 10 :guessed_word "ok mister"} "ok mister")
             =>
             "WON")
       (fact "11 tries and word is equal"
             (get-game-status {:tries 11 :guessed_word "ok mister"} "ok mister")
             =>
             "LOST")
       (fact "12 tries and word is equal"
             (get-game-status {:tries 12 :guessed_word "ok mister"} "ok mister")
             =>
             "LOST")
       (fact "12 tries and word is not equal"
             (get-game-status {:tries 12 :guessed_word "ok mister"} "okister")
             =>
             "LOST")
       (fact "2 tries and word is not equal"
             (get-game-status {:tries 2 :guessed_word "ok mister"} "okister")
             =>
             "IN_PROGRESS")
       (fact "2 tries and word is equal"
             (get-game-status {:tries 2 :guessed_word "ok mister"} "ok mister")
             =>
             "WON"))

(facts "")