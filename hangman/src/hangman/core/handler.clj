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
                    (GET "/" [] (response {}))
                    (GET "/:uuid" [] (response {}))
                    (POST "/:uuid" [] (response {})))
           (route/not-found (not-found {:message "Not Found"})))

(def app
  (-> app-routes
      wrap-json-response
      ))
