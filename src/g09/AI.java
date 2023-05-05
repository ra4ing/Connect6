package g09;

import core.board.PieceColor;
import core.game.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class AI extends core.player.AI {


    private PieceColor selfColor = PieceColor.EMPTY;
    Board board = new Board();
    private ArrayList<Road> selfRoads;
    private ArrayList<Road> opponentRoads;

    /**
     * Return a legal move for me according to my opponent's move, and at that
     * moment, I am facing a board after the opponent's move. Abstract method to be
     * implemented by subclasses.
     *
     * @param opponentMove 对方移动决策
     * @return 己方移动决策
     */
    @Override
    public core.game.Move findMove(Move opponentMove) throws Exception {
        Move move = findFirstMove(opponentMove);
        if (move != null) {
            return move;
        }

        // 扫描全局生成黑白两方的路
        setRoads();

        // 寻找胜着
        move = findWinMove(selfRoads);
        if (move != null) {
            this.board.makeMove(move);
        }

        // 威胁处理
        move = findThreatMove();
        if (move != null) {
            this.board.makeMove(move);
        }

        // 博弈树搜索
        move = findBestMove();
        if (move != null) {
            this.board.makeMove(move);
        }
        return null;
    }

    private Move findBestMove() {
        // TODO
        return null;
    }

    private Point onePoint;
    private final int THTREE = 100;
    private final int FOUR = 1000;
    private final int FIVE = 100000;

    private Move findThreatMove() {
        int[] scores = new int[361];
        initializeScores(board, scores);

        int threatenLevel = evaluateThreatenLevel(opponentRoads, scores);

        int[] topPositions = findTopPositions(scores);

        if (threatenLevel == 2) {
            Move move1 = new G09Move(topPositions[0] % 19, topPositions[0] / 19, topPositions[1] % 19, topPositions[1] / 19);
            board.makeMove(move1);
            return move1;
        } else if (threatenLevel == 1 || threatenLevel == 0) {
            onePoint = new Point(topPositions[0] % 19, topPositions[0] / 19);
        }
        return null;
    }

    private void initializeScores(Board board, int[] scores) {
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
        int p = 0;
        for (int j = 0; j < 6; ++j) {
            if (opponentRoad.roadColor[j] == PieceColor.EMPTY) {
                positions[p] = opponentRoad.roadPosition[j].r * 19 + opponentRoad.roadPosition[j].c;
                ++p;
            }
        }
        Arrays.sort(positions);

        scores[positions[2]] += THTREE;
        if (threatenLevel == -1) threatenLevel = 0;
        return threatenLevel;
    }

    private int handleFourStones(Road opponentRoad, int[] scores, int threatenLevel) {
        int mid = (opponentRoad.roadPosition[2].r * 19 + opponentRoad.roadPosition[2].c + opponentRoad.roadPosition[1].r * 19 + opponentRoad.roadPosition[1].c) / 2;
        int[] positions = new int[2];
        int p = 0;
        for (int j = 0; j < 6; ++j) {
            if (opponentRoad.roadColor[j] == PieceColor.EMPTY) {
                positions[p++] = opponentRoad.roadPosition[j].r * 19 + opponentRoad.roadPosition[j].c;
            }
        }
        int tmp1 = positions[0] / 19 - positions[1] / 19;
        int tmp2 = positions[0] % 19 - positions[1] % 19;
        if (tmp1 * tmp1 + tmp2 * tmp2 < 50) {
            if (threatenLevel == 0) threatenLevel = 1;
            else if (threatenLevel == 1) threatenLevel = 2;
            if ((positions[0] / 19 - mid / 19) * (positions[0] / 19 - mid / 19) + (positions[0] % 19 - mid % 19) * (positions[0] % 19 - mid % 19) < (positions[1] / 19 - mid / 19) * (positions[1] / 19 - mid / 19) + (positions[1] % 19 - mid % 19) * (positions[1] % 19 - mid % 19)) {
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

    private int handleFiveStones(Road opponentRoad, int[] scores, int threatenLevel) {
        if (threatenLevel == 0) threatenLevel = 1;
        else if (threatenLevel == 1) threatenLevel = 2;
        int postions = 0;
        int p = 0;
        for (int j = 0; j < 6; ++j) {
            if (opponentRoad.roadColor[j] == PieceColor.EMPTY) {
                postions = opponentRoad.roadPosition[j].r * 19 + opponentRoad.roadPosition[j].c;
                break;
            }
        }
        scores[postions] += FIVE;
        return threatenLevel;
    }


    private int[] findTopPositions(int[] scores) {
        int[] topPositions = new int[2];

        for (int i = 0; i < 2; ++i) {
            int max = -1;
            for (int j = 0; j < scores.length; ++j) {
                if (scores[j] > max) {
                    max = scores[j];
                    topPositions[i] = j;
                }
            }
            scores[topPositions[i]] = -1;
        }

        return topPositions;
    }


    private void setRoads() {
        HashMap<Integer, ArrayList<Road>> roads = generateRoads();
        int tmp1 = (int) roads.keySet().toArray()[0];
        int tmp2 = (int) roads.keySet().toArray()[1];

        int selfValue;
        if (selfColor == PieceColor.WHITE) selfValue = tmp1 > 0 ? tmp1 : tmp2;
        else selfValue = tmp1 > 0 ? tmp2 : tmp1;
        int opponentValue = tmp1 + tmp2 - selfValue;

        selfRoads = roads.get(selfValue);
        opponentRoads = roads.get(opponentValue);
    }

    private Move findFirstMove(Move opponentMove) {
        if (selfColor == PieceColor.EMPTY) { // 黑先
            selfColor = PieceColor.BLACK;
            G09Move move = (G09Move) firstMove();
            this.board.makeMove(move);
            return move;
        } else {
            selfColor = PieceColor.WHITE;
        }
        board.makeMove(opponentMove);
        return null;
    }

    private Move findWinMove(ArrayList<Road> selfRoad) {
        for (Road road : selfRoad) {
            Point[] winPosition = new Point[2];
            if (road.stonesNum == 4) {

                int idx = 0;
                for (int j = 0; j < 6 && idx < 2; ++j) {
                    if (road.roadColor[j] == PieceColor.EMPTY) {
                        winPosition[idx] = road.roadPosition[j];
                        ++idx;
                    }
                }
                return new G09Move(winPosition[0].c, winPosition[0].r, winPosition[1].c, winPosition[1].r);
            } else if (road.stonesNum == 5) {
                for (int j = 0; j < 6; ++j) {
                    if (road.roadColor[j] == PieceColor.EMPTY) {
                        winPosition[0] = road.roadPosition[j];
                        break;
                    }
                }
                do {
                    winPosition[1] = findRandomPosition();
                } while (winPosition[1].c == winPosition[0].c && winPosition[1].r == winPosition[0].r);
                return new G09Move(winPosition[0].c, winPosition[0].r, winPosition[1].c, winPosition[1].r);
            }
        }
        return null;
    }

    public Point findRandomPosition() {
        Random rand = new Random();
        int index1;
        do {
            index1 = rand.nextInt(361);
        } while (this.board.get(index1) == PieceColor.EMPTY);

        return new Point(index1 % 19, index1 / 19);
    }

    /**
     * 遍历棋盘的每个点的每个方向，找到所有的路并计算其棋子的数量。
     * 将白子的评估值储存为正数，将黑子的评估值储存为负值。
     *
     * @return 白子与黑子的评估值及其所有路组成的数组
     */
    public HashMap<Integer, ArrayList<Road>> generateRoads() {
        HashMap<Integer, ArrayList<Road>> list = new HashMap<>();
        int[] scores = {14, 66, 153, 790, 844, 100000};
        int whiteValue = 0;
        int blackValue = 0;
        ArrayList<Road> whiteRoads = new ArrayList<>();
        ArrayList<Road> blackRoads = new ArrayList<>();
        for (int row = 0; row < 19; ++row) {
            for (int col = 0; col < 19; ++col) {
                //遍历四个方向
                for (int d = 0; d < 4; ++d) {
                    PieceColor[] roadColor = new PieceColor[6];
                    Point[] roadPosition = new Point[6];
                    int stonesNum = 0;

                    if (d == 0 && row <= 13) { // 行
                        for (int k = row; k < row + 6; ++k) {
                            roadColor[k - row] = board.get(k, col);
                            roadPosition[k - row] = new Point(k, col);
                        }
                        stonesNum = countStones(roadColor);
                    }

                    if (d == 1 && col <= 13) { // 列
                        for (int k = col; k < col + 6; ++k) {
                            roadColor[k - col] = board.get(row, k);
                            roadPosition[k - col] = new Point(row, k);
                        }
                        stonesNum = countStones(roadColor);
                    }

                    if (d == 2 && row <= 13 && col <= 13) { // 主对角线
                        for (int p = row, q = col; p <= row + 5; ++p, ++q) {
                            roadColor[p - row] = board.get(p, q);
                            roadPosition[p - row] = new Point(p, q);
                        }
                        stonesNum = countStones(roadColor);
                    }

                    if (d == 3 && row <= 13 && col >= 5) { // 副对角线
                        for (int p = row, q = col; q >= col - 5; ++p, --q) {
                            roadColor[col - q] = board.get(p, q);
                            roadPosition[col - q] = new Point(p, q);
                        }
                        stonesNum = countStones(roadColor);
                    }

                    if (stonesNum > 0) {
                        whiteRoads.add(new Road(PieceColor.WHITE, stonesNum, roadColor, roadPosition));
                        whiteValue += scores[stonesNum - 1];
                    } else if (stonesNum < 0) {
                        blackRoads.add(new Road(PieceColor.BLACK, stonesNum, roadColor, roadPosition));
                        blackValue -= scores[-stonesNum - 1];
                    }
                }
            }
        }
        list.put(whiteValue, whiteRoads);
        list.put(blackValue, blackRoads);
        return list;
    }

    private int countStones(PieceColor[] roadColor) {
        int whiteNum = 0;
        int blackNum = 0;
        for (int i = 0; i < 6; i++) {
            if (roadColor[i] == PieceColor.WHITE) whiteNum++;
            else if (roadColor[i] == PieceColor.BLACK) blackNum++;
        }
        if (0 != blackNum * whiteNum || (blackNum | whiteNum) == 0) return 0;
        return blackNum == 0 ? whiteNum : blackNum;
    }

    /**
     * @return 返回名字
     */
    @Override
    public String name() {
        return "g09";
    }


}
