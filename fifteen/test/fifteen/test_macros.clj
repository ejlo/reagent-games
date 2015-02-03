(ns fifteen.test-macros
  (:require [cemerick.cljs.test :as t :refer-macros [is deftest]]
            #_[reagent.core       :as r]
            #_[fifteen.state       :as state]))

;; from clojurescript source code
(defmacro assert-args [fnname & pairs]
  `(do (when-not ~(first pairs)
         (throw (IllegalArgumentException.
                  ~(clojure.core/str fnname " requires " (second pairs)))))
     ~(clojure.core/let [more (nnext pairs)]
        (when more
          (list* `assert-args fnname more)))))

(defmacro defdomtest [name value-map & body]
  (assert-args defdomtest
               (map? value-map) "a map of paths and values")
 `(cemerick.cljs.test/deftest ~name
    (let [old-state# @fifteen.state/app-state]
      ~@(for [[path v] value-map]
          (list 'reset! `(reagent.cursor/cur fifteen.state/app-state ~path) v))
      (reagent.core/force-update-all)
      ~@body
      (fifteen.state/reset-state! old-state#)
      (reagent.core/force-update-all))))

(defmacro defdom [name]
  `(def ~name nil))
