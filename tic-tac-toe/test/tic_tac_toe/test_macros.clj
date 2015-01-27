(ns tic-tac-toe.test-macros
  (:require [cemerick.cljs.test :as t :refer-macros [is deftest]]
            #_[reagent.core       :as r]
            #_[tic-tac-toe.state       :as state]))

;; from clojurescript source code
(defmacro assert-args [fnname & pairs]
  `(do (when-not ~(first pairs)
         (throw (IllegalArgumentException.
                  ~(clojure.core/str fnname " requires " (second pairs)))))
     ~(clojure.core/let [more (nnext pairs)]
        (when more
          (list* `assert-args fnname more)))))

(defmacro defdomtest [name cursor-value-map & body]
  (assert-args defdomtest
               (map? cursor-value-map) "a map of cursor and values")
 `(cemerick.cljs.test/deftest ~name
    (let [old-state# @tic-tac-toe.state/app-state]
      (tic-tac-toe.state/put! [:testing] true)
      ~@(for [[cursor v] cursor-value-map]
          (list 'tic-tac-toe.state/put! cursor v))
      (reagent.core/force-update-all)
      ~@body
      (tic-tac-toe.state/reset-state! old-state#)
      (reagent.core/force-update-all))))

(defmacro defdom [name]
  `(def ~name nil))
