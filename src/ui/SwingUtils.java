package ui;

import java.awt.Toolkit;

import javax.swing.JFrame;

public class SwingUtils {
	public static void setCenter(JFrame jf) {
		int screenWidth=Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight=Toolkit.getDefaultToolkit().getScreenSize().height; 
		int jframeWidth = 800;
		int jframeHeight = 600;
		jf.setBounds((screenWidth/2)-(jframeWidth/2), (screenHeight/2)-(jframeHeight/2), 
        		jframeWidth, jframeHeight);
	}
}
