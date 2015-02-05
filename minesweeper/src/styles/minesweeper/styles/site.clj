(ns minesweeper.styles.site
  (:require [environ.core :refer [env]]
            [garden.def :refer [defstyles]]
            [garden.units :refer [px em]]
            [minesweeper.styles.dev :as dev]))

(def reset
  [[:body
    {:font-family "sans-serif"
     :font-size (em 1.125)
     :color "#333"
     :line-height 1.5}]

   [:h1 :h2 :h3 :h4 :h5 :h6
    {:color :black
     :margin [[(em 0.5) 0]]}]

   [:h1 {:font-size (em 2.5)}]
   [:h2 {:font-size (em 2.0)}]
   [:h3 {:font-size (em 1.5)}]
   [:h4 {:font-size (em 1.3)}]
   [:h5 {:font-size (em 1.2)}]
   [:h6 {:font-size (em 1.1)}]])

(def app
  [:a
   {:text-decoration "none"
    :color "#09f"}
   [:&:hover
    {:text-decoration 'underline}]])

(defstyles site
  [reset
   app
   (when (env :dev?)
     dev/dev)])
