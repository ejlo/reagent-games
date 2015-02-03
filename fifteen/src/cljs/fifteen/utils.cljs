(ns fifteen.utils)

(defn indexed [coll]
  (map-indexed (fn [idx c] [idx c]) coll))
