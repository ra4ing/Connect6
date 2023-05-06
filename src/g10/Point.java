package g10;

public class Point {
    public int c;
    public int r;

    public Point() {
    }

    public Point(int c, int r) {
        this.c = c;
        this.r = r;
    }

    public Point(Point point) {
        this.c = point.c;
        this.r = point.r;
    }

}
