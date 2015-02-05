(ns minesweeper.core
  (:require [reagent.core :as reagent]
            [cljsjs.react]
            [minesweeper.state :as state :refer [cur]]
            [minesweeper.test :as test]))

(defn main-page []
  [:div#main
   #_[board]
   #_[info]
   #_[new-game-button]
   [test/test-component]])

(defn init! []
  (reagent/render-component [main-page] (.getElementById js/document "app")))
