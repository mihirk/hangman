(ns hangman.core.utils)

(defn seq-contains? [coll target]
  (some #(= target %) coll))

(defn indices-of [char char-coll]
  (keep-indexed #(if (= char %2) %1) char-coll))

(defn replace-at-indices [char indices-of-character char-coll]
  (keep-indexed #(if (seq-contains? indices-of-character %1) char %2) char-coll))


(defn substring [txt sub]
  (re-seq (re-pattern sub) txt))

(defn is-substring [txt sub]
  (not (nil? (substring txt sub))))