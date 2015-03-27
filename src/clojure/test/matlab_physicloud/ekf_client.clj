(ns matlab-physicloud.ekf-client
 (:require [watershed.core :as w]
            [manifold.stream :as s]
            [manifold.deferred :as d]
            [physicloud.core :as phy]
            [matlab-physicloud.matlab :as ml]
            [matlab-physicloud.ekf :as ekf]
            [clojure.core.matrix :as mat])
  (:use [physicloud.utils]
        [incanter.core]
        [incanter.charts])
  (:import [com.phidgets SpatialPhidget]
           [edu.ycp.robotics KobukiRobot]
           [java.util.concurrent Executors]
           [java.util.concurrent ScheduledThreadPoolExecutor])
  (:gen-class))

(def ^ScheduledThreadPoolExecutor exec (Executors/newScheduledThreadPool (* 2 (.availableProcessors (Runtime/getRuntime)))))

(defmacro on-pool
  [^ScheduledThreadPoolExecutor pool & code]
   `(.execute ~pool 
      (fn [] 
        (try ~@code 
          (catch Exception e# 
            (println (str "caught exception: \"" (.getMessage e#) (.printStackTrace e#))))))))



(defn -main []
	;	this agent's properties are loaded from a map in config.clj
	;	config map should look like:
	;	{:id  :robot1
	;	 :ip  "10.10.10.10"
	;	 :start-x 0
	;	 :start-y 0
	;	 :start-t 1.570796}
	(def properties (load-file "config.clj"))
	
	(def spatial (new SpatialPhidget))
	(print "waiting on imu attachment...")
	(.openAny spatial)
	(.waitForAttachment spatial)
	(println "ok")
	
	(def robot (KobukiRobot. "/dev/ttyUSB0"))
	
	(def pi 3.14159265359)
	(def base-length 0.230)
	(def mpt 0.0000852920905)
	
	;;start theta at pi/2 (due to oriention of sensor on robot)
	(def last-state (atom {:x (:start-x properties) :y (:start-y properties) :t (:start-t properties)}))
	
	(def drive-vals (atom nil))
  (def zero-map (atom nil))
  
  (defn plot-progress [num-points]
    (let [p (scatter-plot)]
      (view p)
      (future
        (loop [idx num-points]
          (add-points p [(:x @last-state)] [(:y @last-state)])
          (Thread/sleep 100)
          (if-not (<= idx 0)
            (recur (dec idx)))))))
  

	(defn value-change [new-value, old-value] 
	  "Computes the change between two encoder values."
	  (cond 
	    ;;forward rollover
	    (< (- new-value old-value) -30000) 
	    (+ new-value (- 65535 old-value));;delta = (max - old) + new
	    ;;reverse rollover  
	    (> (- new-value old-value) 30000)
	    (- (- new-value 65535) old-value);;delta = (new - max) - old (this should be negative)
	    ;no encoder rollover event 
	    :else
	    (- new-value old-value)))
	
	(defn odom 
	  [prev-l prev-r prev-x prev-y prev-theta]
	  (let [cur-l (.getLeftEncoder robot)
	        cur-r (.getRightEncoder robot)
	        dl (* mpt (value-change cur-l prev-l))
	        dr (* mpt (value-change cur-r prev-r))
	        dc (/ (+ dr dl) 2)
	        dt (/ (- dr dl) base-length)
	        t (+ prev-theta dt)]
	    [cur-l 
       cur-r
	     (+ prev-x (* dc (Math/cos prev-theta))) 
	     (+ prev-y (* dc (Math/sin prev-theta)))
	     t]))
	  
	(defn imu-step [theta-]
	  (let [dt 0.055 ;use a time step of 55 msec
	        w (.getAngularRate spatial 2)
	        delta-theta (Math/toRadians (* w dt))
	        t (+ theta- delta-theta)]
	    t))

	(defn location-tracker []
	 (Thread/sleep 100) ;to ensure first encoder packet is sent or else the first call to (.getLeftEncoder robot) returns 0 and causes problems!
   (loop [
	         prev-l (.getLeftEncoder robot)
	         prev-r (.getRightEncoder robot)
	         prev-x 0
	         prev-y 0
	         prev-theta (/ pi 2)
           P-  (mat/identity-matrix 3)]
	    (Thread/sleep 50)
	    (let [theta (imu-step prev-theta)
	          [l r x y t] (odom prev-l prev-r prev-x prev-y prev-theta)
	          theta (if(< (Math/abs (- t theta)) pi)
	                    (/ (+ theta t) 2)
	                    theta)
            [[kfx kfy kftheta] P] (ekf/extended-kalman-filter [(:x @last-state) (:y @last-state) (:t @last-state)] ;arg - state
                                                              (if @drive-vals ;arg - control
                                                                [(* 0.001 (:v @drive-vals)) (:w @drive-vals)]
                                                                [0 0])
                                                              [x y theta] ; measurements USING ODOM ONLY (t)
                                                              P-)] ; last prediction covariance matx
       
        ;;if a zero command was received, zero all keys that were in the zero map
        (if @zero-map
          (do
            (doseq [key (keys @zero-map)]
              (if (contains? @last-state key)
                (if-not (= key :t)
                  (swap! last-state assoc key 0)
                  (swap! last-state assoc key 1.570796))))
            (reset! zero-map nil))
          
          ;;otherwise, update normally.
          (reset! last-state {:x kfx :y kfy :t kftheta}))
	      (recur l r (:x @last-state) (:y @last-state) (:t @last-state) P))))
	 

	(defn stop-handler [cmd-map]
	"the stop command map should look something like this:
		{:command stop
		 :ids [robot1 robot2]}
		if no stop command is sent for a specific robot, its id is omitted from the ids vector
		if all robots should stop, ids key is omitted from map"
	 
	  (let [ids (:ids cmd-map)
	        my-id-str (name (:id properties))]
	    (if ids
	      ;;if some ids are sent, see if my id is in the list to stop
	      (if (some (fn [x] (= my-id-str x)) ids)
	        (reset! drive-vals nil)
	        "stop command did not have my specific id, no command sent")
	      ;;if no ids sent, all bots should stop
	      (reset! drive-vals nil))))
	
	
	(defn drive-handler [cmd-map]
	"the drive command map should look something like this:
		{:command drive
		 :robot1 [v w]
     :robot2 [v w]
		 :v v
		 :w w}
		if no drive command is sent for a specific robot, its id is omitted from the ids vector
		if all robots should drive, ids key is omitted from map"
	 
	  (let [my-id-key (:id properties)
	        my-velocity-vals (my-id-key cmd-map) ;my id will be in the map if there is a specific v and w cmd for me
          v (:v cmd-map) ;v and w will be in map only if all robots should drive at that v and w
	        w (:w cmd-map)
	        my-id-str (name (:id properties))]
	     (if my-velocity-vals
		     (reset! drive-vals {:v (get my-velocity-vals 0) :w (get my-velocity-vals 1)})
         (if v 
           (reset! drive-vals {:v v :w w})))))
	

	(defn zero-handler [cmd-map]
	"the zero command map should look something like this:
		{:command zero
		 :ids [robot1]
		 :x zero
		 :y zero}
		if no zero command is sent for a specific robot, its id is omitted from the ids vector
		if all robots should zero, ids key is omitted from map
		any variables that should be zero-ed will be in the map as keys"
	 
	  (let[ids (:ids cmd-map)
	        x (:x cmd-map)
	        y (:y cmd-map)
	        t (:t cmd-map)
	        my-id-str (name (:id properties))]
	     (if ids
		    ;;if some ids are sent, see if my id is in the list to zero
		     (if (some (fn [x] (= my-id-str x)) ids)
	         (reset! zero-map (dissoc cmd-map :command :ids)))
		    ;;if no ids sent, all bots should zero
		     (reset! zero-map (dissoc cmd-map :command :ids)))))
	 

	(defn cmd-handler [cmd-map]
		(let [cmd (:command cmd-map)]
		  (println "COMMAND RECEIVED: " cmd)
		  (cond
	     
		    (= cmd "stop")
		    (stop-handler cmd-map)
	     
		    (= cmd "drive")
		    (drive-handler cmd-map)
	     
		    (= cmd "zero")
		    (zero-handler cmd-map)
	     
		    :else
		    (do
		      (println "unsupported command")
		      "unsupported command"))))
 
 
  (defn motor-controller []
    (loop []
      (if @drive-vals
        (.control robot (:v @drive-vals) (:w @drive-vals))
        (.control robot 0 0))
      (Thread/sleep 50);;issue new motor command every 1/20 of a second
      (recur)))
  
	(on-pool exec (location-tracker))
  (on-pool exec (motor-controller))
	(on-pool exec (phy/physicloud-instance
	                   {:ip (:ip properties)
	                    :neighbors (:neighbors properties)
	                    :requires [:matlab-cmd] 
	                               ;provides either state1, state2, or state3
	                    :provides [(keyword (str "state" (last (str (:id properties)))))]}
	  
	              (w/vertex :control  
	                         [:matlab-cmd] 
	                         (fn [cmd-stream]
	                           (s/consume 
	                             (fn [cmd-map] (cmd-handler cmd-map))
	                             cmd-stream)))
	    
	                        ;this vertex is either :state1, :state2, or :state3
	              (w/vertex (keyword (str "state" (last (str (:id properties)))))
	                         [] 
	                         (fn [] 
	                           (s/periodically 
	                             500 
	                             (fn [] (let [state-vec[(:x @last-state) (:y @last-state) (:t @last-state)]]
                                        state-vec))))))))

