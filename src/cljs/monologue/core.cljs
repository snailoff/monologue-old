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
  (reagent/atom {:piece {:id 1}
                 :recents []
                 }))



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

  (defroute "/p/:pid" {pid :pid}
    (swap! app-state assoc :page :mono :pid pid))


  (hook-browser-navigation!))



(defn monourl [qs]
  (str "http://c.trunkcat.com:3000/" qs))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Pages

(defn mono [ratom]
    (reagent/create-class
      {:component-will-mount (fn []
                              (go (let [response (<! (http/get (monourl (str "piece/1" (:id @ratom)))
                                                               {:with-credentials? false}))] 
                                    (swap! app-state 
                                           assoc :piece (:body response))))

                              (go (let [response (<! (http/get (monourl "piece/recents")
                                                               {:with-credentials? false}))]
                                    (swap! app-state
                                           assoc :recents (:body response)))))

       :reagent-render (fn [] 
                         [:div 
                           [:h1 "monologue"]
                           ;;[:div (mono-body (:piece @ratom))]
                           (mono-body (:piece @ratom))
                           (mono-recents (:recents @ratom))
                         ]
                       )}))

(defn mono-body [piece]
  [:div 
   [:p (str "hi. " (:realday piece))]
  ])

(defn mono-recents [recents]
  [:ul
   (for [piece recents]
;;     [:li {:key (:id piece)} (:changed  piece)]
     [:li "hehehehe1111" ])])


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
