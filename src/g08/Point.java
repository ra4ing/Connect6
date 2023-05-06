package g08;


public class Point{//坐标类
    public Point(){}
    public Point(int x, int y){this.x = x;this.y = y;}
    public Point(Point t){
        x =t.x;
        y =t.y;}
    public int x;
    public int y;
}
