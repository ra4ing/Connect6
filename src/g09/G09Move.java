package g09;

public class G09Move extends core.game.Move {

    public G09Move(int col0, int row0, int col1, int row1) {
        super(col(col0), row(row0), col(col1), row(row1));
    }

    public G09Move(Point point1, Point point2) {
        super(col(point1.c), row(point1.r), col(point2.c), row(point2.r));
    }

}
