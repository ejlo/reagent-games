(ns fifteen.state
  (:require [reagent.core :as reagent]
            [reagent.cursor :as rc]))

(defonce finished-tiles
  (vec (take 15 (map vector
                     (iterate (comp #(mod % 4) inc) 0)
                     (mapcat #(list % % % %) (iterate inc 0))))))

(defonce init-state {:board  {:tiles finished-tiles
                              :empty [3 3]}
                     :status :finished
                     :moves  nil
                     :test   nil})

(defonce app-state (reagent/atom init-state))

(def cur
  (memoize
   (fn [path]
     (rc/cur app-state path))))

(defn tile-cursor [n]
  (cur [:board :tiles n]))

(defn playing? []
  (= @(cur [:status]) :playing))

(defn won? []
  (every? true? (map = @(cur [:board :tiles]) finished-tiles)))

(defn reset-state! [s]
  (reset! app-state s))

(defn clean-state! []
  (reset-state! init-state))

(defn distance [p1 p2]
  (->>
   (map - p1 p2)
   (map js/Math.abs)
   (reduce +)))

(defn raw-move! [tile-cursor]
  (let [tile-pos @tile-cursor]
    (reset! tile-cursor @(cur [:board :empty]))
    (reset! (cur [:board :empty]) tile-pos)))

(defn switch! [a b]
  (let [a-cursor (tile-cursor a)
        b-cursor (tile-cursor b)
        b-pos @b-cursor]
    (reset! b-cursor @a-cursor)
    (reset! a-cursor b-pos)))

(defn scramble! []
  (let [new-empty (rand-int 16)
        new-empty-cursor (tile-cursor new-empty)
        keep-empty? (= new-empty 15)
        parity (if keep-empty?
                 0
                 (mod (inc (distance @new-empty-cursor
                                     @(cur [:board :empty])))
                      2))]
    (when-not keep-empty?
      (raw-move! new-empty-cursor))

    (dotimes [n (+ 100 parity)]
      (let [a (rand-int 15)
            b (rand-int 14)
            b (if (>= b a) (inc b) b)]
        (switch! a b)))))

(defn move! [tile]
  (let [tile-cursor (tile-cursor tile)]
    (when (and (playing?)
               (= 1 (distance @tile-cursor @(cur [:board :empty]))))
      (raw-move! tile-cursor)
      (swap! (cur [:moves]) inc)
      (when (won?)
        (reset! (cur [:status]) :finished)))))

(defn start-game! []
  (reset! (cur [:status]) :playing)
  (reset! (cur [:moves]) 0))

(defn new-game! []
  (scramble!)
  (start-game!))
