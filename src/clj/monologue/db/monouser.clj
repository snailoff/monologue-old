(ns monologue.db.monouser
    (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "monologue/db/sql/monouser.sql")
(hugsql/def-sqlvec-fns "monologue/db/sql/monouser.sql")

