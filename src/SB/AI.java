package SB;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;


public class AI extends core.player.AI {
    private static final long[] value=new long[9];
    private static final String[][] whiteChessType=new String[][]{
            {"0111100","0011110"}//????
            ,{"0111110","10111100","00111101"}//????
            ,{"2111110","2111101","2111011","2110111","2101111",
            "2011111","0111112","1011112","1101112","1110112",
            "1111012","1111102"}//????
            ,{"2011110","2101110","2110110","2111010","2111100",
            "2011101","2101101","2110101","2111001","2111100",
            "2011011","2101011","2110011","2111001","2111010",
            "2010111","2100111","2110011","2110101","2110110",
            "20011112","2100111","2101011","2101101","2101110",
            "2001111","2010111","2011011","2011101","2011110",
            "0111102","0111012","0110112","0101112","0011112",
            "1011102","1011012","1010112","1001112","0011112",
            "1101102","1101012","1100112","1001112","0101112",
            "1110102","1110012","1100112","1010112","0110112",
            "21111002","1110012","1101012","1011012","0111012",
            "1111002","1110102","1101102","1011102","0111102"}//????
            ,{"0011100","0001110","0111000","0101100","0011010",
            "0010110","0110100"}//????
            ,{"2011100","0011102","2001110","0111002","2101010",
            "0101012"}//??????
            ,{"0001112","2111000","2101100","2100110","2110100",
            "2110010"}//????
            ,{"011000","001100","000110","010100","001010","010010"}//???
            ,{"2110000","0000112","2101000","0001012","2100100",
            "0010012","010001","100010","2011000","0001102",
            "2100010","0100012","2010100","0010102","2010010",
            "0100102","2010001","1000102"}//???
    };
    private static final String[][] blackChessType=new String[][]{
            {"0222200","0022220"}//????
            ,{"0222220","20222200","00222202"}//????
            ,{"1222220","1222202","1222022","1220222","1202222", "1022222"
            ,"0222221","2022221","2202221","2220221","2222021","2222201"}//????
            ,{"1022220","1202220","1220220","1222020","1222200"
            ,"1022202","1202202","1220202","1222002","1222200"
            ,"1022022","1202022","1220022","1222002","1222020"
            ,"1020222","1200222","1220022","1220202","1220220"
            ,"10022221","1200222","1202022","1202202","1202220"
            ,"1002222","1020222","1022022","1022202","1022220",
            "0222201","0222021","0220221","0202221","0022221",
            "2022201","2022021","2020221","2002221","0022221",
            "2202201","2202021","2200221","2002221","0202221",
            "2220201","2220021","2200221","2020221","0220221",
            "12222001","2220021","2202021","2022021","0222021",
            "2222001","2220201","2202201","2022201","0222201"}//????
            ,{"0022200","0002220","0222000","0202200","0022020","0020220","0220200"}//????
            ,{"1022200","0022201","1002220","0222001","1202020","0202021"}//??????
            ,{"0002221","1222000","1202200","1200220","1220200","1220020"}//????
            ,{"022000","002200","000220","020200","002020","020020",}//???
            ,{"1220000","0000221","1202000","0002021","1200200"
            , "0020021","020002","200020","1022000","0002201","1200020",
            "0200021","1020200","0020201","1020020","0200201","1020002","2000201"}//???
    };
    static {
        long HUOSI=5000000000L;
        value[0]=HUOSI;
        long HUOWU=1000000000L;
        value[1]=HUOWU;
        long MIANWU=200000000L;
        value[2]=MIANWU;
        long MIANSI=80000000L;
        value[3]=MIANSI;
        long HUOSAN=10000000L;
        value[4]=HUOSAN;
        long MENGLONGSAN=1000000L;
        value[5]=MENGLONGSAN;
        long MIANSAN=80000L;
        value[6]=MIANSAN;
        long HUOER=10000L;
        value[7]=HUOER;
        long MIANER=2000L;
        value[8]=MIANER;
    }

