package g09;

import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.max;

/**
 * AI类，继承自core.player.AI类，实现了博弈的AI玩家
 */
public class AI extends core.player.AI {
    // 存储自身颜色，默认为空
    private PieceColor selfColor = PieceColor.EMPTY;
    // 指定最大搜索深度为2
    private final int maxDepth = 2;
    // 创建棋盘对象
    G09Board board = new G09Board();
    // 自身的所有有效路线
    private ArrayList<Road> selfRoads;
    // 对手的所有有效路线
    private ArrayList<Road> opponentRoads;
    // 最佳移动步骤
    private Move bestMove = null;
    // 临时存储一步棋的位置
    private Point onePoint = null;

    /**
     * Return a legal move for me according to my opponent's move, and at that
     * moment, I am facing a board after the opponent's move. Abstract method to be
     * implemented by subclasses.
     *
     * @param opponentMove 对方移动决策
     * @return 己方移动决策
     */
    @Override
    public Move findMove(Move opponentMove) {
        Move move = findFirstMove(opponentMove);
        if (move != null) {
            this.board.makeMove(move);
            return move;
        }
        board.makeMove(opponentMove);

        // 扫描全局生成黑白两方的路
        setRoads();

        // 寻找胜着
        move = findWinMove();
        if (move != null) {
            this.board.makeMove(move);
            return move;
        }

        // 威胁处理 （生成onePoint，指代这里只用一个棋子防守）
        move = findThreatMove();
        if (move != null) {
            this.board.makeMove(move);
            return move;
        }

        // 博弈树搜索 （利用onePoint， 分两种情况寻找路径）
        move = findBestMove();
        this.board.makeMove(move);
        return move;
    }

    /**
     * 寻找最佳移动步骤，使用alpha-beta剪枝算法
     *
     * @return 返回最佳移动步骤
     */
    private Move findBestMove() {
        this.bestMove = null;
        alphaBeta(-Integer.MAX_VALUE, Integer.MAX_VALUE, maxDepth);
        return bestMove;
    }

    /**
     * Alpha-Beta剪枝算法，进行深度优先搜索，寻找最佳移动步骤
     *
     * @param alpha Alpha值
     * @param beta Beta值
     * @param depth 当前搜索深度
     * @return 返回最佳移动步骤的评分值
     */
    private int alphaBeta(int alpha, int beta, int depth) {
        // 初始化局部变量
        int walkValue, bestValue = -Integer.MAX_VALUE;
        // 当搜索到最大深度或游戏结束时，返回当前局面评估值
        if (depth == 0 || this.board.gameOver()) {
            return evaluate();
        }

        // 生成当前局面的所有合法走法
        ArrayList<Move> moves = generateMove(depth);
        // 对每一种走法进行搜索
        for (Move move : moves) {
            // 按照当前走法走一步
            board.makeMove(move);
            // 递归进行Alpha-Beta搜索
            walkValue = -alphaBeta(-beta, -alpha, depth - 1);
            // 撤销刚才的走法
            board.undo();
            // 更新bestValue和alpha值
            if (walkValue > bestValue) {
                bestValue = walkValue;
                alpha = max(bestValue, alpha);
                // Beta剪枝
                if (walkValue >= beta) break;
            }

            // 在最大搜索深度下，更新最佳移动步骤
            if (depth == maxDepth && walkValue >= bestValue) bestMove = move;
        }

        return bestValue;
    }

    /**
     * 根据深度生成所有可能的移动方式
     *
     * @param depth 用于决定需要生成的移动方式的深度
     * @return 所有可能的移动方式列表
     */
    private ArrayList<Move> generateMove(int depth) {
        // 初始化局部变量
        ArrayList<Move> moves = new ArrayList<>();
        // 若之前有存储的待选走法，尝试进行这一步
        if (depth == maxDepth && onePoint != null) {
            this.board.makeOneMove(onePoint.c, onePoint.r, board.whoseMove());
            ArrayList<Point> points = generatePoint(20);
            this.board.unMakeOneMove(onePoint.c, onePoint.r);

            for (Point p : points) {
                moves.add(new G09Move(onePoint.c, onePoint.r, p.c, p.r));
            }
            onePoint = null;
        } else {
            ArrayList<Point> points1 = generatePoint(7);
            for (Point p1 : points1) {
                this.board.makeOneMove(p1.c, p1.r, board.whoseMove());
                ArrayList<Point> points2 = generatePoint(7);
                this.board.unMakeOneMove(p1.c, p1.r);
                for (Point p2 : points2) {
                    moves.add(new G09Move(p1.c, p1.r, p2.c, p2.r));
                }
            }

        }
        return moves;
    }

