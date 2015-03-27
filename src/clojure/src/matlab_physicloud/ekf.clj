(ns matlab-physicloud.ekf
  (:require [clojure.core.matrix :as mat]))

;vars:
;x- last state [x y t]
;u control vec [v w]
;x' prediction of next state [x y t]
;P- last prediction covaraiance matx
;P' - prediction of prediction covariance matx
;F - jacobian of state transition fn

(def delta-t 0.055) ; 50 ms
(def control (atom [0 0]))
(def state (atom [0 0 0]))
(def measurement [0 0 0])

(def Q (mat/matrix [[0.1 0    0  ]
                    [0    0.1 0  ]
                    [0    0    0.1]]))

(def R (mat/matrix [[0.001 0    0  ]
                    [0    0.001 0  ]
                    [0    0    0.1]]))

(def H (mat/matrix [[1 0 0]
                    [0 1 0]
                    [0 0 1]]))

(defn- jacobian [v t]
  (mat/matrix [[1 0 (* -1 delta-t v (Math/sin t))]
               [0 1 (*  1 delta-t v (Math/cos t))]
               [0 0 1                            ]]))

(defn- state-transition-fn [x y t v w]
  [(+ x (* delta-t v (Math/cos t)))
   (+ y (* delta-t v (Math/sin t)))
   (+ t (* delta-t w))])
  
(defn- predict [x- u P-]
  (let [x' (apply state-transition-fn (concat x- u))
        F  (jacobian (get u 0) (get x- 2))
        P' (mat/add (mat/mmul F P- (mat/transpose F)) Q)]
    [x' P']))

(defn- update [x' P' z]
  (let [G (mat/mmul P' (mat/transpose H) (mat/inverse (mat/add (mat/mmul H P' (mat/transpose H)) R)))
        x (mat/add x' (mat/mmul G (mat/sub z (mat/mmul H x'))))
        P (mat/mmul (mat/sub (mat/identity-matrix 3) (mat/mmul G H)) P')]
    [x P]))

(defn extended-kalman-filter [state control measurement P-]
  (let [[x' P'] (predict state control P-)
        [x P] (update x' P' measurement)]
    [x P]))
    
    
    
    
    
    