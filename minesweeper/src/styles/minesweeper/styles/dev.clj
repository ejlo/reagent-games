(ns minesweeper.styles.dev
  (:require [garden.def :refer [defstyles]]
            [garden.units :as unit :refer [px em]]))

(defstyles dev
  [:.test {:display :block
           :position :fixed
           :top (em 1)
           :right (em 1)
           :margin-left (em 1)
           :max-height "calc(100vh - 4em)"
           :max-width (em 35)
           :overflow :auto
           :font-size (px 14)
           :line-height (em 1.5)
           :background "rgba(252,255,198,0.9)"
           :padding [[(em 0.3) (em 0.6)]]
           :border {:radius (em 0.4)
                    :width (em 0.1)
                    :style :solid
                    :color "rgba(220,223,10,0.7)"}}

   [:&.failed {:background "rgba(255,202,215,0.9)"
               :border-color "rgba(240,23,40,0.7)"}]


   [:&.passed {:background "rgba(220,255,216,0.9)"
               :border-color "rgba(43,240,20,0.7)"}]

   [:.test-count {:text-align :right}]

   [:.fail :.error :.message :.name :.res
    {:font-weight :bold}]
   [:.message {:color "#393"}]

   [:.name {:color "#888"}]
   [:.expected [:.res {:color "#24e"}]]
   [:.actual [:.res {:color "#72f"}]]
   [:.stacktrace {:display :block}]
   [:.fail-message-list {}]
   [:.fail-message {:border-top [[(px 1) :solid :black]]
                    :margin (em 0.4)}]])
