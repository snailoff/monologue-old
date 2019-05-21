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
  (:use [markdown.core]
        [ring.middleware.params :only [wrap-params]]))

(db/set-default-db-connection!
  {:classname "org.postgresql.Driver"
  :subprotocol "postgresql"
  :subname "//localhost:5432/mono"
  :user "snailoff"})

;;(defn pieceUpdateHandler [{:keys [con]}]
(defn pieceUpdateHandler [request]
  (println "*** pieceUpdateHandler! ***")
  (println (str "con:" (:params request)))
  (response {:success (db/update! MonoUser 1 :passwd (get (:params request) "con"))}))

(defn userhandler [request]
  (println "*** userhandler ***")
  (response {:userid (md-to-html-string (db/select-one-field :passwd MonoUser
                                                           :userid "soso"))}))

(defroutes rootRoutes
  (GET "/" [] (-> "public/index.html" io/resource slurp))
  (GET "/user" [] 
       (wrap-json-response userhandler))
  (GET "/piece" []
       (wrap-json-response pieceUpdateHandler))

  (route/files "/" {:root "/public"})
  (route/resources "/"))

(def app (wrap-params rootRoutes))

(run-jetty app {:port 3000})



