(ns hangman.core.games
  (:import (java.util UUID))
  (:use korma.core)
  (:require [hangman.core.entities :as e]))

(def ^:const game-status {:in-progress "IN_PROGRESS" :won "WON" :lost "LOST"})

(defn get-random-word []
  (first (select e/word_dictionary
                 (order (raw "RANDOM()"))
                 (limit 1))))

(defn create-game []
  (let [random-word (get-random-word)
        random-word-id (random-word :word_id)
        random-word-string (clojure.string/replace (random-word :word) #"[a-zA-Z]" ".")]
    (insert e/games
            (values {:game_uuid    (UUID/randomUUID)
                     :word_id      random-word-id
                     :game_status (game-status :in-progress)
                     :tries        0
                     :guessed_word random-word-string})))
  )
