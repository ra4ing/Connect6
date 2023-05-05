package core.game;


import core.board.Board;
import core.board.PieceColor;
import core.player.Player;
import entity.delegate.SocketDelegate;


public class Referee {
    private final Board _board;
    private final Player _black;
    private final Player _white;
    private String endReason;
    private int steps = 0;

    public Referee(Player black, Player white) {
        this._board = new Board();
        this._board.clear();
        this._black = black;
        this._white = white;
    }


    public Player whoseMove() {
        return (this._board.whoseMove() == PieceColor.WHITE) ? this._white : this._black;
    }


    public boolean gameOver() {
        return this._board.gameOver();
    }


    public void endingGame(String endReason, Player currPlayer, Move currMove) {
        this._black.stopTimer();
        this._white.stopTimer();
        if (currPlayer!=null && currPlayer instanceof SocketDelegate) {
            ((SocketDelegate) currPlayer).sendMove(currMove);
        }
        this.endReason = endReason;
        recordGame();
    }


    public boolean legalMove(Move move) {
        return this._board.legalMove(move);
    }


    public void recordMove(Move move) {
        this._board.makeMove(move);
        this.steps++;
    }


    private void recordGame() {
        GameResult result = new GameResult(this._black.name(), this._white.name(), getWinner(), this.steps,
                this.endReason);
        this._black.addGameResult(result);
        this._white.addGameResult(result);
    }

    private String getWinner() {
        if ("M".equalsIgnoreCase(this.endReason)) {
            return "NONE";
        }
        return (this._board.whoseMove() == PieceColor.WHITE) ? this._black.name() : this._white.name();
    }

    public String gameTitle() {
        return this._black.name() + " vs " + this._white.name();
    }
}