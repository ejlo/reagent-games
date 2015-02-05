(ns minesweeper.core-test
  (:require-macros [minesweeper.test-macros :refer [defdomtest defdom]])
  (:require [cemerick.cljs.test :as t :refer-macros [is deftest done]]
            [dommy.core         :as dommy :refer-macros [sel sel1]]
            [reagent.core       :as r]
            [minesweeper.core        :as minesweeper]
            [minesweeper.state       :as state]))

(deftest trivial-pass
  (is (= 1 1)))

(defdomtest test-component {[:test] {:pass 2}}
  (is (= "2" (some-> [".test" ".pass" :span] sel second dommy/text))))

(defdomtest app-exists {}
  (is (sel1 :#app)))

(defdomtest main-exists {}
  (is (sel1 :#main)))
