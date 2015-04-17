(defproject hypower-org/matlab-to-physicloud "0.1.0-SNAPSHOT"
  :description ""
  :url "http://github.com/hypower-org/matlab-to-physicloud"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 ;[hypower-org/physicloud "0.2.0"]
                 [pcloud "0.2.3"] 
                 [hypower-org/watershed "0.1.5"]
                 [manifold "0.1.0-beta10"]
                 [pcc "1.0.0"]
                 [jkobuki "1.2.0"]
                 [phidget "1.0.0"]
                 [jssc "2.8.0"]
                 [seesaw "1.4.5"]]
  :main matlab-physicloud.moving-imu-client
  :aot [matlab-physicloud.moving-imu-client]
 )
 
 