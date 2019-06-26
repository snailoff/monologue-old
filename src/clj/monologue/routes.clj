(ns monologue.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            ;;[markdown.core :refer [md-to-html-string]]
            )
  (:use [markdown.core]))


(defroutes rootRoutes
  (GET "/" [] (-> "public/index.html" io/resource slurp))
  (GET "/user" [] 
       (wrap-json-response userhandler))
  (GET "/piece" [con, cmd]
       (db/update! MonoUser 1 :passwd con)
       "ok")

  (route/files "/" {:root "/public"})
  (route/resources "/"))

