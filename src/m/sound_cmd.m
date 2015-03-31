
%  Author: Samuel <snelso15@ycp.edu>
%  function to wrap kobuki sound play in matlab 
%  args:
%  pc - physicloudclient object 
%  ids - robot ids that the command is to be sent
%  sound -
%  	0 for ON sound
%		1 for OFF sound
%		2 for RECHARGE sound
%		3 for BUTTON sound
%		4 for ERROR sound
%		5 for CLEANINGSTART sound
%		6 for CLEANINGEND sound
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ret] = sound_cmd(pc, ids, sound)
  if (sound < 0 || sound > 6)
    disp('ERROR: sound tag must be integer in rage 0-6');
  else
    pc.sound(ids, sound);
    ret = 1;
  end
return 
