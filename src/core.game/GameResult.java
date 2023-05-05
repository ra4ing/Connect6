package core.game;

public class GameResult {
    private final String black;     //黑方
    private final String white;     //白方
    private final String winner;    //胜方
    private final int steps;
    private final String endReason; //获胜原因

    public GameResult(String black, String white, String winner, int steps, String endReason) {
        this.black = black;
        this.white = white;
        this.winner = winner;
        this.steps = steps;
        this.endReason = endReason;

    }

    /**
     * 棋手name在本次对局中的得分
     * 胜：2分；平：1分；负：0分
     * @param name
     * @return
     */
    public int score(String name) {
        if ("NONE".equals(this.winner))
            return 1;
        if (name.equals(this.winner)) {
            return 2;

        }
        return 0;
    }

    /**
     * 获取棋手name在本次对局中的对手
     * @param name
     * @return name的对手的名字
     */
    public String getOpponent(String name){
        if (this.black.equals(name)){
            return this.white;
        }
        return this.black;
    }

    public String toString() {
        return "\t黑棋：" + this.black + "\n\t白棋：" + this.white + "\n\t胜方：" + this.winner + "\n\t步数：" + this.steps + "\n\t结束原因：" + this.endReason + "\n";

    }
}
