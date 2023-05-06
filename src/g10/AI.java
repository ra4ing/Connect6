package g10;

import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javafx.util.Pair;

import static java.lang.Math.max;

public class AI extends core.player.AI {
    private PieceColor selfColor = PieceColor.EMPTY;
    G10Board board = new G10Board();
    private ArrayList<Road> selfRoads;
    private ArrayList<Road> opponentRoads;

    /**
     * Return a legal move for me according to my opponent's move, and at that
     * moment, I am facing a board after the opponent's move. Abstract method to be
     * implemented by subclasses.
     *
     * @param opponentMove �Է��ƶ�����
     * @return �����ƶ�����
     */
    @Override
    public Move findMove(Move opponentMove) {
        Move move = findFirstMove(opponentMove);
        if (move != null) {
            this.board.makeMove(move);
            return move;
        }
        board.makeMove(opponentMove);

        // ɨ��ȫ�����ɺڰ�������·
        setRoads();

        // Ѱ��ʤ��
        move = findWinMove();
        if (move != null) {
            this.board.makeMove(move);
            return move;
        }

        // ��в���� ������onePoint��ָ������ֻ��һ�����ӷ��أ�
        move = findThreatMove();
        if (move != null) {
            this.board.makeMove(move);
            return move;
        }

        // ���������� ������onePoint�� ���������Ѱ��·����
        move = findBestMove();
        this.board.makeMove(move);
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
        Random rand = new Random();
        int index1 = 19 * (rand.nextInt(width) + startX) + (rand.nextInt(height) + startY);
        int index2 = 19 * (rand.nextInt(width) + startX) + (rand.nextInt(height) + startY);
//        System.out.println(index1 + "    " + index2);
        if (index1 != index2 && this.board.get(index1) == PieceColor.EMPTY && this.board.get(index2) == PieceColor.EMPTY) {
            return new Move(index1, index2);
        } else {
            return null;
        }
    }

    private final int maxDepth = 1;
    private Move bestMove = null;

    private Move findBestMove() {
        this.bestMove = null;
        alphaBeta(-Integer.MAX_VALUE, Integer.MAX_VALUE, maxDepth);
        return bestMove;
    }

    private int alphaBeta(int alpha, int beta, int depth) {
        int walkValue, bestValue = -Integer.MAX_VALUE;
        if (depth == 0 || this.board.gameOver()) {
            return evaluate();
        }

        ArrayList<Move> moves = generateMove(depth);
        for (Move move : moves) {
            board.makeMove(move);
            walkValue = -alphaBeta(-beta, -alpha, depth - 1);
            board.undo();
            if (walkValue > bestValue) {
                bestValue = walkValue;
                alpha = max(bestValue, alpha);
                if (walkValue >= beta) break;
            }

            if (depth == maxDepth && walkValue >= bestValue) bestMove = move;
        }

        return bestValue;
    }

    private ArrayList<Move> generateMove(int depth) {
        ArrayList<Move> moves = new ArrayList<>();
        if (depth == maxDepth && onePoint != null) {
            this.board.makeOneMove(onePoint.c, onePoint.r, board.whoseMove());
            ArrayList<Point> points = generatePoint(20);
            this.board.unMakeOneMove(onePoint.c, onePoint.r);

            for (Point p : points) {
                moves.add(new G10Move(onePoint.c, onePoint.r, p.c, p.r));
            }
            onePoint = null;
        } else {
            ArrayList<Point> points1 = generatePoint(7);
            for (Point p1 : points1) {
                this.board.makeOneMove(p1.c, p1.r, board.whoseMove());
                ArrayList<Point> points2 = generatePoint(7);
                this.board.unMakeOneMove(p1.c, p1.r);
                for (Point p2 : points2) {
                    moves.add(new G10Move(p1.c, p1.r, p2.c, p2.r));
                }
            }

        }
        return moves;
    }

