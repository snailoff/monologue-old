(ns monologue.core
  (:require [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            ;;[markdown.core :refer [md-to-html-string]]
            [monologue.models :refer [mono-user, mono-piece]]
            [toucan.db :as db])
  (:use [markdown.core]))

(db/set-default-db-connection!
  {:classname "org.postgresql.Driver"
  :subprotocol "postgresql"
  :subname "//localhost:5432/mono"
  :user "snailoff"})


(defn userhandler [request]
  (println "*** userhandler ***")
  (response {:userid (md-to-html-string (db/select-one-field :passwd mono-user
                                                           :userid "soso"))}))

(defroutes rootRoutes
  (GET "/" [] (-> "public/index.html" io/resource slurp))

  (GET "/user" [] (wrap-json-response userhandler))

  (GET "/piece/:piece-id" [piece-id] (db/select-one-field :content mono-piece 
                                                          :id piece-id))
  (PUT "/piece/:piece-id" [piece-id, con, knotname] (db/update! mono-user piece-id :passwd con) "ok")

  (GET "/favicon.ico" [] "not yet")

  (route/files "/" {:root "/public"})
  (route/resources "/"))

(def app rootRoutes)

(run-jetty app {:port 3000})



