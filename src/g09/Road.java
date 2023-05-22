package g09;

import core.board.PieceColor;

/**
 * Road类，代表棋盘上的一条路。
 * 这个类包含路上棋子的颜色、数量，以及每个棋子的颜色和位置信息。
 */
public class Road {
    // 路上的棋子的颜色
    public PieceColor color;
    // 路上的棋子的数量
    public int stonesNum;
    // 路上每个棋子的颜色
    public PieceColor[] roadColor;
    // 路上每个棋子的位置
    public Point[] roadPosition;

    /**
     * 构造一个新的Road实例。
     *
     * @param c 路上棋子的颜色
     * @param num 路上棋子的数量
     * @param roadC 路上每个棋子的颜色
     * @param roadP 路上每个棋子的位置
     */
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
