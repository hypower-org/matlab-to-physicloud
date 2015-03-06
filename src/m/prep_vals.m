
%  Author: Samuel <snelso15@ycp.edu>
%  Created: 2015-02-10

% function to hide java syntax while prepping data for
% physicloud
% args - array of numbers, length of array
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function[j_nums] = prep_vals(m_nums)
    len = length(m_nums);
    j_nums = javaArray('java.lang.Double', len);
    %check to see if we are in Octave
    if (exist ('OCTAVE_VERSION', 'builtin'))
      for i = 1:len
          j_nums(i) = javaObject ('java.lang.Double', m_nums(i));
      end
    %otherwise, in MATLAB
    else
      for i = 1:len
        j_nums(i) = java.lang.Double(m_nums(i));
      end
    end
return 
