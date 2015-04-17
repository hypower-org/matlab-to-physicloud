%script to load an instance of the physicloud client into
%matlab
%Sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  cmd = 'killall -9 java';
  rt = javaMethod("getRuntime", "java.lang.Runtime");
  rt.exec(cmd);