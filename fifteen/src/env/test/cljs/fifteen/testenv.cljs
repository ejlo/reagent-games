(ns fifteen.testenv
  (:require [dommy.core :as dommy :refer-macros [sel sel1]]
            [cemerick.cljs.test :as t]
            [fifteen.core :as core]))


(defn exit [code]
  ;; work around a problem in phantomjs 1.9.8
  (js/setTimeout #(.exit js/phantom code) 0)
  (aset js/phantom "onError" (fn [])))

(try
  (enable-console-print!)

  (let [elem (dommy/create-element :div)]
   (aset elem "id" "app")
   (dommy/append! (sel1 :body) elem))

  (core/init!)

  (let [results (t/run-all-tests)]
    (t/on-testing-complete results #(exit (t/successful? %))))

  (catch js/Object e
    (print (.-stack e))
    (exit 1)))
