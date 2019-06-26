-- :name all-piece :? :*
SELECT *
FROM MONO_PIECE

-- :name recents :? :*
SELECT id, changed
FROM MONO_PIECE
ORDER BY changed DESC
LIMIT :limit

-- :name piece-by-id :? :1
SELECT *
FROM MONO_PIECE
WHERE id = :id

-- :name update-piece :!
UPDATE MONO_PIECE
SET content = :content,
    knot = :knot,
    realday = :realday,
    knotday = :knotday,
    changed = :changed
WHERE id = :id

-- :name update-piece-knot :!
UPDATE MONO_PIECE
SET knot = :knot
WHERE id = :id

-- :name insert-piece :<!
INSERT INTO MONO_PIECE (content, knot, realday, knotday, changed)
VALUES (:content, :knot, :realday, :knotday, :changed)
RETURNING id


