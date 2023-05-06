import core.match.GameEvent;

public class GenerateScores {
    public static void main(String[] args) {
        g09.AI basic = new g09.AI();
        int[] baseScores = {14, 66, 153, 790, 844, 100000};
        int[] tmpScore = {0, 0, 0, 0, 0, 0};
        int[] search = {0, 0, 0, 0, 0, 0};
        int idx = 0;
        while (search[4] < 10) {
            search[0]++;
            for (int j=4;j>=1;j--) {
                search[j-1] = search[j] / 10;
                search[j] %= 10;
            }
            GameEvent event = new GameEvent("Report " + idx);
            g10.AI test = new g10.AI();
            for (int j=0; j<5; j++) {
                tmpScore[i] = baseScores[i] + search[i]*50;
            }
            test.scores = baseScores;
            event.addPlayer(basic);
            event.addPlayer(test);

            event.arrangeMatches(100);
            event.runMultiThread();
            event.showStatistics();
        }

    }
}
