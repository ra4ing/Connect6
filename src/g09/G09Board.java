package g09;

import core.board.PieceColor;

import java.util.Random;

import static core.game.Move.col;
import static core.game.Move.row;

public class G09Board extends core.board.Board {
    public static int[][][] zobristTable = new int[19][19][3];//zobrist

    static {
        Random r = new Random();
        for (int i = 0; i < 19; ++i) {
            for (int j = 0; j < 19; ++j) {
                for (int k = 0; k < 3; ++k) {
                    zobristTable[i][j][k] = r.nextInt();
                }
            }
        }
    }

    public int hashcode()//获取当前棋盘的zobrist哈希值
    {
        int hash = 0;
        for (char i = 'A'; i <= 'S'; ++i) {
            for (char j = 'A'; j <= 'S'; ++j) {
                int k;
                if (super.get(i, j) == PieceColor.EMPTY) k = 0;
                else if (super.get(i, j) == PieceColor.BLACK) k = 1;
                else k = 2;
                hash ^= zobristTable[i][j][k];
            }
        }
        return hash;
    }


    public PieceColor get(int i, int j) {
        return super.get(col(i), row(j));
    }

    public void makeMove(int c0, int r0, int c1, int r1) {
        super.makeMove(new G09Move(c0, r0, c1, r1));
    }

    public void makeOneMove(int c, int r, PieceColor color) {
        super.set(col(c), row(r), color);
    }

    public void unMakeOneMove(int c, int r) {
        super.set(col(c), row(r), PieceColor.EMPTY);
    }

}
