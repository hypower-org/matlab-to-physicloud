%function to hide java syntax while prepping cmd data for
%physicloud
%sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ids] = prep_ids(m_ids, num_agents)
    ids = javaArray('java.lang.String', num_agents);
    for i = 1:num_agents
        ids(i) = java.lang.String(m_ids{i});
    end
return 