    private int steps = 0;
    //Bronia
    @Override
    public Move findMove(Move opponentMove) {
        if (opponentMove == null) {
            Move move = firstMove();
            this.board.makeMove(move);
            return move;
        }

        this.board.makeMove(opponentMove);

        PieceColor color = this.getColor();
        long maxValue = Integer.MIN_VALUE;
        long secValue = Integer.MIN_VALUE;
        int maxPosition = -1;
        int secPosition = -1;


        for (int r = 0; r < 19; ++r) {
            for (int c = 0; c < 19; ++c) {

                int tempPostion = intIndex(c, r);
                if (this.board.get(tempPostion) != PieceColor.EMPTY) continue;
                // calculate all the point, get the val
                // use the 2 MAX point
                long val = getPointEvalue(r, c, color);
                if (val > maxValue) {
                    secValue = maxValue;
                    maxValue = val;
                    //save the pos
                    secPosition = maxPosition;
                    maxPosition = tempPostion;

                } else if (val > secValue) {
                    secValue = val;
                    secPosition = tempPostion;
                }
            }

        }
        // to check TODO
//        if(maxPosition == -1 || secPosition == -1){
//            System.out.println("unexpected position=-1 error //TODO");
//            return null;
//        }
//        System.out.println(maxPosition+"???"+secPosition);
        if (maxPosition != secPosition) {
            Move move = new Move(maxPosition, secPosition);
            this.board.makeMove(move);
            steps++;
//            System.out.println("???");
            return move;
        }
        else {
            System.out.println("unexpected repeat error //TODO");
            return null;
        }

    }

    private int intIndex(int c, int r) {
        int k = c * 1 + r * 19;
        assert 0 <= k && k < 361 ;
        return k;
    }

    @Override
    public String name() {
        return "Bronia";
    }

    Board board = new Board();

    public Board setBoard(Board board) {
        return null;
    }

    public Board getBoard() {
        return null;
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
        steps = 0;
    }

    private long getPointEvalue(int x,int y,PieceColor mainChessColor){
        return getLineEvalue(x,y,mainChessColor)+
                getCowEvalue(x,y,mainChessColor)+
                getLeftSlantEvalue(x,y,mainChessColor)+
                getRightSlantEvalue(x,y,mainChessColor);
//        return getLineEvalue(x,y,mainChessColor)+
//                getCowEvalue(x,y,mainChessColor);

        // TODO
    }

