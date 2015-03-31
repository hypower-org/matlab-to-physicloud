%  Sam Nelson
%  March 27, 2015
%  this script demonstrates a distibuted, multi-agent consensus algorithm
%  that leverages PhysiCloud for communication with the agents
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

x1_log = [];
y1_log = [];
x2_log = [];
y2_log = [];
x3_log = [];
y3_log = [];
while 1
[x1 y1 t1] = get_state(pc, 'robot1');
%z1 = [x1, y1];
[x2 y2 t2] = get_state(pc, 'robot2');
%z2 = [x2, y2];
[x3 y3 t3] = get_state(pc, 'robot3');
%z3 = [x3, y3];
 
%z = [z1; z2; z3];

%z_dot = -1 .* (L * z);
%z1_dot = [z_dot(1,:)];
%z2_dot = [z_dot(2,:)];
%z3_dot = [z_dot(3,:)];

x1_dot  = -2 * x1 + x2 + x3;
x1_next = x1 + 0.2 * x1_dot;
y1_dot  = -2 * y1 + y2 + y3;
y1_next = y1 + 0.2 * y1_dot;

x2_dot  = -2 * x2 + x1 + x3;
x2_next = x2 + 0.2 * x2_dot;
y2_dot  = -2 * y2 + y1 + y3;
y2_next = y2 + 0.2 * y2_dot;

x3_dot  = -2 * x3 + x2 + x1;
x3_next = x3 + 0.2 * x3_dot;
y3_dot  = -2 * y3 + y2 + y1;
y3_next = y3 + 0.2 * y3_dot;


[v1, w1] = gtg(x1, y1, t1, x1_next, y1_next);
[v2, w2] = gtg(x2, y2, t2, x2_next, y2_next);
[v3, w3] = gtg(x3, y3, t3, x3_next, y3_next);

drive_cmd(pc, {'robot1','robot2' ,'robot3'}, [v1, v2, v3], [w1, w2, w3]);
pause(0.2);

x1_log = [x1_log x1];
y1_log = [y1_log y1];
x2_log = [x2_log x2];
y2_log = [y2_log y2];
x3_log = [x3_log x3];
y3_log = [y3_log y3];

figure(1);
scatter(x1_log, y1_log);

figure(2);
scatter(x2_log, y2_log);

figure(3);
scatter(x3_log, y3_log);
end


