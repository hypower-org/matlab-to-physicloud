
%  Author: Samuel <snelso15@ycp.edu>
%  function to wrap pc call to drive at v w
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ret] = drive_cmd(pc, ids, vs, ws)
  if strcmp(class(ids), 'cell') %this might only work in octave.... check later!
    j_ids = prep_ids(ids);
  else
    j_ids = ids;
  end
  j_vs = prep_vals(vs);
  j_ws = prep_vals(ws);
  pc.drive(j_ids, j_vs, j_ws);
  ret = 1;
return 

