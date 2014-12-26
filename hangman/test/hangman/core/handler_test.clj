(ns hangman.core.handler-test
  (:import (java.util UUID))
  (:use [midje.sweet]
        [ring.mock.request]
        [korma.core])
  (:require [clojure.data.json :as json]
            [hangman.core.handler :refer :all]
            [hangman.core.entities :as e]))


(defn get-mock-response [method uri]
  (let [response (app (request method uri))]
    (assoc response :body (json/read-str (response :body) :key-fn keyword))))
(defn get-mock-response-param [method uri get-params]
  (get-in (get-mock-response method uri) get-params))

(facts "Route status checks"
       (with-state-changes [(before :facts (dorun (delete e/games)))
                            (after :facts (dorun (delete e/games)))]
                           (fact "Return 404 for an invalid route"
                                 (get-mock-response-param :get "/invalid-route" [:status])
                                 =>
                                 404)
                           (facts "Don't return a 404 for a"
                                  (fact "Get request on /games"
                                        (get-mock-response-param :get "/games" [:status])
                                        =not=>
                                        404)
                                  (fact "Post request on /games"
                                        (get-mock-response-param :post "/games" [:status])
                                        =not=>
                                        404)
                                  (fact "Get request on /games/id"
                                        (get-mock-response-param :post "/games/params" [:status])
                                        =not=>
                                        404)
                                  (fact "Post request on /games/id"
                                        (get-mock-response-param :post "/games/params" [:status])
                                        =not=>
                                        404))))

(facts "Response content type check for routes"
       (with-state-changes [(before :facts (dorun (delete e/games)))
                            (after :facts (dorun (delete e/games)))]
                           (facts "Return json on route - "
                                  (fact "Post /games"
                                        (get-mock-response-param :post "/games" [:headers "Content-Type"])
                                        =>
                                        "application/json; charset=utf-8")
                                  (fact "Get /games"
                                        (get-mock-response-param :get "/games" [:headers "Content-Type"])
                                        =>
                                        "application/json; charset=utf-8")
                                  (fact "Post /games/uuid"
                                        (get-mock-response-param :post "/games/uuid" [:headers "Content-Type"])
                                        =>
                                        "application/json; charset=utf-8")
                                  (fact "Get /games/uuid"
                                        (get-mock-response-param :get "/games/uuid" [:headers "Content-Type"])
                                        =>
                                        "application/json; charset=utf-8"))))

(facts "Post /games "
       (with-state-changes [(before :facts (dorun (delete e/games)))
                            (after :facts (dorun (delete e/games)))]
                           (fact "Returns back the uuid of the game started"
                                 (UUID/fromString (get-in (get-mock-response :post "/games") [:body :game_uuid]))
                                 =not=>
                                 (throws IllegalArgumentException))
                           (fact "Returns back a valid uuid of the game started"
                                 (let [game-uuid (UUID/fromString (get-in (get-mock-response :post "/games") [:body :game_uuid]))]
                                   (type game-uuid)
                                   =>
                                   UUID))
                           (fact "Return the the guessed word replaced with dots"
                                 (let [guessed-word (get-in (get-mock-response :post "/games") [:body :guessed_word])]
                                   (type guessed-word)
                                   =>
                                   String))
                           (fact "Return the the word selected for the game as blanks"
                                 (let [used-tries (get-in (get-mock-response :post "/games") [:body :tries])]
                                   used-tries
                                   =>
                                   0M))))

(defn create-n-games [n]
  (loop [n n]
    (get-mock-response :post "/games")
    (if (= 1 n)
      nil
      (recur (dec n)))
    ))

(facts "Get /games"
       (with-state-changes [(after :facts (dorun (delete e/games)))]
                           (create-n-games 11)
                           (fact "Returns back a list of all games"
                                 (count (get-in (get-mock-response :get "/games") [:body]))
                                 =>
                                 11)))
(facts "Get a particular game by UUID"
       (with-state-changes [(after :facts (dorun (delete e/games)))]
                           (fact "Return 404 message if game not found"
                                 (get-mock-response-param :get "/games/somerandom" [:status])
                                 =>
                                 410)
                           (fact "Return the game when found"
                                 (let [created-game (get-in (get-mock-response :post "/games") [:body])
                                       fetched-game ((get-mock-response :get (str "/games/" (created-game :game_uuid))) :body)]
                                   (created-game :game_uuid)
                                   =>
                                   (fetched-game :game_uuid)
                                   (created-game :game_id)
                                   =>
                                   (fetched-game :game_id)
                                   ))))
(facts "Guess a letter for a game"
       (with-state-changes [(after :facts (dorun (delete e/games)))]
                           (fact "Return 404 message if game not found"
                                 (get-mock-response-param :post "/games/somerandom" [:status])
                                 =>
                                 410)
                           ))