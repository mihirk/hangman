(ns hangman.core.games
  (:import (java.util UUID))
  (:use [korma.core]
        [hangman.core.utils])
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
                     :game_status  (game-status :in-progress)
                     :tries        0
                     :guessed_word (.toLowerCase random-word-string)})))
  )

(defn get-all-games []
  (select e/games))

(defn get-game [game-uuid]
  (first (select e/games (where {:game_uuid game-uuid}))))


(defn replace-char [original-word game guessed-char]
  (let [guessed-word (game :guessed_word)
        original-string-array (clojure.string/split original-word #"")
        guessed-word (clojure.string/split guessed-word #"")
        indices-of-character (indices-of guessed-char original-string-array)
        game (assoc game :guessed_word (apply str (replace-at-indices guessed-char indices-of-character guessed-word)))]
    (if (= (count indices-of-character) 0)
      (assoc game :tries (inc (game :tries)))
      game)
    )
  )


(defn is-won [game word]
  (and (< (game :tries) 11) (= word (game :guessed_word))))

(defn is-lost [game word]
  (< 10 (game :tries)))

(defn get-game-status [game word]
  (let [is-won (is-won game word)
        is-lost (is-lost game word)]
    (if is-won
      (game-status :won)
      (if is-lost
        (game-status :lost)
        (game-status :in-progress))))
  )

(defn save-game [game]
  (let [game-status (game :game_status)
        guessed-word (game :guessed_word)
        tries (game :tries)
        game-id (game :game_id)
        game-uuid (game :game_uuid)
        ]
    (update e/games
            (set-fields {:game_status  game-status
                         :guessed_word guessed-word
                         :tries        tries})
            (where {:game_id   game-id
                    :game_uuid game-uuid}))
    (select e/games (where {:game_id   game-id
                            :game_uuid game-uuid})))
  )

(defn update-game [game word]
  (save-game (assoc game :game_status (get-game-status game word)))
  )

(defn guess-letter [game guessed-char]
  (let [word-id (get game :word_id)
        word (.toLowerCase ((first (select e/word_dictionary (where {:word_id word-id}))) :word))
        guessed-char (.toLowerCase guessed-char)
        game (replace-char word game guessed-char)]
    (update-game game word)
    )
  )

(defn update-game-status [game guessed-char]
  (if (= (game-status :in-progress) (game :game_status))
    (guess-letter game guessed-char)
    (assoc game :message "Game Over")))