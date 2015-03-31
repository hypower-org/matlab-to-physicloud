%script to load an instance of the physicloud client into
%matlab
%Sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  %if octave.. static path already set, start pc
  if (exist ('OCTAVE_VERSION', 'builtin')) 
      pc = javaObject ('edu.gatech.hypower.PhysiCloudClient')
  %if matlab, put jar on dpath, import, and start pc
  else
      javaaddpath({'physicloud.jar'})
      import edu.gatech.hypower.*
      pc = PhysiCloudClient
  end
  disp('Allowing Physicloud network to initialize...')
  pause(13.5);
  disp('Detecting number of agents in system...')

  agent_count = num_agents(pc)

  disp('Creating robot ids variable for workspace...');

  if agent_count == 3
    ids = prep_ids({'robot1', 'robot2', 'robot3'})
  elseif agent_count == 2
    ids = prep_ids({'robot1', 'robot2'})
  else
    ids = prep_ids({'robot1'})
  end

  disp('Workspace initialized.');
    
    