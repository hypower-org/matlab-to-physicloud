
%  Author: Samuel <snelso15@ycp.edu>
%  function to wrap pc call in matlab function. 
%  'field' arg should be passed as a string, either x, y, t, or 'all'
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ret] = reset_state_vars(pc, id, field)
  pc.zero(id, field);
  ret = 1;
return 
