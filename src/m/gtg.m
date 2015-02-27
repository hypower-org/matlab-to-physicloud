


%  Author: Samuel <snelso15@ycp.edu>
%  
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[v, w] = gtg(robot_x, robot_y, robot_t, goal_x, goal_y)
 
  delta_x = goal_x - robot_x;
  delta_y = goal_y - robot_y;
  
  theta_desired = atan2(delta_y, delta_x);
  
  dist= sqrt(delta_x^2 + delta_y^2);
  v = dist * cos(theta_desired - robot_t);
  w = dist * sin(theta_desired - robot_t);
  v = v * 1000;
  if v > 250
    v = 250;
  elseif v < -250
    v = -250;
  else
    v = v;
  end
  
  if w > 0.8
    w = 0.8;
  elseif w < -0.8
    w = -0.8;
  else
    w = w;
  end
    
return 

  