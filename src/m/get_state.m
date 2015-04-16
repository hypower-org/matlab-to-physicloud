
%  Author: Samuel <snelso15@ycp.edu>
%  function to wrap pc call in matlab function. 
%  'field' arg should be passed as a string, either x, y, t, or 'all'
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[x, y, t, bumper, cliff, battery, button] = get_state(pc, id)
  j_data = pc.getData(id);
  x = j_data(1);
  y = j_data(2);
  t = j_data(3);
  bumper = j_data(4);
  cliff = j_data(5);
  battery = j_data(6);
  button = j_data(7);
return 