    private long getLineEvalue(int x,int y,PieceColor mainChessColor)
    {
        StringBuilder s=new StringBuilder();
        int index;

        int start=0;
        int cnt=0;
        while(start<19){
            index = intIndex(start,x);
            if(this.board.get(index)!=PieceColor.EMPTY)
                break;
            ++start;
            ++cnt;
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }

        int end=18;
        cnt=0;
        //fixed
        while(end>=0){
            index = intIndex(end,x);
            if(this.board.get(index)!=PieceColor.EMPTY)
                break;
            --end;
            ++cnt;
        }
        for(int j=start;j<=end;j++){
            index = intIndex(j,x);
            if(this.board.get(index)==PieceColor.WHITE)
                s.append('1');
            else if(this.board.get(index)==PieceColor.BLACK)
                s.append('2');
            else
                s.append('0');
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        return singleValue(s.toString(),mainChessColor);
    }

    private long getCowEvalue(int x,int y,PieceColor mainChessColor){
        StringBuilder s=new StringBuilder();
        int index;

        int start=0;
        int cnt=0;
        while(start<19){
            index = intIndex(y,start);
            if(this.board.get(index)!=PieceColor.EMPTY)
                break;
            ++start;
            ++cnt;
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        int end=18;
        cnt=0;
        while(end>=0){
            index = intIndex(y,end);
            if(this.board.get(index)!=PieceColor.EMPTY)
                break;
            --end;
            ++cnt;
        }

        for(int i=start;i<=end;i++){
            index = intIndex(y,i);
            if(this.board.get(index)==PieceColor.WHITE)
                s.append('1');
            else if(this.board.get(index)==PieceColor.BLACK)
                s.append('2');
            else
                s.append('0');
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        return singleValue(s.toString(),mainChessColor);
    }

    private long getLeftSlantEvalue(int x,int y,PieceColor mainChessColor){
        StringBuilder s=new StringBuilder();
        int index;
        int start_x,start_y;//?????????
        if(x+y<19){
            start_x=x+y;
            start_y=0;
        }else{
            start_x=18;
            start_y=y+x-18;
        }
        int cnt=0;
        while (start_x>=0&&start_y<19){
            index = intIndex(start_y,start_x);
            if(this.board.get(index)!=PieceColor.EMPTY)
                break;
            --start_x;
            ++start_y;
            ++cnt;
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        int end_x,end_y;
        if(x+y<19){
            end_x=0;
            end_y=x+y;
        }else{
            end_x=x+y-18;
            end_y=18;
        }
        cnt=0;
        while(end_x<19&&end_y>=0){
            index = intIndex(end_y,end_x);
            if(this.board.get(index)!=PieceColor.EMPTY)
                break;
            ++end_x;
            --end_y;
            ++cnt;
        }
        for(int i=start_x,j=start_y;i>=end_x&&j<=end_y;--i,++j){
            index = intIndex(j,i);
            if(this.board.get(index)==PieceColor.WHITE)
                s.append('1');
            else if(this.board.get(index)==PieceColor.BLACK)
                s.append('2');
            else
                s.append('0');
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        return singleValue(s.toString(),mainChessColor);
    }
    private long getRightSlantEvalue(int x,int y,PieceColor mainChessColor){
        StringBuilder s=new StringBuilder();
        int index;

        int start_x,start_y;//?????????
        int cnt=0;
        if(x<=y){
            start_x=0;
            start_y=y-x;
        }else {
            start_x=x-y;
            start_y=0;
        }
        while (start_x<19&&start_y<19){
            index = intIndex(start_y,start_x);
            if(this.board.get(index)!=PieceColor.EMPTY)
                break;
            ++start_x;
            ++start_y;
            ++cnt;
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        int end_x,end_y;
        if(x<=y){
            end_x=x+18-y;
            end_y=18;
        }else {
            end_x=18;
            end_y=18-x+y;
        }
        cnt=0;
        while(end_x>=0&&end_y>=0){
            index = intIndex(end_y,end_x);
            if(this.board.get(index)!=PieceColor.EMPTY)
                break;
            --end_x;
            --end_y;
            ++cnt;
        }
        for(int i=start_x,j=start_y;i<=end_x&&j<=end_y;i++,++j){
            index = intIndex(j,i);
            if(this.board.get(index)==PieceColor.WHITE)
                s.append('1');
            else if(this.board.get(index)==PieceColor.BLACK)
                s.append('2');
            else
                s.append('0');
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        return singleValue(s.toString(),mainChessColor);
    }

    private long singleValue(String str,PieceColor mainChessColor){
        PieceColor otherChessColor= ((mainChessColor==PieceColor.WHITE)?PieceColor.BLACK:PieceColor.WHITE);
        return singleValue(str,0,mainChessColor)-(long)(singleValue(str,0,otherChessColor));
    }
    private long singleValue(String str, int valuestart, PieceColor mainChessColor){
        if(str.length()<6)
            return 0;
        int[] n= calcValue(str, valuestart,mainChessColor);
        long ans=0;
        if(n!=null)
            ans+=singleValue(str.substring(0,n[0]), valuestart +1,mainChessColor)+
                    singleValue(str.substring(n[1]),0,mainChessColor)+value[n[2]];
        return ans;
    }

    private int[] calcValue(String str, int valuestart, PieceColor mainChessColor){//????? 1:index 2??index+length 3??firstvalue
        int temp;
        if(mainChessColor==PieceColor.WHITE){
            for(int i = valuestart; i<whiteChessType.length; i++){
                for(String ss:whiteChessType[i]){
                    temp=str.indexOf(ss);
                    if(temp!=-1)
                        return new int[]{temp,temp+ss.length(),i};
                }
            }
        }else{
            for(int i = valuestart; i<blackChessType.length; i++){
                for(String ss:blackChessType[i]){
                    temp=str.indexOf(ss);
                    if(temp!=-1)
                        return new int[]{temp,temp+ss.length(),i};
                }
            }
        }
        return null;
    }
}