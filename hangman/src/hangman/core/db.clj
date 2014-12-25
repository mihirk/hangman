(ns hangman.core.db
  (:use korma.db)
  (:require [environ.core :refer [env]]))

(defdb db (postgres {:db       (get env :hangman-db "hangman")
                     :user     (get env :hangman-db-user "hangman")
                     :password (get env :hangman-db-pass "")
                     :host     (get env :hangman-db-host "localhost")
                     :port     (get env :hangman-db-port 5432)}))

