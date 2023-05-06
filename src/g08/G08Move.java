package g08;

import core.game.Move;
import g09.Point;

public class G08Move extends Move {
    public G08Move(int col0, int row0, int col1, int row1) {
        super((char) (col0+'A'), (char)(row0+'A'), (char)(col1+'A'), (char)(row1+'A'));
    }

    public G08Move(g09.Point p1, Point p2) {
        super((char) (p1.c+'A'), (char)(p1.r+'A'), (char)(p2.c+'A'), (char)(p2.r+'A'));
    }
}
