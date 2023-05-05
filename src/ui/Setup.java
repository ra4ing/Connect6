package ui;

import core.game.Game;
import core.game.timer.GameTimer;
import core.player.Player;
import entity.GameInfo;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
//import jagoclient.Global;
//import jagoclient.board.GoFrame;
//import jagoclient.board.LocalGoFrame;
//import baseline.player.*;

/**
 * @author ����
 * 1.������ʾ��¼�����ֵ���������
 * 2.ѡ�񱾵ػ�Զ�����ֽ�����
 *   ��1��ѡ�񱾵����֣���Ҫ��������һ���ֵ���������
 *   ��2��ѡ���������֣������ӷ�����
 */
public class Setup extends JFrame{
		// �ҷ�����
		private JTextField weClass;
		//�Է�����
		private JTextField otherClass;
		
		private JRadioButton localButton;
	    private JRadioButton serverButton;
	    
	    private JRadioButton first ;
	    private JRadioButton later ;
	    private boolean isFirst;
	    
	 // �б��
		private JComboBox<String> chessTypeC;
		private String hostIp = "127.0.0.1";
//		this.hostIp = "114.215.139.240"; //�Ʒ�����ip
		private int port = 6666;
		private ArrayList<GameInfo> games;
		User user =new User();
		public Setup() {
			// ���õ�¼���ڱ���
			this.setTitle("��������");
			// ȥ�����ڵ�װ��(�߿�)
			// this.setUndecorated(true);
			// ����ָ���Ĵ���װ�η��
			this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
			// ���������ʼ��
			init();
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// ���ò���Ϊ���Զ�λ
			this.setLayout(null);
			this.setSize(355, 340);
			int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
			int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
			this.setLocation((screen_width - this.getWidth()) / 2, (screen_height - this.getHeight()) / 2);
			// �����С���ܸı�
			this.setResizable(false);
			// ������ʾ
			this.setLocationRelativeTo(null);
			// ������ʾ
			this.setVisible(true);
			//����ȡ����
			this.setFocusable(false);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

		/**
		 * ���������ʼ��
		 */
		public void init() {
			Container container = this.getContentPane();

			// С����
			JLabel chessType;
			JLabel we;
			JLabel other;
			JLabel title;
			JLabel example;

			chessType = new JLabel();
			chessType.setBounds(55, 10, 80, 30);
			chessType.setText("��     �ࣺ");
			
			chessTypeC = new JComboBox<String>();
			chessTypeC.addItem("������");
			chessTypeC.addItem("������");
			chessTypeC.setBounds(120, 15, 70,20);
			
			we = new JLabel();
			we.setBounds(55, 50, 80, 30);
			we.setText("�ҷ����֣�");
			
			// �ҷ���������� 
			weClass = new JTextField();
			weClass.setBounds(120,50, 150, 30);
			
			first = new JRadioButton("����");
			first.setBounds(120, 90, 70, 30);
			later = new JRadioButton("����");
			later.setBounds(200, 90, 70, 30);
			//ѡ�����ֺ���
			ButtonGroup group1 = new ButtonGroup();
			group1.add(first);
			group1.add(later);
			first.setSelected(true);
			
			other = new JLabel();
			other.setBounds(55, 140, 80, 30);
			other.setText("�Է����֣�");
			
			localButton = new JRadioButton("Local");
			localButton.setBounds(120, 140, 70, 30);
			serverButton = new JRadioButton("Server");
			serverButton.setBounds(200, 140, 70, 30);
			
			ButtonGroup group2 = new ButtonGroup();
	        group2.add(localButton);
	        group2.add(serverButton);
	        localButton.setSelected(true);
	        
	        localButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					otherClass.enable();
					otherClass.setBackground(new Color(255,255,255));
				}
			});
	        serverButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					otherClass.disable();
					otherClass.setBackground(new Color(205,201,201));

				}
			});
			// �Է��������������   ���ļ��ж�ȡ
			otherClass = new JTextField();
			otherClass.setBounds(120, 180, 150, 30);
			
			example = new JLabel("(�����ʽ�磺baseline.player.AI)");
			example.setBounds(55, 215, 300, 30);
			example.setForeground(new Color(178,48,96));

			// С��ť
			JButton confirm = new JButton("ȷ��");
			// �����������ɫ������ָ��
			confirm.setFont(new Font("����", Font.PLAIN, 12));
			confirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			confirm.setBounds(80, 270, 60, 25);
			// ����ť���
			confirm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					Player we = getPlayer(weClass);
					Player other = null;
					if (localButton.isSelected()) {
						other = getPlayer(otherClass);
					}
					else {

//						new Login(isFirst,weClass.getText().trim());
						//��ȡip�����ļ�
						Socket socket = connectServer();
						if (socket != null) {
							try {
								BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
								games = new ArrayList<GameInfo>();
								String[] info = null;
								while (true) { //���մӷ���˴���������б�
									info = reader.readLine().split("@");
									if (info[0].equals("GameInfo")) {//GameInfo@gameId@blackName@whiteName@winerName@step@date@reason
										GameInfo gameInfo = new GameInfo();
										gameInfo.setGameId(Integer.parseInt(info[1]));
										gameInfo.setBlackName(info[2]);
										gameInfo.setWhiteName(info[3]);
										gameInfo.setWinerName(info[4]);
										gameInfo.setStep(Integer.parseInt(info[5]));
										gameInfo.setDate(info[6]);
										gameInfo.setReason(info[7]);
										games.add(gameInfo);
									} else {
										break;
									}
								}
								System.out.println(info[0] + "==========");
								if (info[0].equals("success")) {
									user.setPlayerName(weClass.getText().trim());
									user.setName(info[1]);
									String[] allplayer = reader.readLine().split("@");
									JOptionPane.showMessageDialog(null, "��¼�ɹ�", "��ϲ", JOptionPane.DEFAULT_OPTION);
									new HomePage(user, socket, allplayer, games, isFirst);
									getFrame().dispose();
								} else {
									JOptionPane.showMessageDialog(null, "�û��������벻ƥ��", "����", JOptionPane.ERROR_MESSAGE);
								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					startGame(we, other);
					getFrame().dispose();
				}

				private void startGame(Player we, Player other) {
					int timeLimit = 30000;

					//�ҷ��ļ�ʱ��
					GameTimer weTimer = new GameTimer(timeLimit);
					we.setTimer(weTimer);

					//�з��ļ�ʱ��
					GameTimer otherTimer = new GameTimer(timeLimit);
					other.setTimer(otherTimer);

					Game game = null;
					if (first.isSelected()) {
						game = new Game(we, other);
					} else {
						game = new Game(other, we);
					}
					game.start();
					getFrame().setPlayerInfo();
				}

				private Player getPlayer(JTextField playerClassName) {
					if(playerClassName.getText().equals("")){
						example.setText("��������������baseline.player.AI");
						playerClassName.setBackground(new Color(250,128,114));
					}
					else{
						playerClassName.setBackground(new Color(255,255,255));
						try {
							return (Player)Class.forName(playerClassName.getText().trim()).newInstance();
						} catch (InstantiationException ex) {
							ex.printStackTrace();
						} catch (IllegalAccessException ex) {
							ex.printStackTrace();
						} catch (ClassNotFoundException ex) {
							ex.printStackTrace();
						}
					}
					return null;
				}
			});

			JButton cancel = new JButton("ȡ��");
			// �����������ɫ������ָ��
			cancel.setFont(new Font("����", Font.PLAIN, 12));
			cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			cancel.setBounds(200, 270, 60, 25);
			// ����ť���
			cancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getFrame().dispose();
				}
			});
			
			// �������������װ��
			container.add(chessType);
			container.add(chessTypeC);
			container.add(we);
			container.add(weClass);
			container.add(first);
			container.add(later);
			container.add(other);
			container.add(otherClass);
			container.add(confirm);
			container.add(cancel);
			container.add(localButton);
			container.add(serverButton);
			container.add(example);
			
			getPlayerInfo();
		}
		
		public Setup getFrame(){
			return this;
		}

		public static void main(String[] args) {
			new Setup();
		}
		
		//���ӷ�����
		private Socket connectServer() {
			getUserInfo();
			getIPInfo();
			// ���ӷ�����
			try {
				System.out.println("hostIp="+hostIp+"  port"+port);
				System.out.println(user.getUsername() +"@"+user.getPassword());
				
				
				Socket socket = new Socket(hostIp, port);// ���ݶ˿ںźͷ�����ip��������
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				
				// ���Ϳͻ����û�������Ϣ(�û�����ip��ַ)
				writer.println(user.getUsername() +"@"+user.getPassword()+ "@" + socket.getLocalAddress().toString());
				writer.flush();
				// ����������Ϣ���߳�
				return socket;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "��˿ں�Ϊ��" + port + "    IP��ַΪ��" + hostIp + "   �ķ���������ʧ��!" + "\r\n", "����", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return null;
			}
		}
		

		/**
		 * ���ļ��ж�ȡ����
		 */
		private void getPlayerInfo() {
			// TODO Auto-generated method stub
			BufferedReader reader = null ;
			//���������������ļ��ж�ȡ
			try {
				reader = new BufferedReader(new FileReader(new File("player.txt")));
				//reader = new BufferedReader(new FileReader(new File("../AIContest/player.txt")));
				String order=reader.readLine(); //���ֺ���
				if(order!=null){
					if(order.trim().equals("later")){
						later.setSelected(true);
					}
					String myPlayer = reader.readLine();
					if(myPlayer!=null){
						weClass.setText(myPlayer.trim());
						String otherPlayer = reader.readLine();
						if(otherPlayer!=null){
							otherClass.setText(otherPlayer.trim());
						}
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally {
				try {
					reader.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		/**
		 * ��ס���������
		 */
		private void setPlayerInfo() {
			// TODO Auto-generated method stub
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(new File("player.txt")));
				//writer=new BufferedWriter(new FileWriter(new File("../AIContest/player.txt")));
				if(first.isSelected()){
					this.isFirst=true;
					writer.write("first\t\n");
				}else{
					this.isFirst=false;
					writer.write("later\t\n");
				}
				writer.write(weClass.getText() + "\t\n" + otherClass.getText());
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		/**
		 * ��ȡip�����ļ�
		 */
		private void getIPInfo() {
			// TODO Auto-generated method stub
			BufferedReader reader = null ;
			//���������������ļ��ж�ȡ
			try {
				reader = new BufferedReader(new FileReader(new File("IPsettings.txt")));
				//reader = new BufferedReader(new FileReader(new File("../AIContest/IPsettings.txt")));
				this.hostIp = reader.readLine().replace("ip:", "").trim(); //���ֺ���
				this.port = Integer.parseInt(reader.readLine().replace("port:", "").trim());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally {
				try {
					reader.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		/**
		 * ��ȡ�û���������
		 */
		private void getUserInfo() {
			// TODO Auto-generated method stub
			BufferedReader reader = null ;
			//���������������ļ��ж�ȡ
			try {
				reader = new BufferedReader(new FileReader(new File("userInfo.txt")));
				//reader = new BufferedReader(new FileReader(new File("../AIContest/userInfo.txt")));
				user.setUsername(reader.readLine().replace("username:", "").trim());
				user.setPassword(reader.readLine().replace("password:", "").trim());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally {
				try {
					reader.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
}
