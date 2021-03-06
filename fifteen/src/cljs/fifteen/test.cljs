(ns fifteen.test
  (:require [clojure.string :as str]
            [reagent.core   :as reagent :refer [atom]]
            [reagent.cursor :as rc]
            [fifteen.state  :as state :refer [cur]]
            [fifteen.utils  :as utils]))

(defn fail-message [{:keys [test-name type message expected actual] :as m}]
  [:div.fail-message
   [:h5.testfail (str/capitalize (name type)) ": " test-name]
   (when message
     [:div.message (str message)])
   [:div.expected [:span.name "Expected: "] [:span.res (str expected)]]
   [:div.actual [:span.name "Actual: "]
    [:span {:class (if (= type :error) :stack :res)} (str actual)]]])


(defn fail-message-list [msgs]
  (let [msgs @(cur [:test :fail-messages])]
    (when-not (empty? msgs)
      [:div.fail-message-list
       (for [[idx m] (utils/indexed msgs)] ^{:keys idx}
         [fail-message m])])))

(defn test-component []
  (when @(cur [:test])
    (let [{:keys [pass fail error fail-messages]} @(cur [:test])
          class (if (or (pos? error) (pos? fail))
                  :failed
                  (when (pos? pass) :passed))]
      [:div.test {:class class}
       [:div.test-count
        (when (pos? pass) [:div.pass "Pass: " pass])
        (when (pos? fail) [:div.fail "Fail: " fail])
        (when (pos? error) [:div.error "Error: " error])
        (when (= 0 fail pass error) [:div.none "No tests"])]
       [fail-message-list]])))
