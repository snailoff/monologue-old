(ns monologue.core
  (:require-macros [cljs.core.async.macros :refer [go]] 
                   [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require
    [cljs-http.client :as http] 
    [cljs.core.async :refer [<!]]
    [secretary.core :as secretary] 
    [goog.events :as events] 
    [goog.history.EventType :as EventType] 
    [reagent.core :as reagent]))

(enable-console-print!)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state
  (reagent/atom {:knot ""
                 :summary ""
                 :content ""}))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Routes

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (swap! app-state assoc :page :mono))

  ;; add routes here


  (hook-browser-navigation!))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Pages

(defn mono [ratom]
    (reagent/create-class
      {:component-will-mount (fn []
                              (go (let [response (<! (http/get "http://121.134.146.100:3000/user"))]
                                    (swap! app-state assoc :knot (:userid (:body response))))))
       :reagent-render (fn [] 
                         [:div 
                          [:h1 "hi. monologue."] 
                          [:div {:dangerouslySetInnerHTML {:__html (:knot @ratom)}}]])
                         })) 



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defmulti page identity)
(defmethod page :mono [] mono)
(defmethod page :default [] (fn [_] [:div]))

(defn current-page [ratom]
  (let [page-key (:page @ratom)]
    [(page page-key) ratom]))

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    ))

(defn reload []
  (reagent/render [current-page app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (app-routes)
  (reload))
