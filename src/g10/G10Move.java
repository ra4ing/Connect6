package g10;

import core.game.Move;

public class G10Move extends Move {

    public G10Move(int col0, int row0, int col1, int row1) {
        super((char) (col0+'A'), (char)(row0+'A'), (char)(col1+'A'), (char)(row1+'A'));
    }

    public G10Move(Point p1, Point p2) {
        super((char) (p1.c+'A'), (char)(p1.r+'A'), (char)(p2.c+'A'), (char)(p2.r+'A'));
    }


}
