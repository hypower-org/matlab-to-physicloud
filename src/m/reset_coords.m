%
%  Author: Samuel <snelso15@ycp.edu>
%  function to wrap reset_coords call
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


function[ret] = reset_coords(pc, id, m_coords)
  j_coords = prep_vals(m_coords);
  pc.resetCoords(id, j_coords);
  ret = 1;
return 