(ns minesweeper.testrunner
  (:require [cemerick.cljs.test :as t :refer [report]]
            [minesweeper.state :as state :refer [cur]]
            [minesweeper.core-test]))

;; temporary state, we don't want to mess with the app state during testing
(def fail-messages (atom '()) )

(defmethod report :pass [{:keys [test-env] :as m}]
  (t/inc-report-counter test-env :pass))

(defn update-test-failure [m]
  (let [msg (-> m
                (select-keys [:message :expected :actual :type])
                (assoc :test-name (t/testing-vars-str m)))]
    (swap! fail-messages conj msg)))

(defmethod report :fail [{:keys [test-env] :as m}]
  (t/inc-report-counter test-env :fail)
  (update-test-failure m))

(defmethod report :error [{:keys [test-env] :as m}]
  (t/inc-report-counter test-env :error)
  (let [actual (:actual m)
        m (assoc m :actual (if (instance? js/Error actual)
                             (do (println "Stacktrace for test: "
                                          (:test-name m) "\n")
                                 (println (.-stack actual))
                                 (.-stack actual))
                             actual))]
    (update-test-failure m)))

(defmethod report :begin-test-ns [{:keys [ns test-env async] :as m}])

(defmethod report :summary [{:keys [test pass fail error] :as test-env}]
  (println "Test summary: " (select-keys test-env [:pass :fail :error]))
  (reset! (cur [:test]) {:pass pass
                         :fail fail
                         :error error
                         :fail-messages @fail-messages}))

(defn run-tests []
  (enable-console-print!)
  (println "Running tests...")
  (reset! fail-messages '())
  (t/run-all-tests))
