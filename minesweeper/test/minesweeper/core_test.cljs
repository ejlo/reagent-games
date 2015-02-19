(ns minesweeper.core-test
  (:require [cemerick.cljs.test :as t :refer-macros [is deftest use-fixtures]]
            [dommy.core         :as dommy :refer-macros [sel sel1]]
            [reagent.core       :as r]
            [minesweeper.core   :as minesweeper]
            [minesweeper.state  :as state :refer [cur]]))

(def saved-app-state (atom nil))

(defn save-app-state [f]
  (reset! saved-app-state @state/app-state)
  (f)
  (reset! state/app-state @saved-app-state))

(use-fixtures :once save-app-state)

(deftest trivial-pass
  (is (= 1 1)))

(defn map2v [f v]
  (mapv #(mapv f %) v))

(defn get-n-vector [tiles]
  (map2v #(if (:mine %) :mine (:n %)) tiles))

(deftest neighbors []
  (let [size [3 3 0]]
    (is (= (set (state/neighbors size 0 0 nil))
           #{[0 1] [1 0] [1 1]}))
    (is (= (set (state/neighbors size 2 2 (fn [y x] (= x 1))))
           #{[1 1] [2 1]}))))

(deftest put-mine
  (let [size [3 3]
        _ (state/clean-state! size)
        tiles @(cur [:board :tiles])]
    (is (= (mapv #(mapv :n %) tiles) [[0 0 0] [0 0 0] [0 0 0]]))
     (let [tiles (state/put-mine tiles size 1 1)
           _     (is (= (get-n-vector tiles)
                       [[1 1 1] [1 :mine 1] [1 1 1]]))
           tiles (state/put-mine tiles size 2 0)
           _     (is (= (get-n-vector tiles)
                        [[1 1 1] [2 :mine 1] [:mine 2 1]]))
           tiles (state/put-mine tiles size 1 2)
           _     (is (= (get-n-vector tiles)
                        [[1 2 2] [2 :mine :mine] [:mine 3 2]]))])))

(deftest put-random-mines
  (let [sz 3
        mines 5
        size [sz sz]
        _ (state/clean-state! size)
        _ (state/put-random-mines! mines)
        tiles @(cur [:board :tiles])]
    (is (= (count (flatten tiles)) (* sz sz)))
    (is (= (->> tiles flatten (filter :mine) count) mines))
    (let [mines2 3
          _ (state/put-random-mines! mines2)
          tiles @(cur [:board :tiles])
          [y1 x1] (first (for [x (range 0 sz)
                               y (range 0 sz)
                               :when (not (:mine (get-in tiles [y x])))]
                           [y x]))
          num (get-in tiles [y1 x1 :n])]
      (is (= (->> tiles flatten (filter :mine) count)
             (+ mines mines2)))
      (is (= num (cond (= 1 x1 y1)      8
                       (odd? (+ x1 y1)) 5
                       :default         3))))))

(deftest test-component
  (reset! (cur [:test :pass]) 2)
  (r/force-update-all)
  (is (= "2" (some-> [".test" ".pass" :span] sel second dommy/text))))

(deftest app-exists
  (is (sel1 :#app)))

(deftest main-exists
  (is (sel1 :#main)))

(deftest board-exists
  (is (sel1 :#board)))

(deftest info-exists
  (is (sel1 :#info)))

(deftest empty-board
  (let [size [3 4]
        [y x] size
        tile-count (* y x)]
    (state/clean-state! size)
    (reset! (cur [:status]) :playing)
    (r/force-update-all)
    (is (= y (count @(cur [:board :tiles]))))
    (is (= tile-count (some-> :.hidden sel count)))
    (state/_show-tile 0 0)
    (r/force-update-all)
    (is (not @(cur [:board :tiles 0 0 :hidden])))
    (is (= tile-count (some-> :.cell sel count)))
    (is (= (dec tile-count) (some-> :.hidden sel count)))
    (let [tile-cursor (cur [:board :tiles])]
      (reset! tile-cursor
              (state/put-mine @tile-cursor size (dec y) (dec x))))
    (state/show-tile 1 0)
    (r/force-update-all)
    (is (= 1 (some-> :.hidden sel count)) "Flood show")))

(deftest board-mark
  (state/start-game! [3 3] 0)
  (state/mark-tile 0 0)
  (r/force-update-all)
  (is @(cur [:board :tiles 0 0 :hidden]))
  (is @(cur [:board :tiles 0 0 :mark]))
  (is (= "tile mark" (some-> :.tile sel1 dommy/class)))
  (state/end-game! :failure))

(deftest boom
  (let [size [3 3]]
    (state/start-game! size 0)
    (-> @(cur [:board :tiles])
        (state/put-mine size 0 0)
        (state/put-mine size 1 1)
        (state/put-mine size 2 2)
        (#(reset! (cur [:board :tiles]) %)))
    (reset! (cur [:info :mines]) 3)
    (reset! (cur [:status]) :playing)
    (state/mark-tile 1 1)
    (state/mark-tile 1 2)
    (state/show-tile 0 0)
    (r/force-update-all)
    (is (= @(cur [:status]) :finished))
    (is (= @(cur [:result]) :failure))
    (is (= (first (state/tile-class-and-content 0 0)) :boom))
    (is (= (first (state/tile-class-and-content 1 1)) :mark))
    (is (= (first (state/tile-class-and-content 1 2)) :bad-mark))
    (is (= (first (state/tile-class-and-content 2 2)) :hidden-mine))))

(deftest win
  (let [size [3 3]]
    (state/start-game! size 0)
    (-> @(cur [:board :tiles])
        (state/put-mine size 0 0)
        (state/put-mine size 0 1)
        (#(reset! (cur [:board :tiles]) %)))
    (reset! (cur [:info :mines]) 2)
    (state/show-tile 2 2)
    (state/mark-tile 0 1)
    (state/show-tile 0 2)
    (r/force-update-all)
    (is (= @(cur [:status]) :finished))
    (is (= @(cur [:result]) :success))
    (is (= (first (state/tile-class-and-content 0 0)) :mark))
    (is (= (first (state/tile-class-and-content 0 1)) :mark))))