    private ArrayList<Point> generatePoint(int num) {
        ArrayList<Point> points = new ArrayList<>();
        if (this.board.gameOver()) {
            Point p = generateRandomPosition();
            points.add(p);
            return points;
        }
        HashMap<Integer, ArrayList<Road>> roads = generateRoads();
        Pair<Integer, Integer> values = getSelfAndOpponentValues(roads);
        int selfValue = values.getKey();
        ArrayList<Road> selfR = roads.get(selfValue);

        //������������
        int[] add = new int[]{1, 10, 100, 1000, 10000};
        int[] scores = new int[361];
        for (int i = 0; i < 361; ++i) {
            scores[i] = 0;
        }
        for (Road road : selfR) {
            for (int i = 0; i < 6; ++i) {
                int index = road.roadPosition[i].r * 19 + road.roadPosition[i].c;
                if (road.roadColor[i] == PieceColor.EMPTY) {
                    scores[index] += add[road.stonesNum - 1];
                } else {
                    scores[index] = -1;
                }
            }
        }
        //ȡ�÷�����num����
        int[] positions = new int[num];
        int maxScore;
        for (int j = 0; j < num; ++j) {
            positions[j] = 0;
            maxScore = -1;
            for (int i = 0; i < 361; ++i) {
                if (scores[i] > maxScore) {
                    maxScore = scores[i];
                    positions[j] = i;
                }
            }
            scores[positions[j]] = -1;
            points.add(new Point(positions[j] % 19, positions[j] / 19));
        }

        return points;
    }

    private int evaluate() {
        HashMap<Integer, ArrayList<Road>> roads = generateRoads();
        int whiteValue = (int) roads.keySet().toArray()[0];
        int blackValue = (int) roads.keySet().toArray()[1];
        if (whiteValue < 0) {
            int t = whiteValue;
            whiteValue = blackValue;
            blackValue = t;
        }
        blackValue = -blackValue;
        int selfValue = (board.whoseMove() == PieceColor.WHITE ? whiteValue : blackValue);
        int opponentValue = whiteValue + blackValue - selfValue;
        return selfValue - opponentValue;
    }

    private Point onePoint = null;

    private Move findThreatMove() {
        int[] scores = new int[361];
        initializeScores(board, scores);

        int threatenLevel = evaluateThreatenLevel(opponentRoads, scores);

        int[] topPositions = findTopPositions(scores);

        if (threatenLevel == 2) {
            return new G10Move(topPositions[0] % 19, topPositions[0] / 19, topPositions[1] % 19, topPositions[1] / 19);
        } else if (threatenLevel == 1 || threatenLevel == 0) {
            onePoint = new Point(topPositions[0] % 19, topPositions[0] / 19);
        }
        return null;
    }

    private void initializeScores(G10Board board, int[] scores) {
        for (int i = 0; i < 361; ++i) {
            if (board.get(i % 19, i / 19) != PieceColor.EMPTY) scores[i] = -1;
        }
    }

    private int evaluateThreatenLevel(ArrayList<Road> opponentRoads, int[] scores) {
        int threatenLevel = -1;

        for (Road road : opponentRoads) {
            int stonesNum = road.stonesNum;

            if (stonesNum == 3) {
                threatenLevel = handleThreeStones(road, scores, threatenLevel);
            } else if (stonesNum == 4) {
                threatenLevel = handleFourStones(road, scores, threatenLevel);
            } else if (stonesNum == 5) {
                threatenLevel = handleFiveStones(road, scores, threatenLevel);
            }
        }
        return threatenLevel;
    }

    private int handleThreeStones(Road opponentRoad, int[] scores, int threatenLevel) {
        int[] positions = new int[3];
        for (int i = 0, cnt = 0; i < 6; ++i) {
            if (opponentRoad.roadColor[i] == PieceColor.EMPTY) {
                positions[cnt++] = opponentRoad.roadPosition[i].r * 19 + opponentRoad.roadPosition[i].c;
            }
        }
        Arrays.sort(positions);

        int THREE = 100;
        scores[positions[2]] += THREE;
        if (threatenLevel == -1) threatenLevel = 0;
        return threatenLevel;
    }

