(ns tic-tac-toe.styles.site
  (:require [garden.def :refer [defstyles]]
            [garden.units :as unit :refer [px em vh vw vmin percent]]
            [garden.arithmetic :as u]))

(def reset
  [[:body
    {:font-family "sans-serif"
     :font-size (em 1.125)
     :color "#333"
     :line-height 1.5
     :position :relative
     :overflow :hidden}]

   [:h1 :h2 :h3 :h4 :h5 :h6
    {:color :black
     :margin [[(em 0.5) 0]]}]

   [:h1 {:font-size (em 2.5)}]
   [:h2 {:font-size (em 2.0)}]
   [:h3 {:font-size (em 1.5)}]
   [:h4 {:font-size (em 1.3)}]
   [:h5 {:font-size (em 1.2)}]
   [:h6 {:font-size (em 1.1)}]

   [:a {:text-decoration "none"
        :color "#09f"}
    [:&:hover
     {:text-decoration 'underline}]]])

(def app
  [:#app
   {:position :absolute
    :top 0
    :left 0
    :height (percent 100)
    :width (percent 100)}])

(def tilesize 20) ;vmin

(def main
  [:#main
   {:height (percent 100)
    :width (percent 100)
    :display :flex
    :flex-direction :column
    :align-items :center
    :justify-content :center}])

(def user-select-none
  {:user-select :none
   :-webkit-user-select :none
   :-moz-user-select :none
   :-ms-user-select :none})

(def board
  [:#board
   {:display :table
    :vertical-align :middle
    :border-spacing (px 3)
    :border [[(px 1) :solid "#aaa"]]
    :border-radius (px 4)
    :background "#ffc0c0"}
   [:.row
    {:display :table-row}]

   [:.tile
    user-select-none
    {:display :table-cell
     :width (vmin tilesize)
     :height (vmin tilesize)
     :border [[(px 1) :solid "#aaa"]]
     :vertical-align :middle
     :text-align :center
     :font-size (vmin (* tilesize 0.8))
     :line-height (vmin tilesize)
     :border-radius (px 4)
     :background "#fafafa"
     :cursor :pointer}
    [:&:hover
     {:background "#fffefa"}]]])

(def info
  [:#info
   {:max-width (vmin (* 5 tilesize))
    :font-size (unit/rem 1.5)
    :text-align :center
    :margin-top (em 1)}])

(def button
  [:.button
   user-select-none
   {:border [[(px 1) :solid :black]]
    :border-radius (em 0.7)
    :padding [[(em 0.3) (em 1.5)]]
    :margin-top (em 1)
    :cursor :pointer}
   [:&.hidden
    {:visibility :hidden}]
   [:&:hover
    {:background "#f8f8ff"}]])

(defstyles site
  [reset
   app
   main
   board
   info
   button])
