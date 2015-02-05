(ns minesweeper.state
    (:require [reagent.core :as reagent]
              [reagent.cursor :as rc]))

(defonce init-state {:text "Hello, this is: "})

(defonce app-state (reagent/atom init-state))

(def cur
  (memoize
   (fn [path]
     (rc/cur app-state path))))

(defn reset-state! [s]
  (reset! app-state s))

(defn clean-state! []
  (reset-state! init-state))