    private int handleFourStones(Road opponentRoad, int[] scores, int threatenLevel) {
        int mid = (opponentRoad.roadPosition[2].r * 19 + opponentRoad.roadPosition[2].c + opponentRoad.roadPosition[1].r * 19 + opponentRoad.roadPosition[1].c) / 2;
        int[] positions = new int[2];
        for (int i = 0, idx = 0; i < 6 && idx < 2; ++i) {
            if (opponentRoad.roadColor[i] == PieceColor.EMPTY) {
                positions[idx++] = opponentRoad.roadPosition[i].r * 19 + opponentRoad.roadPosition[i].c;
            }
        }

        int FOUR = 1000;
        if (euclid(positions[0], positions[1], 0) + euclid(positions[0], positions[1], 1) < 50) {
            if (threatenLevel <= 1) threatenLevel++;

            if (euclid(positions[0], mid, 0) + euclid(positions[0], mid, 1) < euclid(positions[1], mid, 0) + euclid(positions[0], mid, 1)) {
                scores[positions[0]] += FOUR;
            } else {
                scores[positions[1]] += FOUR;
            }
        } else {
            scores[positions[0]] += FOUR;
            scores[positions[1]] += FOUR;
            threatenLevel = 2;
        }
        return threatenLevel;
    }

    private int euclid(int a, int b, int flag) {
        if (flag == 0) {
            a /= 19;
            b /= 19;
        } else {
            a %= 19;
            b %= 19;
        }
        return (a - b) * (a - b);
    }

    private int handleFiveStones(Road opponentRoad, int[] scores, int threatenLevel) {
        if (threatenLevel == 0) threatenLevel = 1;
        else if (threatenLevel == 1) threatenLevel = 2;
        int postions = 0;
        for (int j = 0; j < 6; ++j) {
            if (opponentRoad.roadColor[j] == PieceColor.EMPTY) {
                postions = opponentRoad.roadPosition[j].r * 19 + opponentRoad.roadPosition[j].c;
                break;
            }
        }
        int FIVE = 100000;
        scores[postions] += FIVE;
        return threatenLevel;
    }


    private int[] findTopPositions(int[] scores) {
        int[] topPositions = new int[2];

        for (int i = 0; i < 2; ++i) {
            int maxScore = -1;
            for (int j = 0; j < scores.length; ++j) {
                if (scores[j] > maxScore) {
                    maxScore = scores[j];
                    topPositions[i] = j;
                }
            }
            scores[topPositions[i]] = -1;
        }

        return topPositions;
    }


    private void setRoads() {
        HashMap<Integer, ArrayList<Road>> roads = generateRoads();
        Pair<Integer, Integer> values = getSelfAndOpponentValues(roads);
        selfRoads = roads.get(values.getKey());
        opponentRoads = roads.get(values.getValue());
    }

    private Pair<Integer, Integer> getSelfAndOpponentValues(HashMap<Integer, ArrayList<Road>> roads) {
        int whiteValue = (int) roads.keySet().toArray()[0];
        int blackValue = (int) roads.keySet().toArray()[1];
        if (whiteValue < 0) {
            int t = whiteValue;
            whiteValue = blackValue;
            blackValue = t;
        }
        int selfValue = (board.whoseMove() == PieceColor.WHITE ? whiteValue : blackValue);
        int opponentValue = whiteValue + blackValue - selfValue;

        return new Pair<>(selfValue, opponentValue);
    }


    private Move findFirstMove(Move opponentMove) {
        Move move = null;
        if (opponentMove == null) {
            if (selfColor == PieceColor.EMPTY) selfColor = PieceColor.BLACK; //����
            move = this.firstMove();
        } else {
            if (selfColor == PieceColor.EMPTY) selfColor = PieceColor.WHITE;
        }
        return move;
    }

    private Move findWinMove() {
        for (Road road : this.selfRoads) {
            Point[] winPosition = new Point[2];
            if (road.stonesNum == 4) {
                int idx = 0;
                for (int j = 0; j < 6 && idx < 2; ++j) {
                    if (road.roadColor[j] == PieceColor.EMPTY) {
                        winPosition[idx] = road.roadPosition[j];
                        ++idx;
                    }
                }
                return new G10Move(winPosition[0], winPosition[1]);
            } else if (road.stonesNum == 5) {
                for (int j = 0; j < 6; ++j) {
                    if (road.roadColor[j] == PieceColor.EMPTY) {
                        winPosition[0] = road.roadPosition[j];
                        break;
                    }
                }
                do {
                    winPosition[1] = generateRandomPosition();
                } while (winPosition[1].c == winPosition[0].c && winPosition[1].r == winPosition[0].r);
                return new G10Move(winPosition[0], winPosition[1]);
            }
        }
        return null;
    }

