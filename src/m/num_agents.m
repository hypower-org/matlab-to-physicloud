
%  Author: Samuel <snelso15@ycp.edu>
%  function to determine number of agents running in system
%  args:
%  pc - physicloudclient object 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ret] = num_agents(pc)
  ret = pc.numAgents();
return 
