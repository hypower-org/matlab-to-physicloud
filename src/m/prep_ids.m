%function to hide java syntax while prepping stop data for
%physicloud
%args - cell array of robot ids
%
%Sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[j_ids] = prep_ids(m_ids)
    num_agents = length(m_ids);
    j_ids = javaArray('java.lang.String', num_agents);
    %check to see if we are in Octave
    if (exist ('OCTAVE_VERSION', 'builtin'))
      for i = 1:num_agents
          j_ids(i) = javaObject ('java.lang.String', m_ids{i});
      end
    %otherwise, in MATLAB
    else
      for i = 1:num_agents
        j_ids(i) = java.lang.String(m_ids{i});
      end
    end
return 
