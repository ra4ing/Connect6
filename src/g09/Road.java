package g09;

import core.board.PieceColor;

/**
 * Road�࣬���������ϵ�һ��·��
 * ��������·�����ӵ���ɫ���������Լ�ÿ�����ӵ���ɫ��λ����Ϣ��
 */
public class Road {
    // ·�ϵ����ӵ���ɫ
    public PieceColor color;
    // ·�ϵ����ӵ�����
    public int stonesNum;
    // ·��ÿ�����ӵ���ɫ
    public PieceColor[] roadColor;
    // ·��ÿ�����ӵ�λ��
    public Point[] roadPosition;

    /**
     * ����һ���µ�Roadʵ����
     *
     * @param c ·�����ӵ���ɫ
     * @param num ·�����ӵ�����
     * @param roadC ·��ÿ�����ӵ���ɫ
     * @param roadP ·��ÿ�����ӵ�λ��
     */
    public Road(PieceColor c, int num, PieceColor[] roadC, Point[] roadP) {
        this.roadColor = new PieceColor[6];
        this.roadPosition = new Point[6];
        this.color = c;
        this.stonesNum = num;
        System.arraycopy(roadC, 0, this.roadColor, 0, 6);
        System.arraycopy(roadP, 0, this.roadPosition, 0, 6);
        this.roadColor = roadC;
        this.roadPosition = roadP;
    }
}
