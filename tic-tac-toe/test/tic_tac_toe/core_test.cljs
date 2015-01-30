(ns tic-tac-toe.core-test
  (:require-macros [tic-tac-toe.test-macros :refer [defdomtest defdom]])
  (:require [cemerick.cljs.test :as t :refer-macros [is deftest done]]
            [dommy.core         :as dommy :refer-macros [sel sel1]]
            [reagent.core       :as r]
            [tic-tac-toe.core   :as tic-tac-toe]
            [tic-tac-toe.state  :as state]))

(deftest trivial-pass
  (is (= 1 1)))

(deftest ^:async async-test
  (let [now #(.getTime (js/Date.))
        t (now)]
    (js/setTimeout
      (fn []
        (is (>= (now) (+ t 10)))
        (done))
      10)))

(deftest winner
  (is (= :draw (state/winner [[:X :O :O] [:O :X :X] [:O :X :O]])))
  (is (= :O    (state/winner [[:O nil :O] [:X :O :X] [nil nil :O]])))
  (is (= :X    (state/winner [[:X :O :O] [:X :X :X] [:O :X :O]])))
  (is (= :draw (state/winner [[:O :O :O] [:X :X :X] [nil nil :O]])))
  (is (= nil   (state/winner [[:X :X nil] [nil :O :X] [nil :O :O]])))
  (is (= :O    (state/winner [[:X :X nil] [nil :O :X] [:O :O :O]]))))


(defdomtest test-component {[:test] {:pass 2}}
  (is (= "2" (some-> [".test" ".pass" :span] sel second dommy/text))))

(defdomtest body-exists {}
  (is (sel1 :body)))

(defdomtest app-exists {}
  (is (sel1 :#app)))

(defdomtest main-exists {}
  (is (sel1 :#main)))

(defdomtest board-exists {}
  (is (sel1 :#board)))

(defdomtest rows-are-three {}
  (is (= 3 (some-> :.row sel count))))

(defdomtest tiles-are-nine {}
  (is (= 9 (some-> :.tile sel count))))

(defdomtest board-with-content {[:board] [[:X  :O  nil]
                                          [nil nil :X ]
                                          [nil :X  nil]]
                                [:status] :playing}
  (let [tile-divs (some-> [:.tile] sel)]
    (is (= 3 (some->> tile-divs (filter #(= (dommy/text %) "X")) count)))
    (is (= "X" (some->> (nth tile-divs 5) dommy/text)))))

(defdomtest info-exists {}
  (is (sel1 :#info)))

(defdomtest info-draw {[:status] :finished, [:winner] :draw}
  (is (= "Draw!" (some-> :#info sel1 dommy/text))))

(defdomtest info-winner {[:status] :finished, [:winner] :X}
  (is (= "Player X won the game!" (some-> :#info sel1 dommy/text))))

(defdomtest button-exists {}
  (is (sel1 :.button)))

(defdomtest button-hidden {[:status] :playing}
  (is (sel1 :.button.hidden)))

(defdomtest button-shown {[:status] :finished, , [:winner] :X}
  (is (nil? (some-> [:.button.hidden] sel1))))

(defn click-tile [y x]
  (some-> (sel :.row) (nth y) (sel :.tile) (nth x) (.click))
  (r/force-update-all))

(defdomtest full-game {[:board] [[:X  :O  nil]
                                 [nil nil :X ]
                                 [nil :X  nil]]
                       [:status] :playing
                       [:to-move] :X}
  (click-tile 0 2)
  (is (= :X (state/get-state [:board 0 2])))
  (is (= :O (state/get-state [:to-move])))
  (is (= :playing (state/get-state [:status])))
  (click-tile 1 1)
  (is (= :O (state/get-state [:board 1 1])))
  (is (= :X (state/get-state [:to-move])))
  (is (= :playing (state/get-state [:status])))
  (click-tile 2 2)
  (is (= :X (state/get-state [:board 2 2])))
  (is (nil? (state/get-state [:to-move])))
  (is (= :finished (state/get-state [:status])))
  (some-> (sel1 :.button) (.click))
  (r/force-update-all)
  (is (= state/empty-board (state/get-state [:board])))
  (is (= :O (state/get-state [:to-move])))
  (is (= :playing (state/get-state [:status]))))
