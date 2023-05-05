package method_1;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.Random;

public class AI extends core.player.AI {
    private int steps = 0;
    private Board board = new Board();
    private final Random rand = new Random();

    public Board setBoard(Board board) {
        return null;
    }

    @Override
    public Move findMove(Move opponentMove) {
        if (opponentMove == null) {
            Move move = firstMove();
            this.board.makeMove(move);
            return move;
        }

        this.board.makeMove(opponentMove);
        Move move = randomMove();
        this.board.makeMove(move);
        steps++;
        return move;
    }

    private Move randomMove() {
        Move move = null;
        while (move == null) {
            move = randomMove(0, 0, 19, 19);
        }
        return move;
    }

    private Move randomMove(int startX, int startY, int width, int height) {
        int index1 = 19 * (rand.nextInt(width) + startX) + (rand.nextInt(height) + startY);
        int index2 = 19 * (rand.nextInt(width) + startX) + (rand.nextInt(height) + startY);
//        System.out.println(index1 + "    " + index2);
        if (index1 != index2 && this.board.get(index1) == PieceColor.EMPTY && this.board.get(index2) == PieceColor.EMPTY) {
            return new Move(index1, index2);
        } else {
            return null;
        }
    }

    @Override
    public String name() {
        return "method_1";
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
        steps = 0;
    }
}
