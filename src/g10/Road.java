package g10;

import core.board.PieceColor;

public class Road {
    public PieceColor color;
    public int stonesNum;
    public PieceColor[] roadColor;
    public Point[] roadPosition;

    public Road(PieceColor c, int num, PieceColor[] roadC, Point[] roadP) {
        this.roadColor = new PieceColor[6];
        this.roadPosition = new Point[6];
        this.color = c;
        this.stonesNum = num;
        System.arraycopy(roadC, 0, this.roadColor, 0, 6);
        System.arraycopy(roadP, 0, this.roadPosition, 0, 6);
        this.roadColor = roadC;
        this.roadPosition = roadP;
    }
}
