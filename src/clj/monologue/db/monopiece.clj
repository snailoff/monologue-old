(ns monologue.db.monopiece
    (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "monologue/db/sql/monopiece.sql")
(hugsql/def-sqlvec-fns "monologue/db/sql/monopiece.sql")

