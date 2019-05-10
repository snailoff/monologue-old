(ns monologue.core
  (:require [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            ;;[markdown.core :refer [md-to-html-string]]
            [monologue.models.monouser :refer [MonoUser]]
            [toucan.db :as db])
  (:use [markdown.core]))

(db/set-default-db-connection!
  {:classname "org.postgresql.Driver"
  :subprotocol "postgresql"
  :subname "//localhost:5432/mono"
  :user "snailoff"})

(defn userhandler [request]
  (response {:userid (md-to-html-string (db/select-one-field :passwd MonoUser
                                                           :userid "soso"))}))

(defroutes handler
  (GET "/" [] (-> "public/index.html" io/resource slurp))
  (GET "/user" [] (wrap-json-response userhandler))

  (route/files "/" {:root "/public"})
  (route/resources "/"))


(run-jetty handler {:port 3000})



