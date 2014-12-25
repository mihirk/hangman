(ns hangman.core.games-test
  (:use [midje.sweet])
  (:require [hangman.core.games :refer :all :as games]))

(facts "Create new game"
       (fact "Select Random Word from dictionary"
             (type (games/get-random-word))
             =>
             String))