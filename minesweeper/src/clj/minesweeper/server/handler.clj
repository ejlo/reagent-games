(ns minesweeper.server.handler
  (:require [clojure.java.io :as io]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [environ.core :refer [env]]
            [prone.middleware :refer [wrap-exceptions]]
            [minesweeper.server.services :refer [browser-repl]]))

(defroutes routes
  (GET "/" [] (io/resource "public/index.html"))
  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (wrap-defaults routes site-defaults)]
    (if (env :dev?) (wrap-exceptions handler) handler)))
