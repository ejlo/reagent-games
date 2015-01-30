(ns tic-tac-toe.utils)

(defn indexed [coll]
  (map-indexed (fn [idx c] [idx c]) coll))
