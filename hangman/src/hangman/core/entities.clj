(ns hangman.core.entities
  (:use korma.core
        hangman.core.db))

(declare word_dictionary games)

(defentity word_dictionary
           (pk :word_id)
           (table :word_dictionary)
           (entity-fields :word_id :word))

(defentity games
           (pk :game_id)
           (table :games)
           (belongs-to word_dictionary {:fk :word_id})
           (entity-fields
             :game_id :game_uuid :word_id :game_status
             :tries :guessed_word))