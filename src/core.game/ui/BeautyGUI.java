package core.game.ui;

import java.awt.Frame;
import java.util.Observable;

import core.game.timer.GameTimer;
import jagoclient.board.GoFrame;

public class BeautyGUI implements GameUI {
	
	GoFrame frame = null;
	
	public BeautyGUI(String title) {
		frame = new GoFrame(new Frame(), title);
	}

	@Override
	public void update(Observable o, Object arg) {

		char move[] = arg.toString().toCharArray();
		
		int i = move[0] - 'A';
    	int j = move[1] - 'A';	    	
    	frame.B.set(j, i);
    	frame.B.showinformation();
    	frame.B.copy();
    	
    	i = move[2] - 'A';
    	j = move[3] - 'A';
    	frame.B.set(j, i);
    	frame.B.showinformation();
    	frame.B.copy();
	}

	@Override
	public void setTimer(GameTimer bTimer, GameTimer wTimer) {
		
	}
	
}
