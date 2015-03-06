idx = 0;
g_x = 0;
g_y = 0;
while idx < 200
  jdata = pc.getData('robot1');
  x = jdata(1);
  y = jdata(2);
  t = jdata(3);
  [v, w] = gtg(x, y, t, g_x, g_y);
  pc.drive(v, w);
  pause(0.1);
  idx = idx + 1;
end 