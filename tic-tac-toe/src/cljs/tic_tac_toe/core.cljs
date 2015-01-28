(ns tic-tac-toe.core
  (:require [reagent.core :as reagent]
            [cljsjs.react]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [tic-tac-toe.state :as state]
            [tic-tac-toe.test :as test])
  (:import goog.History))

;; -------------------------
;; Views

(defmulti page identity)

(defmethod page :page1 [_]
  [:div [:h2 (state/get-state [:text]) "Page 1"]
   [:div [:a {:href "#/page2"} "go to page 2 →"]]])

(defmethod page :page2 [_]
  [:div [:h2 (state/get-state [:text]) "Page 2"]
   [:div [:a {:href "#/"} "← go to page 1"]]])

(defmethod page :default [_]
  [:div "Invalid/Unknown route"])

(defn main-page []
  [:div#main [page (state/get-state [:current-page])]
   [test/test-component]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (state/put! [:current-page] :page1))

(secretary/defroute "/page2" []
  (state/put! [:current-page] :page2))

;; -------------------------
;; Initialize app
(defn init! []
  (reagent/render-component [main-page] (.getElementById js/document "app")))

;; -------------------------
;; History
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))
;; need to run this after routes have been defined
(hook-browser-navigation!)
