%function to hide java syntax while prepping stop data for
%physicloud
%args - cell array of robot ids, vector of x positions,
%       vector of y positions, number of robots
%
%Sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ids] = prep_ids(m_ids, num_agents)
    
    ids = javaArray('java.lang.String', num_agents);
    
    %check to see if we are in Octave
    if (exist ('OCTAVE_VERSION', 'builtin'))
      for i = 1:num_agents
          ids(i) = javaObject ('java.lang.String', m_ids{i});
      end
    %otherwise, in MATLAB
    else
      for i = 1:num_agents
        ids(i) = java.lang.String(m_ids{i});
      end
    end
return 
