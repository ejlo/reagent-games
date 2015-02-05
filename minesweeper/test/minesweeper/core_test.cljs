(ns minesweeper.core-test
  (:require-macros [minesweeper.test-macros :refer [defdomtest defdom]])
  (:require [cemerick.cljs.test :as t :refer-macros [is deftest done]]
            [dommy.core         :as dommy :refer-macros [sel sel1]]
            [reagent.core       :as r]
            [minesweeper.core        :as minesweeper]
            [minesweeper.state       :as state]))

(deftest trivial-pass
  (is (= 1 1)))

(deftest trivial-fail
  #_(is (= 2 1)))

(deftest trivial-error
  #_(is (1 = 1) "Don't do this!!!"))

(deftest ^:async trivial-async-test
  (let [now #(.getTime (js/Date.))
        t (now)]
    (js/setTimeout
      (fn []
        (is (>= (now) (+ t 10)))
        (done))
      10)))

(defdomtest page1 {[:current-page] :page1}
  (is (= (some-> [:#main :h2] sel1 dommy/text)
         "Hello, this is: Page 1")))

(defdomtest page2 {[:current-page] :page2}
  (is (= (some-> [:#main :h2] sel1 dommy/text)
         "Hello, this is: Page 2")))

(defdomtest test-component {[:test] {:pass 2}}
  (is (= "2" (some-> [".test" ".pass" :span] sel second dommy/text))))
