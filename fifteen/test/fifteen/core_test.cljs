(ns fifteen.core-test
  (:require-macros [fifteen.test-macros :refer [defdomtest defdom]])
  (:require [cemerick.cljs.test :as t :refer-macros [is deftest done]]
            [dommy.core         :as dommy :refer-macros [sel sel1]]
            [reagent.core       :as r]
            [reagent.cursor     :as rc]
            [fifteen.core       :as fifteen]
            [fifteen.state      :as state :refer [cur]]))

(deftest trivial-pass
  (is (= 1 1)))

(deftest trivial-fail
  #_(is (= 2 1)))

(deftest trivial-error
  #_(is (1 = 1) "Don't do this!!!"))

(deftest ^:async trivial-async-test
  (let [now #(.getTime (js/Date.))
        t (now)]
    (js/setTimeout
      (fn []
        (is (>= (now) (+ t 10)))
        (done))
      10)))

(defdomtest test-component {[:test] {:pass 2}}
  (is (= "2" (some-> [".test" ".pass" :span] sel second dommy/text))))


(deftest distance
  (is (= 3
         (state/distance [0 0] [1 2])
         (state/distance [1 2] [0 0])
         (state/distance [2 1] [3 3]))))

(defdomtest move! {}
  (state/clean-state!)
  (let [tiles-cursor (cur [:board :tiles])
        tiles @tiles-cursor]
    (state/start-game!)
    (state/move! (dec 11))
    (is (= tiles @tiles-cursor))
    (is (state/won?))
    (state/move! (dec 15))
    (is (= @(state/tile-cursor (dec 15)) [3 3]))
    (is (= @(cur [:board :empty]) [2 3]))
    (is (not (state/won?)))
    (state/move! (dec 11))
    (is (= @(state/tile-cursor (dec 11)) [2 3] ))
    (is (= @(cur [:board :empty]) [2 2]))
    (is (= @(cur [:moves]) 2))))





(defdomtest app-exists {}
  (is (sel1 :#app)))

(defdomtest main-exists {}
  (is (sel1 :#main)))

(defdomtest board-exists {}
  (is (sel1 :#board)))

(defdomtest rows-are-four {}
  (is (= 4 (some-> :.row sel count))))

(defdomtest cells-are-sixteen {}
  (is (= 16 (some-> :.cell sel count))))

(defdomtest tiles-are-fifteen {}
  (is (= 15 (some-> :.tile sel count))))


(defdomtest info-exists {}
  (is (sel1 :#info)))

(defdomtest info-moves {[:status] :playing [:moves] 10}
  (is (= "10 moves" (some-> :#info sel1 dommy/text))))

(defdomtest info-done {[:status] :finished, [:moves] 42}
  (is (= "You did it in 42 moves!" (some-> :#info sel1 dommy/text))))

(defdomtest button-exists {}
  (is (sel1 :.button)))

(defdomtest button-hidden {[:status] :playing}
  (is (sel1 :.button.hidden)))

(defdomtest button-shown {[:status] :finished}
  (is (nil? (some-> [:.button.hidden] sel1))))


(defn click-tile! [n]
  (some-> (sel1 (str ".tile.nr-" n)) (.click)))

(defdomtest full-game {[:board :tiles]  [[3 2] [3 3] [1 2] [0 2]
                                         [1 3] [3 1] [0 3] [2 3]
                                         [1 0] [2 1] [2 2] [0 0]
                                         [1 1] [2 0] [3 0]]
                       [:board :empty] [0 1]}
  (state/start-game!)
  (r/force-update-all)
  (is (= [3 0] @(state/tile-cursor 14)))
  (is (= [0 1] @(cur [:board :empty])))
  (is (= :playing @(cur [:status])))
  (is (= 0 @(cur [:moves])))
  (is (sel1 ".tile.nr-1"))
  (let [move-list [4 3 13 9 12 4 3 13 11 1 2 8 5 11 1 5 11 7 13 1
                   5 10 14 12 4 3 1 5 9 14 6 2 10 6 2 15 12 4 3 1
                   5 9 14 2 6 10 8 11 7 14 10 8 15 12 4 3 2 6 8 7
                   11 15 12 8 7 11 15]]
    (doseq [mv move-list]
      (click-tile! mv))
    (r/force-update-all)
    (is (= :finished @(cur [:status])))
    (is (= (count move-list) @(cur [:moves]))))
  (some-> (sel1 :.button) (.click))
  (r/force-update-all)
  (is (= :playing @(cur [:status]))))
