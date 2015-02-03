(ns fifteen.dev
  (:require [figwheel.client :as figwheel :include-macros true]
            [weasel.repl :as weasel]
            [reagent.core :as r]
            [fifteen.core :as core]
            [fifteen.testrunner :as testrunner]))

(enable-console-print!)

(figwheel/start
 {:websocket-url "ws://localhost:3449/figwheel-ws"
  :on-jsload (fn []
               (r/force-update-all)
               (testrunner/run-tests))})

(weasel/connect "ws://localhost:9001" :verbose true)

(core/init!)

(testrunner/run-tests)
