(ns minesweeper.state
  (:require [reagent.core :as reagent]))

(defonce sizes {:small  [[9 9] 10]
                :medium [[16 16] 40]
                :big    [[16 30] 99]})

(defn now []
  (.getTime (js/Date.)))

(defn empty-board-state [[y x :as size]]
  (assert (and (<= 1 x 1000) (<= 1 y 1000)) (str "x: " x ", y: " y))
  (let [empty-tiles (vec (repeat y (vec (repeat x {:n 0 :hidden true}))))]
    {:tiles empty-tiles
     :size  size}))

(def empty-info-state
  {:mines 0
   :marked 0
   :shown 0
   :time {:time-elapsed 0}})

(defn init-state! [size]
  {:board (empty-board-state (if (keyword? size)
                               (first (size sizes))
                               size))
   :info empty-info-state
   :size-form-value (if (keyword? size) size :custom)
   :status nil
   :result nil})

(defonce app-state (reagent/atom (init-state! :medium)))

(def cursor
  (memoize
   (fn [atom path]
     (reagent/cursor atom path))))

(defn cur
  ([path]
   (cursor app-state path))
  ([atom path]
   (cursor atom path)))



(defn neighbors [[y-size x-size] y-pos x-pos filter-fn]
  (for [x (range (max 0 (dec x-pos))
                 (inc (min (dec x-size) (inc x-pos))))
        y (range (max 0 (dec y-pos))
                 (inc (min (dec y-size) (inc y-pos))))
        :when (and (not (and (= x x-pos) (= y y-pos)))
                   (or (nil? filter-fn) (filter-fn y x)))]
    [y x]))

(defn put-mine [tiles size y-mine x-mine]
  (let [tiles     (assoc-in tiles [y-mine x-mine :mine] true)
        neighbors (neighbors size y-mine x-mine nil)]
    (reduce
     (fn [t [y x]]
       (update-in t [y x :n] inc))
     tiles
     neighbors)))

(defn put-random-mines! [new-mines]
  (let [[y-size x-size :as size] @(cur [:board :size])]
    (assert (<= 0 (+ @(cur [:info :mines]) new-mines) (dec (* y-size x-size)))
            (str "x-size: " x-size ", y-size: " y-size ", mines: "
                 @(cur [:info :mines]) ", new-mines: " new-mines))
    (swap! (cur [:info :mines]) #(+ new-mines %))
    (loop [tiles @(cur [:board :tiles])
           mines-left new-mines]
      (if (zero? mines-left)
        (reset! (cur [:board :tiles]) tiles)
        (let [x (rand-int x-size)
              y (rand-int y-size)]
          (if (:mine (get-in tiles [y x]))
            (recur tiles mines-left)
            (recur (put-mine tiles size y x)
                   (dec mines-left))))))))

(defn reset-state! [s]
  (reset! app-state s))

(defn clean-state! [size]
  (reset-state! (init-state! size)))

(defn playing? []
  (let [s @(cur [:status])]
    (or (= :playing s)
        (= :ready s))))

(def boom-imgs
  (list ^{:key 1} [:img.crater {:src "img/burn_mark.png"}]
        ^{:key 2} [:img.explosion {:src "img/explosion.png"}]))

(defn tile-class-and-content [y x]
  (let [{:keys [n mine mark boom hidden]} @(cur [:board :tiles y x])
        success? #(= :success @(cur [:result]))]
    (cond
      boom                                        [:boom boom-imgs]
      (and mark (or mine (playing?)))             [:mark ""]
      (and hidden (or (and (not mark) (not mine))
                      (playing?)))                [:hidden ""]
      (and (not hidden) (not mine) (not mark))    [(str "number nr" n) n]
      mark                                        [:bad-mark "✕"]
      (and hidden (success?))                     [:mark ""]
      hidden                                      [:hidden-mine ""]
      :default nil)))

(defn update-clock! []
  (reset! (cur [:info :time :time-elapsed])
          (- (now) @(cur [:info :time :start-time]))))

(defn stop-clock! []
  (when-let [id @(cur [:info :time :clock-updater-id])]
    (update-clock!)
    (js/clearInterval id)
    (reset! (cur [:info :time :clock-updater-id]) nil)))

(defn start-clock! []
  (reset! (cur [:info :time :start-time]) (now))
  (reset! (cur [:info :time :time-elapsed]) 0)
  (reset! (cur [:info :time :clock-updater-id])
          (js/setInterval update-clock! 1000)))

(defn end-game! [result]
  (reset! (cur [:status]) :finished)
  (reset! (cur [:result]) result)
  (stop-clock!))

(defn boom! [y x]
  (reset! (cur [:board :tiles y x :boom]) true)
  (end-game! :failure))

(defn start-game!
  ([size mines]
   (when (playing?)
     (end-game! :failure))
   (clean-state! size)
   (put-random-mines! mines)
   (reset! (cur [:status]) :ready))
  ([type]
   (apply start-game! (sizes (keyword type)))))

(defn won? []
  (= (+ @(cur [:info :shown]) @(cur [:info :mines]))
     (apply * @(cur [:board :size]))))

(defn _show-tile [y x]
  (reset! (cur [:board :tiles y x :hidden]) false)
  (swap! (cur [:info :shown]) inc)
  (when @(cur [:board :tiles y x :mine])
    (boom! y x)))


;; flood-show helpers

(defn get-tile [tiles y x]
  (nth (nth tiles y) x))

(defn __show-tile [tiles [y x]]
  (assoc-in tiles [y x :hidden] false))

(defn showable? [tiles y x]
  (let [t (get-tile tiles y x)]
    (and (:hidden t)
         (not (:mark t)))))

(defn zero-unmarked? [tiles y x]
  (let [t (get-tile tiles y x)]
    (and (zero? (:n t))
         (not (:mark t)))))

(defn flood-show [pos]
  (let [size @(cur [:board :size])]
    (loop [positions (list pos)
           tiles @(cur [:board :tiles])
           show-count 0]
      (if (empty? positions)
        (do
          (reset! (cur [:board :tiles]) tiles)
          (swap! (cur [:info :shown]) #(+ show-count %)))
        (let [[[y x] & rest] positions
              neighbs (neighbors size y x (fn [y x] (showable? tiles y x)))
              tiles (reduce __show-tile tiles neighbs)]
          (recur (into rest
                       (filter
                        (fn [[y x]] (zero-unmarked? tiles y x))
                        neighbs))
                 tiles
                 (+ show-count (count neighbs))))))))

(defn show-tile [y x]
  (when (= @(cur [:status]) :ready)
    (if @(cur [:board :tiles y x :mine])
      ;;failing at the first attempt is not much fun
      (let [mines @(cur [:info :mines])]
        (clean-state! @(cur [:board :size]))
        (put-random-mines! mines)
        (reset! (cur [:status]) :ready)
        (show-tile y x))
      (do
        (start-clock!)
        (reset! (cur [:status]) :playing))))

  (when (and (playing?) (and @(cur [:board :tiles y x :hidden])
                             (not @(cur [:board :tiles y x :mark]))))
    (_show-tile y x)
    (when (and (not @(cur [:board :tiles y x :mine]))
               (zero? @(cur [:board :tiles y x :n])))
      (flood-show [y x]))
    (when (and (playing?) (won?))
      (end-game! :success))))

(defn mark-tile [y x]
  (when (and (playing?) @(cur [:board :tiles y x :hidden]))
    (let [change (if @(cur [:board :tiles y x :mark]) -1 1)]
      (swap! (cur [:board :tiles y x :mark]) not)
      (swap! (cur [:info :marked]) #(+ % change)))))
