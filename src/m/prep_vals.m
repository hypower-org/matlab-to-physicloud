%function to hide java syntax while prepping cmd data for
%physicloud
%sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[ids, x, y] = prep_vals(m_ids, m_x, m_y, num_agents)
    ids = javaArray('java.lang.String', num_agents);
    x = javaArray('java.lang.Double', num_agents);
    y = javaArray('java.lang.Double', num_agents);
    
    for i = 1:num_agents
        ids(i) = java.lang.String(m_ids{i});
        x(i) = java.lang.Double(m_x{i});
        y(i) = java.lang.Double(m_y{i});
    end
return 