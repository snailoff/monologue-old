(ns monologue.db
    (:require [hugsql.core :as hugsql]))

(def db
    {:subprotocol "postgresql"
     :subname "//localhost:5432/mono"
     :classname "org.postgresql.Driver"
     :username "snailoff"
     })

