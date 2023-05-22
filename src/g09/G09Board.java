package g09;

import core.board.PieceColor;
import core.game.Move;


/**
 * G09Board�࣬�������չ��core.board.Board�࣬Ϊһ������������ṩ��һЩ�ض��Ĳ�����
 * G09���̵���񱻶���Ϊ19*19������
 */
public class G09Board extends core.board.Board {
    //ȫ�ֱ������������̵�����������ͨ���ַ���������λ��
    String s="ABCDEFGHIJKLMNOPQRS";//��������


    /**
     * �������������ȡ������ɫ
     *
     * @param c �е�λ��
     * @param r �е�λ��
     * @return ���ӵ���ɫ
     */
    public PieceColor get(int c, int r) {
        return super.get(s.charAt(c), s.charAt(r));
    }

    /**
     * ������ʼ��Ŀ��λ�ý���һ�������ƶ�
     *
     * @param c0 ������
     * @param r0 ������
     * @param c1 ������
     * @param r1 ������
     */
    public void makeMove(int c0, int r0, int c1, int r1) {
        super.makeMove(new Move(s.charAt(c0), s.charAt(r0), s.charAt(c1), s.charAt(r1)));
    }

    /**
     * ����ָ��λ�ú�������ɫ��ִ��һ�����ӵķ���
     *
     * @param c ���ӷ���λ�õ�������
     * @param r ���ӷ���λ�õ�������
     * @param color ���ӵ���ɫ
     */
    public void makeOneMove(int c, int r, PieceColor color) {
        // Ϊ���ӵ�λ���ҵ�һ��Ψһ�����������̱���Ϊһά���飬����ͨ���кź��кŵ���ϼ���ó���
        super.set(c+r*19, color);
    }

    /**
     * ����һ�����ӵķ���
     *
     * @param c ���ӷ���λ�õ�������
     * @param r ���ӷ���λ�õ�������
     */
    public void unMakeOneMove(int c, int r) {
        // �����ṩ���кź��к��ҵ�������������λ������Ϊ��
        super.set(c+r*19, PieceColor.EMPTY);
    }

}
