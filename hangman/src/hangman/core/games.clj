(ns hangman.core.games
  (:use korma.core)
  (:require [hangman.core.entities :as e]))

(defn create-game []

  )

(defn get-random-word []
  (first (select e/word_dictionary
                 (order (raw "RANDOM()"))
                 (limit 1))))
