(defproject hangman "0.1.0-SNAPSHOT"
            :description "Hangman"
            :url "https://github.com/mihirk/hangman"
            :min-lein-version "2.0.0"
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [compojure "1.3.1"]
                           [midje "1.6.3"]
                           [ring "1.3.2"]
                           [ring/ring-json "0.3.1"]
                           [org.clojure/data.json "0.2.5"]
                           [environ "1.0.0"]
                           [postgresql "9.3-1102.jdbc41"]
                           [ragtime "0.3.8"]
                           [korma "0.4.0"]]
            :plugins [[lein-ring "0.8.13"]
                      [lein-midje "3.1.3"]
                      [lein-environ "1.0.0"]
                      [ragtime/ragtime.lein "0.3.8"]]
            :ring {:handler hangman.core.handler/app}
            :ragtime {:migrations ragtime.sql.files/migrations
                      :database   ~(System/getenv "HANGMAN_DB_URL")}
            :profiles
            {
             :dev  {
                    :dependencies [[javax.servlet/servlet-api "2.5"]
                                   [ring-mock "0.1.5"]]
                    :ragtime      {:database "jdbc:postgresql://localhost:5432/hangman?user=hangman&password=hangman"}
                    :env          {:handman-db      "hangman"
                                   :hangman-db-user "hangman"
                                   :hangman-db-pass "pass_dev"}}
             :test {:ragtime {:database "jdbc:postgresql://localhost:5432/hangman?user=hangman&password=hangman"}
                    :env     {:hangman-db      "hangman"
                              :hangman-db-user "hangman"
                              :hangman-db-pass "hangman"}}
             })
