(ns hangman.core.handler
  (:use [ring.middleware.json]
        [hangman.core.utils])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hangman.core.games :as games]
            [ring.util.response :refer [response not-found]]))

(defn is-valid? [guessed-char] (size1 guessed-char))


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
                    (POST "/:uuid" {params :params body :body}
                          (let [uuid (get params :uuid)
                                game (games/get-game uuid)
                                guessed-char (get body "char" nil)]
                            (if (or (nil? game) (not (is-valid? guessed-char)))
                              {:status 410
                               :body   {:message (str uuid " is not a valid game id or parameter")}}
                              {:status 201
                               :body   (games/update-game-status game guessed-char)}))))
           (route/not-found (not-found {:message "Not Found"})))


(def app
  (-> app-routes
      wrap-json-body
      wrap-json-response
      ))
