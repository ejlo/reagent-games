(ns tic-tac-toe.dev
  (:require [figwheel.client :as figwheel :include-macros true]
            [weasel.repl :as weasel]
            [reagent.core :as r]
            [tic-tac-toe.core :as core]
            [tic-tac-toe.testrunner :as testrunner]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback (fn [] (r/force-update-all)
                     (testrunner/run-tests)))

(weasel/connect "ws://localhost:9001" :verbose true)

(core/init!)

(testrunner/run-tests)
