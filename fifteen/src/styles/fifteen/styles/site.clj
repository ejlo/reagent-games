(ns fifteen.styles.site
  (:require [environ.core :refer [env]]
            [garden.def :refer [defstyles]]
            [garden.units :as unit :refer [px em percent vmin s]]
            [garden.stylesheet :refer [at-media]]
            [fifteen.styles.dev :as dev]))

(def user-select-none
  {:user-select :none
   :-webkit-user-select :none
   :-moz-user-select :none
   :-ms-user-select :none})

(def reset
  [[:body
    {:font-family "sans-serif"
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

(def font
  [:html {:font-size "calc(2vmin + 3px)"}])

(def app
  [:#app
   {:position :absolute
    :top 0
    :left 0
    :height (percent 100)
    :width (percent 100)}
   [:a
    {:text-decoration "none"
     :color "#09f"}
    [:&:hover
     {:text-decoration 'underline}]]])

(def main
  [:#main
   {:height (percent 100)
    :width (percent 100)
    :display :flex
    :flex-direction :column
    :align-items :center
    :justify-content :center}])

(def tilesize 5) ;rem
(def margin 0.5) ;rem

(def board
  [:#board
   user-select-none
   {:position :relative
    :vertical-align :middle}
   [:#table
    {:display :table
     :border-spacing (unit/rem margin)
     :border [[(px 1) :solid "#aaa"]]
     :border-radius (unit/rem margin)}
    [:.row
     {:display :table-row}]

    [:.cell
     {:display :table-cell
      :width (unit/rem tilesize)
      :height (unit/rem tilesize)}
     ]]])

(def tiles
  [:.tiles
   {:position :absolute
    :width 0
    :height 0
    :left 0
    :top 0}

   [:.tile
    user-select-none
    {:position :absolute
     :width (unit/rem tilesize)
     :height (unit/rem tilesize)
     :border [[(px 1) :solid "#aaa"]]
     :margin (unit/rem margin)
     :border-radius (px 5)
     :top 0
     :left 0
     :vertical-align :middle
     :text-align :center
     :font-size (unit/rem 2.3)
     :line-height (unit/rem tilesize)
     :cursor :pointer
     :transition-property [:top :left]
     :transition-duration (s 0.2)
     :transition-timing-function :ease-out}
    [:&:hover
     {:background "#f0f0e0"}]
    (vec (for [n (range 4)]
           [(str "&.y-" n)
            {:top (unit/rem (* n (+ margin tilesize)))}]))
    (vec (for [n (range 4)]
           [(str "&.x-" n)
            {:left (unit/rem (* n (+ margin tilesize)))}]))]])

(def info
  [:#info
   {:max-width (em 30)
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
    {:background "#e8e8ff"}]])

(defstyles site
  [reset
   font
   app
   main
   board
   tiles
   info
   button
   (when (env :dev?)
     dev/dev)])
