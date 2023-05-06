package g10;

import core.board.PieceColor;
import core.game.Move;


public class G10Board extends core.board.Board {
    String s="ABCDEFGHIJKLMNOPQRS";//��������


    public PieceColor get(int c, int r) {
        return super.get(s.charAt(c), s.charAt(r));
    }

    public void makeMove(int c0, int r0, int c1, int r1) {
        super.makeMove(new Move(s.charAt(c0), s.charAt(r0), s.charAt(c1), s.charAt(r1)));
    }

    public void makeOneMove(int c, int r, PieceColor color) {
        super.set(c+r*19, color);
    }

    public void unMakeOneMove(int c, int r) {
        super.set(c+r*19, PieceColor.EMPTY);
    }

}
