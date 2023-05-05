/**
 * 
 */
package entity.delegate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import core.game.Move;
import core.player.Player;

/**
 * @author ÷Ï¡’
 *
 */
public class SocketDelegate extends Player implements Observer{
	private final String name;
	PrintStream out = null;
	private volatile String moveStr = null;
	
	public SocketDelegate(String name,Socket socket) {
		super();
		this.name = name;
		try {
			out = new PrintStream(socket.getOutputStream());
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to a Server");
	}
	
	/* (non-Javadoc)
	 * @see core.player.Player#isManual()
	 */
	@Override
	public boolean isManual() {
		return false;
	}

	/* (non-Javadoc)
	 * @see core.player.Player#name()
	 */
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return name;
	}

	/* (non-Javadoc)
	 * @see core.player.Player#findMove(core.game.Move)
	 */
	@Override
	public Move findMove(Move opponentMove) {//∑¢ÀÕplaychess√¸¡Ó
		if (opponentMove != null) sendMove(opponentMove);
		
		Move move = null;
		while(moveStr == null){
			
		}
		move = Move.parseMove(moveStr);
		moveStr = null;
		return move;
	}

	public void sendMove(Move currentMove) {
		// TODO Auto-generated method stub
		out.println("Move@" + currentMove);
		out.flush();
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		StringTokenizer stringTokenizer = new StringTokenizer((String)arg, "@");
		String flag = stringTokenizer.nextToken(); // ±Í ∂∑˚
		if(flag.equals("Move")){
			this.moveStr=stringTokenizer.nextToken();
		}
	}

}
