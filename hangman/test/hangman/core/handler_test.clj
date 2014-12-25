(ns hangman.core.handler-test
  (:import (java.util UUID))
  (:use [midje.sweet]
        [ring.mock.request])
  (:require [clojure.data.json :as json]
            [hangman.core.handler :refer :all]))


(defn get-mock-response [method uri]
  (let [response (app (request method uri))]
    (assoc response :body (json/read-str (response :body) :key-fn keyword))))
(defn get-mock-response-param [method uri params]
  (get-in (get-mock-response method uri) params))

(facts "Route status checks"
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
                    404)))

(facts "Response content type check for routes"
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
                    "application/json; charset=utf-8")))

(facts "Post /games "
       (fact "Returns back the uuid of the game started"
             (UUID/fromString (get-in (get-mock-response :post "/games") [:body :game-id]))
             =not=>
             (throws IllegalArgumentException))
       (fact "Returns back a valid uuid of the game started"
             (let [game-uuid (UUID/fromString (get-in (get-mock-response :post "/games") [:body :game-id]))]
               (type game-uuid)
               =>
               UUID))
       (fact "Return the the length of the word selected as long"
             (let [word-length (get-in (get-mock-response :post "/games") [:body :word-length])]
               (type word-length)
               =>
               Long))
       (fact "Return the the word selected for the game as blanks"
             (let [remaining-tries (get-in (get-mock-response :post "/games") [:body :remaining-tries])]
               remaining-tries
               =>
               11)))
