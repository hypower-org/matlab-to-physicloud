%script to load an instance of the physicloud client into
%matlab
%Sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  %if octave.. static path already set, start pc
  neighbors = 0;
  
  while (neighbors < 1 || neighbors > 3)
    neighbors = input('How many robots are in the system? (1-3): ');
  end
  
  neighbors = neighbors + 1; %account for this laptop as an agent
  
  cmd = sprintf('java -jar /home/pjmartin/Downloads/mserver2.3.jar 10.10.10.4 %d', neighbors);
  
  rt = javaMethod("getRuntime", "java.lang.Runtime");
  j_process = rt.exec(cmd);
  
  if (exist ('OCTAVE_VERSION', 'builtin')) 
      pc = javaObject ('edu.gatech.hypower.PhysiCloudClient')
  %if matlab, put jar on dpath, import, and start pc
  else
      javaaddpath({'physicloud.jar'})
      import edu.gatech.hypower.*
      pc = PhysiCloudClient
  end
  
  agent_count = 0;
  while (agent_count == 0)
    agent_count = num_agents(pc);
  end

  disp('Creating robot ids variable for workspace...');

  if agent_count == 3
    ids = prep_ids({'robot1', 'robot2', 'robot3'})
  elseif agent_count == 2
    ids = prep_ids({'robot1', 'robot2'})
  else
    ids = prep_ids({'robot1'})
  end
  
  clc;
  disp('Workspace initialized.');
  disp('######################################################')
  disp(strcat(char(10), char(10), char(10), char(10), char(10)));
    
    