    public Point generateRandomPosition() {
        Random rand = new Random();
        int index;
        do {
            index = rand.nextInt(361);
        } while (this.board.get(index) != PieceColor.EMPTY);

        return new Point(index % 19, index / 19);
    }

    /**
     * �������̵�ÿ�����ÿ�������ҵ����е�·�����������ӵ�������
     * �����ӵ�����ֵ����Ϊ�����������ӵ�����ֵ����Ϊ��ֵ��
     *
     * @return ��������ӵ�����ֵ��������·��ɵ�����
     */
    public HashMap<Integer, ArrayList<Road>> generateRoads() {
        HashMap<Integer, ArrayList<Road>> list = new HashMap<>();
        int[] scores = {14, 66, 153, 790, 844, 100000};
        int blackValue = 0;
        int whiteValue = 0;
        ArrayList<Road> blackRoads = new ArrayList<>();
        ArrayList<Road> whiteRoads = new ArrayList<>();
        for (int col = 0; col < 19; ++col) {
            for (int row = 0; row < 19; ++row) {
                //�����ĸ����� ���� ���� ���Խ����� ���Խ����� , ��ȡ���������·����
                for (int d = 0; d < 4; ++d) {
                    PieceColor[] roadColor = new PieceColor[6];
                    Point[] roadPosition = new Point[6];
                    int stonesNum = 0;
                    if (d == 0 && col <= 13) {
                        for (int k = col; k < col + 6; ++k) {
                            roadColor[k - col] = board.get(k, row);
                            roadPosition[k - col] = new Point(k, row);
                        }
                        stonesNum = countStones(roadColor);
                    }
                    if (d == 1 && row <= 13) {
                        for (int k = row; k < row + 6; ++k) {
                            roadColor[k - row] = board.get(col, k);
                            roadPosition[k - row] = new Point(col, k);
                        }
                        stonesNum = countStones(roadColor);
                    }
                    if (d == 2 && col <= 13 && row <= 13) {
                        for (int p = col, q = row; p <= col + 5; ++p, ++q) {
                            roadColor[p - col] = board.get(p, q);
                            roadPosition[p - col] = new Point(p, q);
                        }
                        stonesNum = countStones(roadColor);
                    }
                    if (d == 3 && col <= 13 && row >= 5) {
                        for (int p = col, q = row; q >= row - 5; ++p, --q) {
                            roadColor[row - q] = board.get(p, q);
                            roadPosition[row - q] = new Point(p, q);
                        }
                        stonesNum = countStones(roadColor);
                    }
                    if (stonesNum < 0) {
                        blackRoads.add(new Road(PieceColor.BLACK, -stonesNum, roadColor, roadPosition));
                        blackValue += scores[-stonesNum - 1];
                    } else if (stonesNum > 0) {
                        whiteRoads.add(new Road(PieceColor.WHITE, stonesNum, roadColor, roadPosition));
                        whiteValue += scores[stonesNum - 1];
                    }
                }
            }
        }
        list.put(-blackValue, blackRoads);
        list.put(whiteValue, whiteRoads);

//        System.out.println(list.size());
        return list;
    }


    private int countStones(PieceColor[] roadColor) {
        int whiteNum = 0;
        int blackNum = 0;
        for (int i = 0; i < 6; i++) {
            if (roadColor[i] == PieceColor.WHITE) ++whiteNum;
            else if (roadColor[i] == PieceColor.BLACK) --blackNum;
        }
        if ((0 != blackNum * whiteNum) || (0 == (-blackNum + whiteNum))) return 0;
        return blackNum == 0 ? whiteNum : blackNum;
    }

    /**
     * @return ��������
     */
    @Override
    public String name() {
        return "g10";
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new G10Board();
    }

    public static void main(String[] args) throws Exception {
        AI ai = new AI();
        ai.findMove(new Move(1, 2));
    }
}
