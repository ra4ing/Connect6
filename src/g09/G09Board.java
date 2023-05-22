package g09;

import core.board.PieceColor;
import core.game.Move;


/**
 * G09Board类，这个类扩展了core.board.Board类，为一个特殊的棋盘提供了一些特定的操作。
 * G09棋盘的棋格被定义为19*19的网格。
 */
public class G09Board extends core.board.Board {
    //全局变量，代表棋盘的索引，便于通过字符访问棋盘位置
    String s="ABCDEFGHIJKLMNOPQRS";//索引棋盘


    /**
     * 根据棋盘坐标获取棋子颜色
     *
     * @param c 列的位置
     * @param r 行的位置
     * @return 棋子的颜色
     */
    public PieceColor get(int c, int r) {
        return super.get(s.charAt(c), s.charAt(r));
    }

    /**
     * 根据起始和目标位置进行一次棋子移动
     *
     * @param c0 列坐标
     * @param r0 行坐标
     * @param c1 列坐标
     * @param r1 行坐标
     */
    public void makeMove(int c0, int r0, int c1, int r1) {
        super.makeMove(new Move(s.charAt(c0), s.charAt(r0), s.charAt(c1), s.charAt(r1)));
    }

    /**
     * 根据指定位置和棋子颜色，执行一步棋子的放置
     *
     * @param c 棋子放置位置的列坐标
     * @param r 棋子放置位置的行坐标
     * @param color 棋子的颜色
     */
    public void makeOneMove(int c, int r, PieceColor color) {
        // 为棋子的位置找到一个唯一的索引。棋盘被视为一维数组，索引通过列号和行号的组合计算得出。
        super.set(c+r*19, color);
    }

    /**
     * 撤销一步棋子的放置
     *
     * @param c 棋子放置位置的列坐标
     * @param r 棋子放置位置的行坐标
     */
    public void unMakeOneMove(int c, int r) {
        // 根据提供的列号和行号找到索引，并将该位置设置为空
        super.set(c+r*19, PieceColor.EMPTY);
    }

}
