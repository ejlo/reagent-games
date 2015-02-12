(ns minesweeper.core
  (:require [clojure.string :as str]
            [dommy.core        :as dommy :refer-macros [sel1]]
            [goog.events       :as gevents]
            [minesweeper.state :as state :refer [cur]]
            [minesweeper.test  :as test]
            [reagent.core      :as reagent]))


(defn tile [y x]
  (reagent/create-class
   {:component-did-mount
    (fn [this]
      (gevents/listen (reagent/dom-node this) "click"
                      (fn [event]
                        (state/show-tile y x))
                      true)
      (gevents/listen (reagent/dom-node this) "contextmenu"
                        (fn [event]
                          (state/mark-tile y x)
                          (.preventDefault event))
                        true))
    :render (fn []
              (let [[class content] (state/tile-class-and-content y x)]
                [:div.cell
                 [:div.tile {:class class}
                  content]]))}))

(defn row [y size]
  [:div.row
   (for [x (range (second size))] ^{:key x}
        [tile y x])])

(defn board [size]
  [:div#board
   [:div#table (when (state/playing?) {:class :active})
    (for [y (range (first size))] ^{:key y}
         [row y size])]])

(defn time [time-elapsed]
  [:div#time
   (when-let [time time-elapsed]
     (let [min (js/Math.floor (/ time 60000))
           sec (js/Math.floor (/ (- time (* min 60000)) 1000))]
       (str min ":" (if (< sec 10) "0" "") sec)))])

(defn info []
  (let [info @(cur [:info])
        status @(cur [:status])
        result @(cur [:result])
        left (- (:mines info) (:marked info))]
    [:div#info
     [time (get-in info [:time :time-elapsed])]
     [:div#mark-count (if (and (= :finished status)
                               (= :success result))
                        0
                        left)]
     [:div#smiley {:class (if (state/playing?) :play
                              (if (= :success result)
                                :success :failure))}]]))

(defn get-size-form-value []
  (some-> js/document
          (.getElementById "size")
          (.-value)
          keyword))

(defn size-form []
  [:div#size-form
   [:label {:for :size} "Size: "]
   [:select#size {:id :size
                  :defaultValue @(cur [:size-form-value])
                  :onChange #(reset! (cur [:size-form-value])
                                     (get-size-form-value))}
    (for [val [:small :medium :big]] ^{:key val}
         [:option {:value val}
          (str/capitalize (name val))])]])

(defn new-game-button []
  [:div.button {:on-click #(let [sz (or (get-size-form-value) :medium)]
                             (reset! (cur [:size-form-value]) sz)
                             (state/start-game! sz))}
   "Start new game!"])

(defn new-game-row []
  [:div.new-game-row
   (when-not (= :finished @(cur [:status]))
     {:class :hide})
   [new-game-button]
   [size-form]])

(defn main-page []
  [:div#main
   [:div#content
    {:style {:width (str (+ 0.7 @(cur [:board :size 1])) "rem")}}
    [info]
    [board @(cur [:board :size])]
    [new-game-row]]
   [test/test-component]])

(defn render []
  (let [app (.getElementById js/document "app")]
    (reagent/render [main-page] app)))

(defn init! []
  (render)
  (state/start-game! :small))
