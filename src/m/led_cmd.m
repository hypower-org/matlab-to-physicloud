
%  Author: Samuel <snelso15@ycp.edu>
%  function to wrap led toggle in matlab 
%  args:
%  pc - physicloudclient object 
%  ids - robot ids that the command is to be sent
%  led - 1 or 2 to indicate which led
%  color - either 'red', 'green', or 'off'
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ret] = led_cmd(pc, ids, led, color)
  if (led ~= 1 && led ~=2)
    disp('ERROR:  LED must be 1 or 2');
  elseif (~strcmp('red', color) && ~strcmp('green', color) && ~strcmp('off', color))
    disp('ERROR: color must be "red", "green", or "off"');
  else
    pc.led(ids, led, color);
    ret = 1;
  end
return 
