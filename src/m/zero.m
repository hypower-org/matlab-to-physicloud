
%  Author: Samuel <snelso15@ycp.edu>
%  function to wrap pc call in matlab function. 
%  'field' arg should be passed as a string, either x, y, t, or 'all'
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ret] = zero(pc, id, field)
  ret = pc.zero(id, field);
return 
