(ns hangman.core.entities
  (:use korma.core
        hangman.core.db))

(declare word_dictionary)

(defentity word_dictionary
           (pk :word_id)
           (table :word_dictionary)
           (entity-fields :word_id :word))



