%script to load an instance of the physicloud client into
%matlab
%Sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
javaaddpath({'physicloud-client.jar'})
import physicloud.*
pc = PhysiCloudClient