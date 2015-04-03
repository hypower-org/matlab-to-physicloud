%script to kill an instance of the physicloud client
%Sam Nelson
%3/7/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

pc.kill;
j_process.destroy;

disp('Killed the Octave-Java client and PhysiCloud server')