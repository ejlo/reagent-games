(ns fifteen.core
  (:require [reagent.core   :as reagent]
            [reagent.cursor :as rc]
            [cljsjs.react]
            [fifteen.state  :as state :refer [cur]]
            [fifteen.test   :as test]))

(defn tile [n]
  (let [[x y] @(state/tile-cursor n)]
    [:div.tile
     (merge {:class (str "nr-" (inc n) " y-" y " x-" x)}
            (when state/playing?
              {:on-click #(state/move! n)}))
     (str (inc n))]))

(defn tiles []
  [:div.tiles
   (for [n (range 15)] ^{:key n}
        [tile n])])

(defn row [y]
  [:div.row
   (for [x (range 4)] ^{:key x}
        [:div.cell])])

(defn board []
  [:div#board
   [:div#table
    (for [y (range 4)] ^{:key y}
         [row y])]
   [tiles]])

(defn info []
  [:div#info
   (let [moves @(cur [:moves])]
     (if (= :finished @(cur [:status]))
       (if moves
         (str "You did it in " moves " moves!")
         "")
       (str moves " moves")))])


(defn new-game-button []
  [:div.button (if (= :finished @(cur [:status]))
                 {:on-click #(state/new-game!)}
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
