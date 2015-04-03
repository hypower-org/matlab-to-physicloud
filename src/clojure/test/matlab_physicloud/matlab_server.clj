(ns matlab-physicloud.matlab-server
  (:require [watershed.core :as w]
            [manifold.stream :as s]
            [manifold.deferred :as d]
            [physicloud.core :as phy]
            [matlab-physicloud.matlab :as ml]
            [physicloud.utils :as util])
  (:use [seesaw.core]
        [seesaw.font])
  (:import [java.net ServerSocket Socket SocketException]
           [java.io ObjectOutputStream ObjectInputStream])
  (:gen-class))

(defn -main 
  [ip ns]
  
  (def neighbors (load-string ns))
  
  (util/initialize-printer 2) ;(2) - print output to physicloud console
  
  (def connection-status (atom 0))
  
  (native!)
  (future
    (let [progress (progress-bar :min 0 :max 100 :value 0 :bounds [10 60 380 40])
          frame (frame 
                  :title "PhysiCloud Notification" 
                  :resizable? false
                  :on-close :nothing
                  :content  
                     (xyz-panel 
                      :items 
                        [(label 
                           :text "Please wait while PhysiCloud establishes network..."
                           :font (font :name :sans-serif :style :bold :size 12)
                           :background java.awt.Color/LIGHT_GRAY
                           :visible?  true
                           :bounds [10 10 380 40])
                         progress]
                       :background java.awt.Color/LIGHT_GRAY))]
      (show! frame)
      (.setLocation frame 300 300)
      (.setSize frame 400 140)
      (loop []
        (if-not @connection-status
          (dispose! frame)
          (do 
            (swap! connection-status inc)
            (.setValue progress @connection-status)
            (Thread/sleep 100) 
            (recur))))))
  
  (ml/start-server)

  (phy/physicloud-instance
         {:ip ip
          :neighbors neighbors
          :requires (cond
                      (= neighbors 4)
                      [:state1 :state2 :state3] 
                      (= neighbors 3)
                      [:state1 :state2] 
                      (= neighbors 2)
                      [:state1])
                      
          :provides [:matlab-cmd]
          :output-preference 2}
  
    (w/vertex :matlab-cmd 
               [] 
               (fn [] (s/->source (repeatedly (fn [] (let [cmd-map (ml/to-clj-map (. ml/in readObject))]
                                                       (util/pc-println "Sending command: " cmd-map)
                                                       cmd-map))))))
                                                       
    ;;build system-state vertex depending on how many robots are in system
    (cond
      (= neighbors 4)
      (w/vertex :system-state 
               [:state1 :state2 :state3]
               (fn [& state-streams] 
                 (s/map 
                   (fn [[state-vec-1
                         state-vec-2
                         state-vec-3]] 
                     (let [system-state-map (java.util.HashMap. 
                                              {"robot1" (java.util.Vector. state-vec-1) 
                                               "robot2" (java.util.Vector. state-vec-2) 
                                               "robot3" (java.util.Vector. state-vec-3)})]
                       system-state-map))
                   (apply s/zip state-streams))))
      
      (= neighbors 3)
      (w/vertex :system-state 
               [:state1 :state2]
               (fn [& state-streams] 
                 (s/map 
                   (fn [[state-vec-1
                         state-vec-2]] 
                     (let [system-state-map (java.util.HashMap. 
                                              {"robot1" (java.util.Vector. state-vec-1) 
                                               "robot2" (java.util.Vector. state-vec-2)})]
                       system-state-map))
                   (apply s/zip state-streams)))) 
      
      (= neighbors 2)
      (w/vertex :system-state 
	              [:state1]
	              (fn [state-stream] 
	                (s/map 
	                  (fn [state-vec-1] 
	                    (let [system-state-map (java.util.HashMap. 
	                                             {"robot1" (java.util.Vector. state-vec-1)})]
	                      system-state-map))
	                  state-stream))))
  
    (w/vertex :matlab-push 
               [:system-state] 
               (fn [state-stream] 
                 (s/consume 
                   (fn [state-map]
                     (if @connection-status (reset! connection-status false))
                     (ml/write-data state-map)) 
                   state-stream)))))





