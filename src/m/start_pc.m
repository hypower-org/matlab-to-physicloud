%script to load an instance of the physicloud client into
%matlab
%Sam Nelson
%1/26/15
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%if octave.. static path already set, start pc
if (exist ('OCTAVE_VERSION', 'builtin')) 
    pc = javaObject ('physicloud.PhysiCloudClient')

%if matlab, put jar on dpath, import, and start pc
else
    javaaddpath({'physicloud.jar'})
    import edu.gatech.hypower.*
    pc = PhysiCloudClient
end
    
    