%  Sam Nelson
%  March 27, 2015
%  this script demonstrates a distibuted, multi-agent consensus algorithm
%  that leverages PhysiCloud for communication with the agents
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

x1_log = [];
y1_log = [];
x2_log = [];
y2_log = [];

while 1
[x1 y1 t1] = get_state(pc, 'robot1');
[x2 y2 t2] = get_state(pc, 'robot2');

x1_dot  = x2-x1;
x1_next = x1 + 0.2 * x1_dot;
y1_dot  = y2-y1;
y1_next = y1 + 0.2 * y1_dot;

x2_dot  = x1-x2;
x2_next = x2 + 0.2 * x2_dot;
y2_dot  = y1-y2;
y2_next = y2 + 0.2 * y2_dot;

[v1, w1] = gtg(x1, y1, t1, x1_next, y1_next);
[v2, w2] = gtg(x2, y2, t2, x2_next, y2_next);

drive_cmd(pc, {'robot1','robot2'}, [v1, v2], [w1, w2]);
pause(0.2);

x1_log = [x1_log x1];
y1_log = [y1_log y1];
x2_log = [x2_log x2];
y2_log = [y2_log y2];

end


