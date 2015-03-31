(ns matlab-physicloud.matlab-server
  (:require [watershed.core :as w]
            [manifold.stream :as s]
            [manifold.deferred :as d]
            [physicloud.core :as phy]
            [matlab-physicloud.matlab :as ml])
  (:use [physicloud.utils])
  (:import [java.net ServerSocket Socket SocketException]
           [java.io ObjectOutputStream ObjectInputStream]))

(defn -main 
  [ip neighbors]
  
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
                      
          :provides [:matlab-cmd]}
  
    (w/vertex :matlab-cmd 
               [] 
               (fn [] (s/->source (repeatedly (fn [] (let [cmd-map (ml/to-clj-map (. ml/in readObject))]
                                                       (println "Sending command: " cmd-map)
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
                                               "robot2" (java.util.Vector.  state-vec-2)})]
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
                   (fn [state-map]; (println "Server: pushing data: " state-map)
                     (ml/write-data state-map)) 
                   state-stream)))))





