# matlab-to-physicloud
A repository of Matlab code for interacting with a PhysiCloud instance.
Support is also built in for the open-source version of matlab, Octave.

This code facilitates easy interaction with cyber-physical resources across an instance of [PhysiCloud](http://github.com/hypower-org/physicloud).
In order to connect Matlab with PhysiCloud, a PhysiCloud client must be launched within your Matlab workspace. 
After its creation, the cyber-physical resources are available through simple function calls.

This implimentation of physicloud allows for Kobuki Robots (turtlebots) to be controlled through matlab commands.

The java class PhysiCloudClient communicates with the local instance of physicloud.  This class is responsible for relaying all matlab commands to the network in a way that they can be interpreted and carried out by the robot.  Also, this class provides matlab with system state data when requested to do so.  By implementing these layers of abstration, one can program control algorithms for the robotics system from a well-known, computationally-friendly development environment, rather than from clojure itself.

A series of matlab functions are also provided to hide type conversions and nasty "mat-java" syntax.  

For more detailed documentation, visit the [wiki](https://github.com/hypower-org/matlab-to-physicloud/wiki)




