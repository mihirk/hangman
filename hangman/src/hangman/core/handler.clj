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
                                             (not-found {:message (str uuid " is not a valid game id")})
                                             {:status 200
                                              :body   game})))
                    (POST "/:uuid" [] (response {})))
           (route/not-found (not-found {:message "Not Found"})))

(def app
  (-> app-routes
      wrap-json-response
      ))
