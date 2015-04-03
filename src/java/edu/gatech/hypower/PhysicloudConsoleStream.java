package edu.gatech.hypower;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

public class PhysicloudConsoleStream extends OutputStream {
	private JTextArea PhysicloudConsole;
	
    public PhysicloudConsoleStream(JTextArea TextAreaFromClojure) {
        this.PhysicloudConsole = TextAreaFromClojure;
    }
    
	@Override
	public void write(int b) throws IOException {
		PhysicloudConsole.append(String.valueOf((char)b));
		PhysicloudConsole.setCaretPosition(PhysicloudConsole.getDocument().getLength());
	}
}
