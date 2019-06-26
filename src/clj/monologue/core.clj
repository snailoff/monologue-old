(ns monologue.core
  (:require [clojure.java.io :as io]
            [compojure.api.sweet :refer :all]
            [compojure.coercions :refer :all]
            [compojure.route :as route]
            [monologue.db :refer [db]]
            [monologue.db.monopiece :as monopiece]
            [monologue.db.monouser :as monouser]
            [ring.util.http-response :refer :all]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.reload :refer [wrap-reload]]
            [schema.core :as s])

  (:use [markdown.core]))

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
                  (ok (monouser/all-users db))))

    (context "/piece" []
             :tags ["piece"]

             (GET "/recents" []
                  :summary "최근 piece list 가져오기(id)"
                  (ok (monopiece/recents db {:limit 3})))

             (GET "/:pid" []
                  :path-params [pid :- s/Int]
                  :summary "piece 가져오기"
                  (ok (monopiece/piece-by-id db {:id pid})))


             (POST "/create" []
                   :query-params [content, knot, realday, knotday] 
                   :summary "piece 만들기"
                   (let [rid (monopiece/insert-piece db {:content content 
                                                         :knot knot 
                                                         :realday (if (empty? realday) "20190101" realday) 
                                                         :knotday knotday 
                                                         :changed (java.time.LocalDateTime/now)})] 
                     (ok {:result true
                          :id rid})))

             (PUT "/:pid" []
                  :path-params [pid :- s/Int]
                  :query-params [content, knot, realday, knotday]
                  :summary "piece 바꾸기"
                  (monopiece/update-piece db {:id pid
                                              :content content
                                              :knot knot
                                              :realday realday
                                              :knotday knotday
                                              :changed (java.time.LocalDateTime/now)})
                  (ok {:result true}))

             (PUT "/:pid/knot" []
                  :path-params [pid :- s/Int]
                  :query-params [knot]
                  :summary "knot 바꾸기"
                  (monopiece/update-piece-knot db {:id pid
                                                   :knot knot})
                  (ok {:result true}))


             ))

)

(def rr-app
  (-> #'app 
      wrap-reload 
      (wrap-cors :access-control-allow-origin [#".*"] 
                 :access-control-allow-methods [:get :put :post :delete])
      ))
