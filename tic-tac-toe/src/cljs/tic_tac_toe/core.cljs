(ns tic-tac-toe.core
  (:require [reagent.core :as reagent]
            [cljsjs.react]
            [tic-tac-toe.utils :as utils]
            [tic-tac-toe.state :as state]
            [tic-tac-toe.test :as test]))

(defn make-move [player x y]
  (when (nil? (state/get-state [:board y x]))
    (state/put! [:board y x] player)
    (state/put! [:to-move] (player {:X :O, :O :X}))
    (when-let [winner (state/winner (state/get-state [:board]))]
      (state/put! [:to-move] nil)
      (state/put! [:winner] winner)
      (state/put! [:status] :finished))))

(defn player-str [p]
  (name (or p "")))

(defn tile [x y]
  [:div.tile (when-let [player (state/get-state [:to-move])]
               {:on-click #(make-move player x y)})
   (player-str (state/get-state [:board y x]))])

(defn row [y]
  [:div.row
   (for [x (range 3)] ^{:key x}
     [tile x y])])

(defn board []
  [:div#board
   (for [y (range 3)] ^{:key y}
     [row y])])

(defn info []
  [:div#info
   (if (= :finished (state/get-state [:status]))
     (let [winner (state/get-state [:winner])]
       (if (= winner :draw)
         "Draw!"
         (str "Player " (player-str winner) " won the game!")))
     (str "Player " (player-str (state/get-state [:to-move])) " to play"))])

(defn new-game-button []
  [:div.button (if (= :finished (state/get-state [:status]))
                 {:on-click #(state/clean-state!)}
                 {:class :hidden})
   "Start new game!"])

(defn main-page []
  [:div#main
   [board]
   [info]
   [new-game-button]
   [test/test-component]])

(defn init! []
  (reagent/render-component [main-page] (.getElementById js/document "app")))
