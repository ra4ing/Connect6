package g08;


import core.board.PieceColor;
import core.game.Game;
import core.game.Move;
import g09.G09Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.max;

public class AI extends core.player.AI {

    String s="ABCDEFGHIJKLMNOPQRS";//索引棋盘
    public static Random rd=new Random();
    @Override
    public String name() {
        return "g08";
    }
    Point getRandomPosition()//在区域[0-18]内获取随机位置
    {
        int begin=0,end=18;
        Point p=new Point(0,0);
        do{
            p.x=rd.nextInt(end-begin+1)+begin;
            p.y=rd.nextInt(end-begin+1)+begin;
        }while(board.get(p.x,p.y)!= PieceColor.EMPTY);

        return p;
    }
    public PieceColor player=PieceColor.EMPTY;
    @Override
    public Move findMove(Move opponentMove) throws Exception {
        if (opponentMove == null) {
            if(player==PieceColor.EMPTY)player=PieceColor.BLACK;//黑先
            Move move = this.firstMove();
            this.board.makeMove(move);
            return move;
        }
        else{
            if(player==PieceColor.EMPTY)player=PieceColor.WHITE;
        }
        board.makeMove(opponentMove);

        HashMap<Integer, ArrayList<Road>> scan=scaner();//索取估值和所有路
        int smallval=(int)scan.keySet().toArray()[0];//负值value black
        int bigval=(int)scan.keySet().toArray()[1];//正值value white
        if(smallval>bigval){
            int t=smallval;
            smallval=bigval;
            bigval=t;
        }

        int myval=(board.whoseMove()==PieceColor.WHITE?bigval:smallval);//我方的估值
        int enemyval=bigval+smallval-myval;//对手的估值
        ArrayList<Road> me=scan.get(myval);//我方的所有路
        ArrayList<Road> enemy=scan.get(enemyval);//对手的所有路
        //System.out.printf("myroads=%d enemy's=%d\n",me.size(),enemy.size());

//        1 首先找一下我们有没有直接获胜的路
        Move move=findwin_if_win(me);
        if(move!=null){
            board.makeMove(move);
            return move;
        }


        //2 如果没找到我们直接获胜的路，则去查找对手可以直接获胜的路，并进行威胁处理
        /** TODO 对每个威胁，我们都进行一次威胁处理，并将威胁处理的方法加到一个处理集中
         *  获取处理集中的重复部分，得到Move
         *
         *  可能的情况
         *      0 没有威胁，直接进入下一步
         *      1 威胁数量等于1，直接进行运算，看看落几个子、在哪里落子能解决威胁
         *      2 威胁数量大于等于2，但他们存在交集，或者说某个棋眼能同时解决他们
         *      3 威胁数量大于2且不能解决威胁
         * */
        // TODO 我们需要一个落子集来存放落子 Point[2]
        int three=100;
        int four=1000;
        int five=100000;
        int[] scores = new int[361];
        for(int i=0;i<361;++i){
            if(board.get(i%19,i/19)!=PieceColor.EMPTY)
                scores[i]=-1;
        }
        int threatenLevel = -1;
        //0 两个棋子去博弈树进行搜索（此时没有威胁）
        //1 一个棋子用于提防对手的三连（此时没有威胁）
        //2 两个棋子都用于避开威胁（此时有威胁）
        for(int i=0;i<enemy.size();++i)
        {
            if(enemy.get(i).stonesNum==3)
            {
                int[] postions=new int[3];
                int p=0;
                for(int j=0;j<6;++j)
                {
                    if(enemy.get(i).roadPc[j]==PieceColor.EMPTY)
                    {
                        postions[p]=enemy.get(i).roadPt[j].y*19+enemy.get(i).roadPt[j].x;
                        ++p;
                    }
                }
                Arrays.sort(postions);
//                if((postions[0]+1==postions[1]&&postions[1]+1==postions[2])
//                        ||(postions[0]+19==postions[1]&&postions[1]+19==postions[2])
//                        ||(postions[0]+20==postions[1]&&postions[1]+20==postions[2])
//                        ||(postions[0]+18==postions[1]&&postions[1]+18==postions[2])){
                //scores[postions[0]]+=three;
                //scores[postions[1]]+=three;
                scores[postions[2]]+=three;
                if(threatenLevel==-1) threatenLevel=0;
                //}
            }
            else if(enemy.get(i).stonesNum==4)
            {
                int mid=(enemy.get(i).roadPt[2].y*19+enemy.get(i).roadPt[2].x
                        +enemy.get(i).roadPt[1].y*19+enemy.get(i).roadPt[1].x)/2;
                int[] postions=new int[2];
                int p=0;
                for(int j=0;j<6;++j)
                {
                    if(enemy.get(i).roadPc[j]==PieceColor.EMPTY)
                    {
                        postions[p++]=enemy.get(i).roadPt[j].y*19+enemy.get(i).roadPt[j].x;
                    }
                }
                if((postions[0]/19-postions[1]/19)*(postions[0]/19-postions[1]/19)
                        +(postions[0]%19-postions[1]%19)*(postions[0]%19-postions[1]%19)<50){
                    if(threatenLevel==0) threatenLevel=1;
                    else if(threatenLevel==1) threatenLevel=2;
                    if((postions[0]/19-mid/19)*(postions[0]/19-mid/19) +(postions[0]%19-mid%19)*(postions[0]%19-mid%19)
                            <(postions[1]/19-mid/19)*(postions[1]/19-mid/19)
                            +(postions[1]%19-mid%19)*(postions[1]%19-mid%19)){
                        scores[postions[0]]+=four;
                    }else {
                        scores[postions[1]]+=four;
                    }
                }else{
                    scores[postions[0]]+=four;
                    scores[postions[1]]+=four;
                    threatenLevel=2;
                }
            }
            else if(enemy.get(i).stonesNum==5)
            {
                // TODO
                if(threatenLevel==0) threatenLevel=1;
                else if(threatenLevel==1) threatenLevel=2;
                int postions=0;
                int p=0;
                for(int j=0;j<6;++j)
                {
                    if(enemy.get(i).roadPc[j]==PieceColor.EMPTY)
                    {
                        postions=enemy.get(i).roadPt[j].y*19+enemy.get(i).roadPt[j].x;
                        break;
                    }
                }
                scores[postions]+=five;
            }
        }
        int[] positions = new int[2];
        positions[0] = positions[1] = 0;
        int max=0;
        for(int j=0;j<2;++j) {
            max=-1;
            for(int i=0;i<361;++i){
                if(scores[i]>max){
                    max=scores[i];
                    positions[j]=i;
                }
            }
            scores[positions[j]]=-1;
        }
        //System.out.println("level="+threatenLevel);
        if(threatenLevel==2)
        {

            Move move1 = new Move(s.charAt(positions[0]%19),s.charAt(positions[0]/19),s.charAt(positions[1]%19),s.charAt(positions[1]/19));

            board.makeMove(move1);
            return move1;
        }else if(threatenLevel==1||threatenLevel==0){

            onePoint=new Point(positions[0]%19,positions[0]/19);
        }

        //3 如果找不到威胁，则进行博弈树搜索，查找最优落子，α-β剪枝计算后续Move
        //3.1 下一层所有状态的获取，有两种情况：
        //      3.3.1 落子集中没有棋子-->随机获取两个落子；
        //      3.1.2 落子集中有一个棋子-->指定一个落子，随机一个落子；
        //TODO α-β剪枝中是否需要计算分支节点（非叶节点）的评估值？
        bestMove = null;
        //System.out.println("alpha-beta");
        alphaBeta(-Integer.MAX_VALUE, Integer.MAX_VALUE, maxDepth);
        board.makeMove(bestMove);
        return bestMove;

//        move = randomMove();
//        this.board.makeMove(move);
//        return move;
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

    public Point onePoint=null;
    public int maxDepth=2;//最大搜索深度

    private Move findwin_if_win(ArrayList<Road> me){
        for(int i=0;i<me.size();++i)
        {
            if(me.get(i).stonesNum==4)
            {

                Point[] pt=new Point[2];
                int pti=0;
                for(int j=0;j<6&&pti<2;++j)
                {
                    if(me.get(i).roadPc[j]==PieceColor.EMPTY)
                    {
                        pt[pti]=me.get(i).roadPt[j];
                        ++pti;
                    }
                }
                Move move=new Move(s.charAt(pt[0].x),s.charAt(pt[0].y),s.charAt(pt[1].x),s.charAt(pt[1].y));

                return move;
            }
            else if(me.get(i).stonesNum==5)
            {
                Point[] pt=new Point[2];
                for(int j=0;j<6;++j)
                {
                    if(me.get(i).roadPc[j]==PieceColor.EMPTY)
                    {
                        pt[0]=me.get(i).roadPt[j];
                        break;
                    }
                }
                do{
                    pt[1]=getRandomPosition();
                }
                while(pt[1].x==pt[0].x&&pt[1].y==pt[0].y);
                Move move=new Move(s.charAt(pt[0].x),s.charAt(pt[0].y),s.charAt(pt[1].x),s.charAt(pt[1].y));
                return move;
            }
        }
        return null;
    }

    public int evaluation()//估值函数
    {
        HashMap<Integer, ArrayList<Road>> scan=scaner();//索取估值和所有路
        int smallval=(int)scan.keySet().toArray()[0];//负值value black
        int bigval=(int)scan.keySet().toArray()[1];//正值value white
        if(smallval>bigval){
            int t=smallval;
            smallval=bigval;
            bigval=t;
        }
        smallval=-smallval;
        //if i'm black ,evaluation = black - white
        //if i'm white ,evaluation = white - black
        if(board.whoseMove()==PieceColor.WHITE)
        {
            int t=smallval;
            smallval=bigval;
            bigval=t;
        }
        //System.out.println("evaluation="+(smallval-bigval));
        int score=smallval-bigval;
        //return player==board.whoseMove()?score:-score;
        return score;
    }
    ArrayList<Point> findPoints(int num)
    {
        //TODO
        if(board.gameOver())
        {
            Point p=getRandomPosition();
            ArrayList<Point> arr=new ArrayList<>();
            arr.add(p);
            return arr;
        }
        HashMap<Integer, ArrayList<Road>> scan=scaner();//索取估值和所有路
        int smallval=(int)scan.keySet().toArray()[0];//负值value black
        int bigval=(int)scan.keySet().toArray()[1];//正值value white
        if(smallval>bigval){
            int t=smallval;
            smallval=bigval;
            bigval=t;
        }

        int myval=(board.whoseMove()==PieceColor.WHITE?bigval:smallval);//我方的估值
        ArrayList<Road> me=scan.get(myval);//我方的所有路

        //给出评分棋盘
        int[] add=new int[]{1,10,100,1000,10000};
        int[] scores = new int[361];
        for(int i=0;i<361;++i){
            scores[i]=0;
        }
        for(int i=0;i<me.size();++i)
        {
            for(int j=0;j<6;++j)
            {
                if(me.get(i).roadPc[j]==PieceColor.EMPTY)
                {
                    scores[me.get(i).roadPt[j].y*19+me.get(i).roadPt[j].x]+=add[me.get(i).stonesNum-1];
                }else{
                    scores[me.get(i).roadPt[j].y*19+me.get(i).roadPt[j].x]=-1;
                }
            }
        }
        //取得分最大的num个点
        ArrayList<Point> points=new ArrayList<>();
        int[] positions = new int[num];
        int max=0;
        for(int j=0;j<num;++j) {
            positions[j]=0;
            max=-1;
            for(int i=0;i<361;++i){
                if(scores[i]>max){
                    max=scores[i];
                    positions[j]=i;
                }
            }
            scores[positions[j]]=-1;
            points.add(new Point(positions[j]%19,positions[j]/19));
        }

        return points;
    }

    ArrayList<Move> getMoves(int depth)
    {
        ArrayList<Move> moves=new ArrayList<>();
        if(depth==maxDepth&&onePoint!=null)
        {
            board.makePoint(onePoint,board.whoseMove());
            ArrayList<Point> points=findPoints(20);
            board.unMakePoint(onePoint);
            for(Point p:points)
            {
                //if(board.get(p.x,p.y)==PieceColor.EMPTY)
                //{
                moves.add(new Move(s.charAt(onePoint.x),s.charAt(onePoint.y),s.charAt(p.x),s.charAt(p.y)));
                //}
            }
            onePoint=null;
        }
        else{
            ArrayList<Point> points=findPoints(7);
            for(Point p:points)
            {
                board.makePoint(p,board.whoseMove());
                ArrayList<Point> points2=findPoints(7);
                board.unMakePoint(p);
                for(Point p2:points2)
                {
                    moves.add(new Move(s.charAt(p.x),s.charAt(p.y),s.charAt(p2.x),s.charAt(p2.y)));
                }
            }
        }
        return moves;
    }

    // 记录最佳走步
    private Move bestMove = null;

    public int alphaBeta(int alpha, int beta, int depth){
        //if(depth!=0)
        //System.out.printf("a=%d b=%d d=%d nd=%d\n",alpha,beta,depth,expandedNode);
        int value, best = -Integer.MAX_VALUE;
        //如果棋局结束或当前节点为叶子节点则返回评估值
        if (board.gameOver() || depth == 0) {
            return evaluation();
        }
        // 如果在置换表当中，且深度小于表中深度
        // if (transTable.containsKey(board.hash) && transTable.get(board.hash).getDepth() >= depth) {
        //   return transTable.get(board.hash).getEvaluation();
        // }
        ArrayList<Move> moves = getMoves(depth);
        for (Move move : moves) {
            board.makeMove(move);
            // 负极大值方法
            value  = -alphaBeta(-beta, -alpha,depth - 1);
//            board.unMakeMove(move);
            board.undo();
            if (value > best) {
                best = value;
                alpha = max(beta, best);
                if (value >= beta) {
                    break;
                }
            }
            // 如果是第一层则记录最优Move
            if (depth == maxDepth && value >= best){
                bestMove = move;
            }
        }
        // transTable.put(board.hash, new Node(depth, best));
        return best;
    }


    Board board=new Board();//记录棋盘

    //评估函数
    class Road//路类
    {
        Road(PieceColor[] pc,Point[] pt,int num,PieceColor c)
        {
            roadPc=new PieceColor[6];
            roadPt=new Point[6];
            stonesNum=num;
            color=c;
            for(int i=0;i<6;++i)
            {
                roadPc[i]=pc[i];
                roadPt[i]=pt[i];
            }
        }

        public PieceColor color;
        public int stonesNum;
        public PieceColor[] roadPc;
        public Point[] roadPt;
    }
    public int roadStones(PieceColor[] t)//获取路中棋子的数量
    {
        int w=0,b=0,e=0;//white black empty
        for(int i=0;i<6;++i)
        {
            if(t[i]==PieceColor.WHITE)++w;
            else if(t[i]==PieceColor.BLACK)++b;
            else ++e;
        }
        if(b*w!=0||b+w==0)return -1;//表示bw均不为零或均为零，则不是路
        else return (b+w)*10+(w==0?1:2);//返回路中棋子的数量
    }
    //扫描棋盘，找出所有路（双方的），并分别计算黑/白路的估值valueb/valuew
    public HashMap<Integer, ArrayList<Road>> scaner()
    {
        HashMap<Integer, ArrayList<Road>> list=new HashMap<>();
        int[] scores={14, 66, 153, 790, 844,100000};
        int valueb=0;//value black
        int valuew=0;//value white
        ArrayList<Road> roadsb=new ArrayList<>();//roads black
        ArrayList<Road> roadsw=new ArrayList<>();//roads white
        for(int i=0;i<19;++i)
        {
            for(int j=0;j<19;++j)
            {
                //遍历四个方向 行向 列向 主对角线向 副对角线向 , 获取各个方向的路序列
                for(int d=0;d<4;++d)
                {
                    PieceColor[] roadPc=new PieceColor[6];
                    Point[] roadPt=new Point[6];
                    int stonesNum=-1;
                    if(d==0&&i<=13)//行向
                    {
                        for(int k=i;k<i+6;++k)
                        {
                            roadPc[k-i]=board.get(k,j);
                            roadPt[k-i]=new Point(k,j);
                        }
                        stonesNum=roadStones(roadPc);
                    }
                    if(d==1&&j<=13)//列向
                    {
                        for(int k=j;k<j+6;++k)
                        {
                            roadPc[k-j]=board.get(i,k);
                            roadPt[k-j]=new Point(i,k);
                        }
                        stonesNum=roadStones(roadPc);
                    }
                    if(d==2&&i<=13&&j<=13)//主对角线向
                    {
                        for(int p=i,q=j;p<=i+5;++p,++q)
                        {
                            roadPc[p-i]=board.get(p,q);
                            roadPt[p-i]=new Point(p,q);
                        }
                        stonesNum=roadStones(roadPc);
                    }
                    if(d==3&&i<=13&&j>=5)//副对角线向
                    {
                        for(int p=i,q=j;q>=j-5;++p,--q)
                        {
                            roadPc[j-q]=board.get(p,q);
                            roadPt[j-q]=new Point(p,q);
                        }
                        stonesNum=roadStones(roadPc);
                    }
                    if(stonesNum!=-1)
                    {
                        PieceColor c=stonesNum%10==1?PieceColor.BLACK:PieceColor.WHITE;
                        if(c==PieceColor.BLACK)
                        {
                            roadsb.add(new Road(roadPc,roadPt,stonesNum/10,c));
                            valueb+=scores[stonesNum/10-1];
                        }
                        else
                        {
                            roadsw.add(new Road(roadPc,roadPt,stonesNum/10,c));
                            valuew+=scores[stonesNum/10-1];
                        }
                    }
                }
            }
        }
        list.put(-valueb,roadsb);
        list.put(valuew,roadsw);
        return list;
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
    }

    //α-β剪枝选择后续着点


}

