package method_2;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AI extends core.player.AI {
    private int steps = 0;
    private Board board = new Board();

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
        Move move = adjacentMove();
        this.board.makeMove(move);
        steps++;
        return move;
    }

    private Move adjacentMove() {
        Random rand = new Random();
        int index1 = rand.nextInt(361);
        while (this.board.get(index1) != PieceColor.EMPTY) {
            index1 = rand.nextInt(361);
        }
        List<Integer> adjacentEmpty = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int x = index1 / 19 + i;
                int y = index1 % 19 + j;
                if (x >= 0 && x < 19 && y >= 0 && y < 19 && this.board.get(x * 19 + y) == PieceColor.EMPTY) {
                    adjacentEmpty.add(x * 19 + y);
                }
            }
        }

        int index2;
        if (adjacentEmpty.isEmpty()) {
            index2 = rand.nextInt(361);
            while (this.board.get(index2) != PieceColor.EMPTY) {
                index2 = rand.nextInt(361);
            }
        } else {
            index2 = adjacentEmpty.get(rand.nextInt(adjacentEmpty.size()));
        }

        return new Move(index1, index2);
    }

    @Override
    public String name() {
        return "method_2";
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
        steps = 0;
    }
}
