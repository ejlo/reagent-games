(ns minesweeper.test
  (:require [clojure.string :as str]
            [reagent.core :as reagent :refer [atom]]
            [minesweeper.state :as state :refer [cur]]))

(defn fail-message [{:keys [test-name type message expected actual] :as m}]
  [:div.fail-message
   [:h5.testfail (str/capitalize (name type)) ": " test-name]
   (when message
     [:div.message (str message)])
   (when (= type :fail)
     (list [:div.expected [:span.name "Expected: "] [:span.res (str expected)]]
           [:div.actual [:span.name (if (= type :error) "Stacktrace: " "Actual: ")]
            [:span {:class :res} (str actual)]]))
   (when (= type :error)
     [:div.stacktrace [:span.name "Stacktrace: "]
      [:span {:class :stacktrace} (str actual)]])])

(defn indexed [coll]
  (map-indexed (fn [idx c] [idx c]) coll))

(defn fail-message-list [msgs]
  (let [msgs @(cur [:test :fail-messages])]
    (when-not (empty? msgs)
      [:div.fail-message-list
       (for [[idx m] (indexed msgs)] ^{:key idx}
         [fail-message m])])))

(defn test-component []
  (let [{:keys [pass fail error fail-messages]} @(cur [:test])
        class (if (or (pos? error) (pos? fail))
                :failed
                (when (pos? pass) :passed))]
    (when @(cur [:test])
      [:div.test {:class class}
       [:div.test-count
        (when (pos? pass) [:div.pass "Pass: " pass])
        (when (pos? fail) [:div.fail "Fail: " fail])
        (when (pos? error) [:div.error "Error: " error])
        (when (= 0 fail pass error) [:div.none "No tests"])]
       [fail-message-list]])))
