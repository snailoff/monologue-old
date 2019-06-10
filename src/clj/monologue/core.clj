(ns monologue.core
  (:require [clojure.java.io :as io]
            [compojure.api.sweet :refer :all]
            [compojure.coercions :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer :all]
            [ring.middleware.reload :refer [wrap-reload]]
            [schema.core :as s]
            [monologue.models :refer [MonoUser, MonoMain]]
            [toucan.db :as db])
  (:use [markdown.core]))

(db/set-default-db-connection!
  {:classname "org.postgresql.Driver"
  :subprotocol "postgresql"
  :subname "//localhost:5432/mono"
  :user "snailoff"})


(def swagger-config
  {:ui "/swagger"
   :spec "/swagger.json"
   :options {:ui {:validatiorUrl nil}
             :data {:info {:version "1.0.0", :title "Monologue API"}}}})

(def app
  (api 
    {:swagger swagger-config} 
    (undocumented 
      (GET "/" [] (clojure.string/replace (-> "public/index.html" io/resource slurp) 
                                          "RANDOM" 
                                          (str (rand 100))))

      (GET "/favicon.ico" [] (ok {:result "ico"}))
      (route/files "/" {:root "/public"}) 
      (route/resources "/"))

    (context "/user" []
             :tags ["user"]
             (GET "/" [] 
                  :summary "내 정보 가져옴...."
                  (ok (db/select-one MonoUser :userid "soso"))))

    (context "/piece" []
             :tags ["piece"]
             (POST "/create" []
                   :query-params [realday, knotday, content] 
                   :summary "piece 만들기"
                   (db/insert! MonoMain 
                               :content content
                               :realday (if (empty? realday) "20190101" realday)
                               :knotday knotday 
                               :changed (java.time.LocalDateTime/now)) 
                   (ok {:result true}))

             (PUT "/:pid" []
                  :path-params [pid :- s/Int] 
                  :query-params [realday :- String, knotday :- String, {content :- String ""}] 
                  :summary "piece 바꾸기"
                  (db/update! MonoMain pid 
                              :content content 
                              :realday realday 
                              :knotday knotday 
                              :changed (java.time.LocalDateTime/now))
                  (ok {:result true}))
             (PUT "/:pid/knot" []
                  :path-params [pid :- s/Int] 
                  :query-params [knotday]
                  :summary "piece 바꾸기"
                  (db/update! MonoMain pid 
                              :knotday knotday 
                              :changed (java.time.LocalDateTime/now))
                  (ok {:result true}))

             (GET "/any" []
                  :summary "어떤 piece 가져오기"
                  (ok (db/select-one MonoMain :id 2)))

             (GET "/:pid" []
                  :path-params [pid :- s/Int]
                  :summary "piece 가져오기"
                  (ok (db/select-one MonoMain :id pid)))

             (DELETE "/:pid" []
                     :path-params [pid :- s/Int]
                     :summary "piece 지우기"
                     (ok (db/delete! MonoMain :id pid)))
             
             )))

(def rr-app
  (wrap-reload #'app))
