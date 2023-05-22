package g09;

import core.game.Move;

/**
 * G09Move类，这个类扩展了core.game.Move类，用于表示G09棋盘上的一次移动。
 * G09棋盘的棋格被定义为19*19的网格，这个类将数字坐标转换为字符表示。
 */
public class G09Move extends Move {

    /**
     * 使用四个整数作为输入来构造一次移动
     * 将列和行的数字坐标转换为对应的字符
     *
     * @param col0 坐标
     * @param row0 行坐标
     * @param col1 列坐标
     * @param row1 行坐标
     */
    public G09Move(int col0, int row0, int col1, int row1) {
        super((char) (col0+'A'), (char)(row0+'A'), (char)(col1+'A'), (char)(row1+'A'));
    }

    /**
     * 使用两个Point对象作为输入来构造一次移动
     * 将Point对象的列和行坐标转换为对应的字符
     *
     * @param p1 Point对象
     * @param p2 Point对象
     */
    public G09Move(Point p1, Point p2) {
        super((char) (p1.c+'A'), (char)(p1.r+'A'), (char)(p2.c+'A'), (char)(p2.r+'A'));
    }


}
