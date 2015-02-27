
%  Author: Samuel <snelso15@ycp.edu>
%  function to wrap pc call to drive at v w
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ret] = drive(pc, ids, vs, ws)
  ids = prep_ids(ids);
  vs = prep_vals(vs);
  ws = prep_vals(ws);
  ret = pc.drive(ids, vs, ws);
return 

