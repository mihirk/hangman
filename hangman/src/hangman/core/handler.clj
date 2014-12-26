(ns hangman.core.handler
  (:use ring.middleware.json)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hangman.core.games :as games]
            [ring.util.response :refer [response not-found]]))

(defroutes app-routes
           (context "/games" []
                    (POST "/" [] {:status 201
                                  :body   (games/create-game)})
                    (GET "/" [] {:status 200
                                 :body   (games/get-all-games)})
                    (GET "/:uuid" [uuid] (let [game (games/get-game uuid)]
                                           (if (nil? game)
                                             {:status 410
                                              :body   {:message (str uuid " is not a valid game id")}}
                                             {:status 200
                                              :body   game})))
                    (POST "/:uuid" {params :params body :body} (println params)
                          (let [uuid (:uuid params)
                                game (games/get-game uuid)
                                guessed-char (get params "char" nil)]
                            (if (or (nil? game) (nil? guessed-char))
                              {:status 410
                               :body   {:message (str uuid " is not a valid game id or parameter")}}
                              {:status 200
                               :body   (games/update-game-status game guessed-char)}))))
           (route/not-found (not-found {:message "Not Found"})))

(def app
  (-> app-routes
      wrap-json-response
      wrap-json-params
      ))
