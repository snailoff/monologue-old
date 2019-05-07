(ns monologue.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[monologue started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[monologue has shut down successfully]=-"))
   :middleware identity})
