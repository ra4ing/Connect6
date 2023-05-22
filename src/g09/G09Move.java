package g09;

import core.game.Move;

/**
 * G09Move�࣬�������չ��core.game.Move�࣬���ڱ�ʾG09�����ϵ�һ���ƶ���
 * G09���̵���񱻶���Ϊ19*19����������ཫ��������ת��Ϊ�ַ���ʾ��
 */
public class G09Move extends Move {

    /**
     * ʹ���ĸ�������Ϊ����������һ���ƶ�
     * ���к��е���������ת��Ϊ��Ӧ���ַ�
     *
     * @param col0 ����
     * @param row0 ������
     * @param col1 ������
     * @param row1 ������
     */
    public G09Move(int col0, int row0, int col1, int row1) {
        super((char) (col0+'A'), (char)(row0+'A'), (char)(col1+'A'), (char)(row1+'A'));
    }

    /**
     * ʹ������Point������Ϊ����������һ���ƶ�
     * ��Point������к�������ת��Ϊ��Ӧ���ַ�
     *
     * @param p1 Point����
     * @param p2 Point����
     */
    public G09Move(Point p1, Point p2) {
        super((char) (p1.c+'A'), (char)(p1.r+'A'), (char)(p2.c+'A'), (char)(p2.r+'A'));
    }


}