    /**
     * 生成一组可能的棋子放置点
     *
     * @param num 需要生成的点的数量
     * @return 生成的点的列表
     */
    private ArrayList<Point> generatePoint(int num) {
        ArrayList<Point> points = new ArrayList<>();
        if (this.board.gameOver()) {
            Point p = generateRandomPosition();
            points.add(p);
            return points;
        }
        HashMap<Integer, ArrayList<Road>> roads = generateRoads();
        IntPair values = getSelfAndOpponentValues(roads);
        int selfValue = values.getKey();
        ArrayList<Road> selfR = roads.get(selfValue);

        // 评分
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
        // 获取最大值
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

    /**
     * 评估当前棋盘状态的价值，以决定下一步的移动
     *
     * @return 当前棋盘状态的价值
     */
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


    /**
     * 找到一个对对手产生威胁的移动方式
     *
     * @return 一个对对手产生威胁的移动方式，如果没有找到则返回null
     */
    private Move findThreatMove() {
        int[] scores = new int[361];
        initializeScores(board, scores);

        int threatenLevel = evaluateThreatenLevel(opponentRoads, scores);

        int[] topPositions = findTopPositions(scores);

        if (threatenLevel == 2) {
            return new G09Move(topPositions[0] % 19, topPositions[0] / 19, topPositions[1] % 19, topPositions[1] / 19);
        } else if (threatenLevel == 1 || threatenLevel == 0) {
            onePoint = new Point(topPositions[0] % 19, topPositions[0] / 19);
        }
        return null;
    }

    /**
     * 初始化评分数组，根据当前棋盘状态给每个位置评分
     *
     * @param board 当前棋盘状态
     * @param scores 用于存储评分的数组
     */
    private void initializeScores(G09Board board, int[] scores) {
        for (int i = 0; i < 361; ++i) {
            if (board.get(i % 19, i / 19) != PieceColor.EMPTY) scores[i] = -1;
        }
    }

    /**
     * 计算对手道路的威胁等级。
     *
     * @param opponentRoads 对手的道路集合
     * @param scores 道路的分数数组
     * @return 威胁等级
     */
    private int evaluateThreatenLevel(ArrayList<Road> opponentRoads, int[] scores) {
        int threatenLevel = -1;

        // 遍历所有对手的道路
        for (Road road : opponentRoads) {
            int stonesNum = road.stonesNum;

            // 对不同的石头数量进行处理
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

    /**
     * 处理道路上有三个石头的情况
     *
     * @param opponentRoad 对手的道路
     * @param scores 道路的分数数组
     * @param threatenLevel 威胁等级
     * @return 新的威胁等级
     */
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

    /**
     * 处理道路上有四个石头的情况
     *
     * @param opponentRoad 对手的道路
     * @param scores 道路的分数数组
     * @param threatenLevel 威胁等级
     * @return 新的威胁等级
     */
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

    /**
     * 处理道路上有五个石头的情况
     *
     * @param opponentRoad 对手的道路
     * @param scores 道路的分数数组
     * @param threatenLevel 威胁等级
     * @return 新的威胁等级
     */
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


    /**
     * 找到分数最高的两个位置
     *
     * @param scores 道路的分数数组
     * @return 两个最高分数的位置
     */
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


    /**
     * 设置自己和对手的道路
     */
    private void setRoads() {
        HashMap<Integer, ArrayList<Road>> roads = generateRoads();
        IntPair values = getSelfAndOpponentValues(roads);
        selfRoads = roads.get(values.getKey());
        opponentRoads = roads.get(values.getValue());
    }

    /**
     * 获取自己和对手的值
     *
     * @param roads 道路集合
     * @return 一个包含自己和对手值的键值对
     */
    private IntPair getSelfAndOpponentValues(HashMap<Integer, ArrayList<Road>> roads) {
        int whiteValue = (int) roads.keySet().toArray()[0];
        int blackValue = (int) roads.keySet().toArray()[1];
        if (whiteValue < 0) {
            int t = whiteValue;
            whiteValue = blackValue;
            blackValue = t;
        }
        int selfValue = (board.whoseMove() == PieceColor.WHITE ? whiteValue : blackValue);
        int opponentValue = whiteValue + blackValue - selfValue;

        return new IntPair(selfValue, opponentValue);
    }


    /**
     * 寻找第一次移动
     *
     * @param opponentMove 对手的移动
     * @return 本次移动
     */
    private Move findFirstMove(Move opponentMove) {
        Move move = null;
        if (opponentMove == null) {
            if (selfColor == PieceColor.EMPTY) selfColor = PieceColor.BLACK; //黑先
            move = this.firstMove();
        } else {
            if (selfColor == PieceColor.EMPTY) selfColor = PieceColor.WHITE;
        }
        return move;
    }

    /**
     * 寻找能够获胜的移动
     *
     * @return 能够获胜的移动
     */
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
                return new G09Move(winPosition[0], winPosition[1]);
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
                return new G09Move(winPosition[0], winPosition[1]);
            }
        }
        return null;
    }

    /**
     * 随机生成一个可用的位置
     *
     * @return 随机生成的位置
     */
    public Point generateRandomPosition() {
        Random rand = new Random();
        int index;
        do {
            index = rand.nextInt(361);
        } while (this.board.get(index) != PieceColor.EMPTY);

        return new Point(index % 19, index / 19);
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
        int blackValue = 0;
        int whiteValue = 0;
        ArrayList<Road> blackRoads = new ArrayList<>();
        ArrayList<Road> whiteRoads = new ArrayList<>();
        for (int col = 0; col < 19; ++col) {
            for (int row = 0; row < 19; ++row) {
                //遍历四个方向 行向 列向 主对角线向 副对角线向 , 获取各个方向的路序列
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
     * 返回AI的名字
     *
     * @return AI的名字
     */
    @Override
    public String name() {
        return "g09";
    }

    /**
     * 在给定的游戏中玩游戏
     *
     * @param game 需要参与的游戏
     */
    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new G09Board();
    }

    /**
     * 该类用于存储一对整数值，提供了获取键值和数值的方法
     */
    public static class IntPair {
        private final int key;
        private final int value;

        public IntPair(int key, int value) {
            this.key = key;
            this.value = value;
        }

        /**
         * 获取键值
         *
         * @return 键值
         */
        public int getKey() {
            return key;
        }

        /**
         * 获取数值
         *
         * @return 数值
         */
        public int getValue() {
            return value;
        }
    }


}
