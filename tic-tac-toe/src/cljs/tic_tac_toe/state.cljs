(ns tic-tac-toe.state
    (:require [reagent.core :as reagent]))

(defonce empty-board
  [[nil nil nil]
   [nil nil nil]
   [nil nil nil]])

(defonce init-state {:board empty-board
                     :status :playing
                     :to-move :O
                     :winner nil})

(defonce app-state (reagent/atom init-state))

(defonce winner-rows
  (for [[[y x] [dy dx]] [[[0 0] [1 0]] [[0 1] [1 0]] [[0 2] [1 0]]
                         [[0 0] [0 1]] [[1 0] [0 1]] [[2 0] [0 1]]
                         [[0 0] [1 1]] [[2 0] [-1 1]]]]
    (for [n (range 3)]
      [(+ y (* n dy)) (+ x (* n dx))])))

(defn winners [board]
  (->> winner-rows
       (map #(map (fn [[y x]] (get-in board [y x])) %))
       (map set)
       (filter #(= 1 (count %)))
       (map first)
       (remove nil?)
       set))

(defn winner [board]
  (let [winner-set (winners board)]
    (if (empty? winner-set)
      (when (= 9 (count (remove nil? (flatten board))))
        :draw)
       (if (> (count winner-set) 1)
        :draw
        (first winner-set)))))


(defn get-state
  ([cursor]
     (get-in @app-state cursor))
  ([cursor default]
     (get-in @app-state cursor default)))

(defn put! [cursor v]
  (swap! app-state assoc-in cursor v))

(defn update! [cursor f & args]
  (apply swap! app-state update-in cursor f args))

(defn reset-state! [s]
  (reset! app-state s))

(defn clean-state! []
  (reset-state! init-state))
