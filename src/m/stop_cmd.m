
%  Author: Samuel <snelso15@ycp.edu>
%  
%  function to wrap pc stop command in amatlab function
%  args can be just the pc object, or a call array of id strings 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ret] = stop_cmd(pc, varargin)
  %if it was passed a second arg, and it is a cell array(presumably of id strings),
  %then call stop on those ids... otherwise, call stop on everyone
  
  if ~isempty(varargin) && iscell(varargin{1})
    ids = varargin{1};
    ids = prep_ids(ids);
    pc.stop(ids);
    ret = 1;
  else
    pc.stop()
    ret = 1;
  end
return 