(ns tic-tac-toe.state
    (:require [reagent.core :as reagent]))

(defonce app-state (reagent/atom {:text "Hello, this is: "}))

(defn get-state
  ([cursor]
     (get-in @app-state cursor))
  ([cursor default]
     (get-in @app-state cursor default)))

(defn put! [cursor v]
  (swap! app-state assoc-in cursor v))

(defn update! [cursor f & args]
  (apply swap! app-state update-in cursor f args))

(defn reset-state! [s]
  (reset! app-state s))
