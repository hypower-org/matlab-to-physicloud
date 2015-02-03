%script to load an instance of the physicloud client into
%matlab
%Sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
javaaddpath({'H:\coop\physicloud.jar'})
import matlab.*
pc = PhysiCloudClient