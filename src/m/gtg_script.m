idx = 0;
g_x = 0;
g_y = 0;
while idx < 200
  [x1, y1, t1, rest] = get_state(pc, 'robot1');
  [x2, y2, t2, rest] = get_state(pc, 'robot2');
  [x3, y3, t3, rest] = get_state(pc, 'robot3');
  [v1, w1] = gtg(x1, y1, t1, g_x, g_y);
  [v2, w2] = gtg(x2, y2, t2, g_x, g_y);
  [v3, w3] = gtg(x3, y3, t3, g_x, g_y);
  drive_cmd(pc, ids, [v1, v2, v3], [w1, w2, w3]);
  pause(0.1);
  idx = idx + 1;
end 