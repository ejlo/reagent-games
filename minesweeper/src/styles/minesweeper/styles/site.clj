(ns minesweeper.styles.site
  (:require [environ.core :refer [env]]
            [garden.def :refer [defstyles defcssfn defkeyframes]]
            [garden.units :as unit :refer [px em percent]]
            [garden.color :as color]
            [garden.stylesheet :refer [at-media]]
            [minesweeper.styles.dev :as dev]))

(def user-select-none
  ^:prefix ^{:vendors ["webkit" "moz" "ms"]}
  {:user-select :none})

(defn rgba
  ([c a] (cond
           (color/color? c) (assoc c :alpha a)
           (color/hex? c) (rgba (color/as-rgb c) a)
           :default (rgba (color/from-name c) a)))
  ([r g b a] (color/rgba r g b a)))

(defcssfn url)

(defn background-icon [icon & [{:keys [size] :or {size 0.6}}]]
  (let [margin (unit/rem (* 0.5 (- 1 size)))]
    {:background-image (url (str "../img/" (name icon) ".png"))
     :background-repeat :no-repeat
     :background-size [[(unit/rem size) (unit/rem size)]]
     :background-position [[margin margin]]}))

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


(defn font-size [sz]
  [:html {:font-size (px sz)}])

(def font
  [(at-media {:min-width (px 480) :min-height (px 300)} (font-size 15))
   (at-media {:min-width (px 640) :min-height (px 400)} (font-size 20))
   (at-media {:min-width (px 800) :min-height (px 500)} (font-size 25))
   (at-media {:min-width (px 960) :min-height (px 600)} (font-size 30))
   (at-media {:min-width (px 1120) :min-height (px 700)} (font-size 35))
   (at-media {:min-width (px 1280) :min-height (px 800)} (font-size 40))
   (at-media {:min-width (px 1600) :min-height (px 1000)} (font-size 50))
   (at-media {:min-width (px 2400) :min-height (px 1500)} (font-size 75))])

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
    :witdh (percent 100)
    :display :flex
    :flex-direction :column
    :flex-wrap :nowrap
    :align-items :center
    :justify-content :center}])

(def content
  [:#content
   {:padding [[0 (em 0.3)]]}]
  )

(def info
  [:#info
   {:width (percent 100)
    :font-size (em 0.7)}
   [:#time
    {:float :right
     :margin-right (em 0.15)}]
   [:#mark-count
    {:margin-left (em 0.15)
     :float :left}]
   [:#smiley
    {:height (unit/rem 1.2)
     :width (unit/rem 1.2)
     :margin-left :auto
     :margin-right :auto}
    [:&.play
     (background-icon "smiley_play" {:size 1.2})]
    [:&.success
     (background-icon "smiley_success" {:size 1.2})]
    [:&.failure
     (background-icon "smiley_failure" {:size 1.2})]]])

(def new-game-row
  [:.new-game-row
   {:text-align :center}
   [:&.hide
    {:visibility :hidden}]])

(def size-form
  [:#size-form
   {:position :absolute
    :top (em 0.3)
    :left (em 0.3)
    :font-size (px 16)}
   ])

(def button
  [:.button
   user-select-none
   {:display :inline-block
    :border [[(px 1) :solid :black]]
    :border-radius (em 0.7)
    :padding [[(em 0.3) (em 1)]]
    :margin-top (em 1)
    :font-size (em 0.7)
    :cursor :pointer}
   [:&.hidden
    {:visibility :hidden}]
   [:&:hover
    {:background "#e8e8ff"}]])

(defn box-shadow [& [{:keys [revers?]}]]
  (let [w (unit/rem 0.05)
        w+ "calc(1px + 0.05rem)"
        -w (unit/rem -0.05)
        black (rgba :black 0.4)
        white (rgba :white 0.8)]
    {:box-shadow
     [[-w -w w 0 (if revers? white black) :inset]
      [w+ w+ w+ 0 (if revers? black white) :inset]]}))

(def board
  [:#board
   user-select-none
   (box-shadow)
   {:vertical-align :middle
    :padding (unit/rem 0.35)
    :background-color "#ddd"
    }
   [:#table
    {:display :table
     :border-collapse :collapse}
    [:.row
     {:display :table-row}]
    [:.cell
     {:display :table-cell
      :position :relative
      :width (unit/rem 1)
      :height (unit/rem 1)
      :border [[(px 1) :solid "rgba(0,0,0,0.05)"]]}]
    [:&.active
     [:.tile
      {:cursor :pointer}
      [:&:hover
       {:background-color "#e5e5e5"}]
      [:&:active
       (box-shadow {:revers? true})]]]]]

  )

(defn explosion-size [size]
  (let [margin (unit/rem (* 0.5 (- 1 size)))]
    {:width (unit/rem size)
     :height (unit/rem size)
     :top margin
     :left margin}))

(defkeyframes explode
  ["0%" (merge (explosion-size 1)
               {:opacity 1})]
  ["100%" (merge (explosion-size 5)
               {:opacity 0})])

(def tile
  [:.tile
   {
    :position :absolute
    :text-align :center
    :width (unit/rem 1)
    :min-width (unit/rem 1)
    :max-width (unit/rem 1)
    :height (unit/rem 1)
    :min-height (unit/rem 1)
    :max-height (unit/rem 1)
    :font-size (unit/rem 0.75)
    :font-weight :bold
    :line-height (unit/rem 1)
    :vertical-align :middle}
   [:&.hidden :&.mark :&.bad-mark
    (box-shadow)]
   [:&.mark :&.bad-mark
    (background-icon :flag)
    {:font-size (unit/rem 0.9)
     :color "#383"}]
   [:&.bad-mark
    {:color "#c33"
     :cursor :normal
     :font-size (unit/rem 1)
     :font-weight :normal}]
   [:&.boom
    [:img
     (explosion-size 2.2)
     {:position :absolute
      :z-index 1}
     [:&.explosion
      ^:prefix {:animation [[explode "0.4s"]]}
      {:opacity 0}]]
    ]
   [:&.hidden-mine
    (background-icon :mine)]

   (let [s 75 l 45
         s2 55 l2 35]
     [[:&.nr0 {:color "#ddd"}]
      [:&.nr1 {:color (color/hsl 240 s l)}]
      [:&.nr2 {:color (color/hsl 120 s l)}]
      [:&.nr3 {:color (color/hsl 360 s l)}]
      [:&.nr4 {:color (color/hsl 195 s l)}]
      [:&.nr5 {:color (color/hsl 20 s2 l2)}]
      [:&.nr6 {:color (color/hsl 285 s2 l2)}]
      [:&.nr7 {:color (color/hsl 150 s2 l2)}]
      [:&.nr8 {:color (color/hsl 60 s2 l2)}]])])

(defstyles site
  [reset
   font
   explode
   app
   main
   content
   info
   board
   tile
   new-game-row
   button
   size-form
   (when (env :dev?)
     dev/dev)])
