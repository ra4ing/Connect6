package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import core.game.Game;
import core.player.Player;
import entity.delegate.SocketDelegate;
import entity.delegate.SocketPlayer;
import entity.GameInfo;
import entity.User;

public class HomePage {
	private JFrame frame;
	private JList playerList;
	private JMenuBar mb;
	private JMenu more, edit, function,upload;
	private JMenuItem serverBattle, updateGames,uploadjar;// 定义菜单选项
	private JPanel northPanel;
	private JLabel welcome;
	private JScrollPane playersScroll;
	private DefaultListModel listModel;
	private JPanel chessPanel;

	private boolean isConnected = false;
	private Thread messageThread;// 负责接收消息的线程
	private Thread heartThread;// 负责发送心跳包的线程
	
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private Map<String, User> onLineUsers = new ConcurrentHashMap<String, User>();// 所有在线用户
	private String username;// 当前用户
	private SocketDelegate serverPlayer;
	private int gameId;// 初始设置为六子棋 1
	private boolean isFirst;
	private JScrollPane rightScroll;
	private DefaultTableModel model;
	private JTable table;
	private ArrayList<GameInfo> games;
	private Vector datas;
	private Vector bt;//棋局列表标题
	
	public HomePage(User user, Socket socket, String[] allPlayer, ArrayList<GameInfo> games, boolean isFirst) {
		this.username = user.getName();
		this.gameId = 1;
		mb = new JMenuBar();
		more = new JMenu(" 更多对战方式");
		serverBattle = new JMenuItem(" 服务器间棋手对战");
		more.add(serverBattle);

		function = new JMenu("功能");
		updateGames = new JMenuItem("刷新棋局列表");
		function.add(updateGames);

		upload = new JMenu("上传");
		uploadjar = new JMenuItem("上传棋手jar包");
		upload.add(uploadjar);


		mb.add(more);
		mb.add(function);
		mb.add(upload);
		welcome = new JLabel("   欢迎 " + this.username + "同学！");
		welcome.setPreferredSize(new Dimension(200, 40));
		northPanel = new JPanel();
		northPanel.setBounds(0, 0, 600, 30);
		northPanel.setLayout(new GridLayout(1, 7));
		northPanel.add(welcome);

		chessPanel = new JPanel();

		JButton connect6 = new JButton("六子棋");
		connect6.setBackground(new Color(176, 224, 230));
		connect6.setBounds(20, 30, 150, 100);
		JButton Gobang = new JButton("五子棋");
		Gobang.setBackground(new Color(64, 224, 208));
		Gobang.setBounds(20, 150, 150, 100);
		JButton go = new JButton("围棋");
		go.setBackground(new Color(135, 206, 235));
		go.setBounds(20, 270, 150, 100);
		JButton chineseChess = new JButton("中国象棋");
		chineseChess.setBackground(new Color(127, 255, 212));
		chineseChess.setBounds(20, 390, 150, 100);
		chessPanel.setLayout(null);
		chessPanel.add(connect6);
		chessPanel.add(Gobang);
		chessPanel.add(go);
		chessPanel.add(chineseChess);

		listModel = new DefaultListModel();
		playerList = new JList(listModel);
		playersScroll = new JScrollPane(playerList);
		playersScroll.setBorder(new TitledBorder("棋手列表"));

		JPanel jp = new JPanel();
		JTabbedPane tabbedPane = new JTabbedPane();

		// -----创建标题--------------
	    bt = new Vector();
		bt.add("GAMEID");
		bt.add("BLACK");
		bt.add("WHITE");
		bt.add("STEP");
		bt.add("WINER");
		bt.add("DATE");
		// -----创建内容--------------
		this.games = games;
		datas = new Vector();
		for (GameInfo game : games) {
			Vector data = new Vector();
			data.add(game.getGameId());
			data.add(game.getBlackName());
			data.add(game.getWhiteName());
			data.add(game.getStep());
			data.add(game.getWinerName());
			data.add(game.getDate());
			datas.add(data);
		}
		// -----表格模式--------------

		// 把实例加到表格 ，并使单元格不能被编辑
		model = new DefaultTableModel(datas, bt) {
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;// 返回true表示能编辑，false表示不能编辑
			}
		};

		table = new JTable(model);
		// 数据居中对齐
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();// 单元格渲染器
		tcr.setHorizontalAlignment(JLabel.CENTER);// 居中显示
		table.setDefaultRenderer(Object.class, tcr);// 设置渲染器

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 一次只能选择一个列表索引
		TableColumn idC = table.getColumnModel().getColumn(0);
		idC.setPreferredWidth(6);
		TableColumn stepC = table.getColumnModel().getColumn(3);
		stepC.setPreferredWidth(6);

		rightScroll = new JScrollPane(table);
		rightScroll.setRowHeaderView(new RowHeaderTable(table, 40));
		// rightScroll = new JScrollPane(textArea);
		rightScroll.setBorder(new TitledBorder("Games"));

		tabbedPane.addTab("对战信息", rightScroll);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		JComponent panel2 = makeTextPanel("棋手排名");
		tabbedPane.addTab("棋手排名", panel2);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		JComponent panel3 = makeTextPanel("赛事");
		tabbedPane.addTab("赛事", panel3);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		jp.add(tabbedPane);

		frame = new JFrame("博弈开发平台");
		// 更改JFrame的图标：
		// frame.setIconImage(Toolkit.getDefaultToolkit().createImage(Client.class.getResource("qq.jpg")));
		frame.setLayout(null);
		frame.setJMenuBar(mb);
		chessPanel.setBounds(0, 30, 180, 500);
		jp.setBounds(180, 30, 620, 500);
		// jp.setBackground(new Color(150));
		playersScroll.setBounds(810, 30, 150, 500);

		frame.add(northPanel);
		frame.add(chessPanel);
		frame.add(playersScroll);
		frame.add(jp);
		frame.setSize(1000, 600);
		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screen_width - frame.getWidth()) / 2, (screen_height - frame.getHeight()) / 2);
		frame.setVisible(true);

		// 获取与服务端连接
		this.socket = socket;
		try {
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MessageThread mt = new MessageThread(reader);
		messageThread = new Thread(mt);
		messageThread.start();
		
		HeartThread ht = new HeartThread();
		heartThread = new Thread(ht);
		heartThread.start();
		

		isConnected = true;// 已经连接上了
		for (String playerName : allPlayer) {
			listModel.addElement(playerName);
		}


		
		
		// 服务器棋手间对战
		serverBattle.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				new ServerBattle(allPlayer, socket, mt);
			}
		});
		// 更新棋局列表
		updateGames.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				sendMessage("ALLGAMES@");
			}
		});
		//上传jar包
		uploadjar.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				new UploadJar();
				sendMessage("UPLOAD");
			}
		});

		// 棋局列表点击事件
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					int colummCount = table.getModel().getColumnCount();// 列数
					System.out.println(
							"选中的gameId:" + table.getModel().getValueAt(table.getSelectedRow(), 0).toString() + " ");
					String gameId = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
					String whiteName = table.getModel().getValueAt(table.getSelectedRow(), 1).toString();
					String blackName = table.getModel().getValueAt(table.getSelectedRow(), 2).toString();
					writer.println("GAMEINFO@" + gameId);
					writer.flush();

					// 新建棋谱
					// SQLDelegate clientPlayer = new SQLDelegate(whiteName,
					// socket,"w");
					// SQLDelegate serverPlayer = new SQLDelegate(blackName,
					// socket,"b");
					SocketPlayer clientPlayer = new SocketPlayer(whiteName, socket, "black", "GameResult");
					SocketPlayer serverPlayer = new SocketPlayer(blackName, socket, "white", "GameResult");
					mt.addObserver(serverPlayer);
					mt.addObserver(clientPlayer);

					// 开启服务端下棋监听
					int timeLimit = 30000;
					Game game = new Game(clientPlayer, serverPlayer);
					game.start();
				}
			}
		});
		// 用户列表单击事件
		playerList.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getClickCount() == 2) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							// 找到对弈棋手，新建game
							String player = user.getPlayerName();
							try {
								Class clazz = Class.forName(player);
								Player clientPlayer = (Player) clazz.newInstance();

								serverPlayer = new SocketDelegate((String) playerList.getSelectedValue(), socket);
								mt.addObserver(serverPlayer);
								// 向服务端发信号 开始下棋
								writer.println("PLAYCHESS@" + clientPlayer.name() + "@"
										+ (String) playerList.getSelectedValue() + "@" + isFirst);
								writer.flush();
								// 开启服务端下棋监听

								int timeLimit = 30000;
								Game game = null;
								if (isFirst) {
									game = new Game(clientPlayer, serverPlayer);
								} else {
									game = new Game(serverPlayer, clientPlayer);
								}
								game.start();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}).start();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		

		
		
		
		// 关闭窗口时事件
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("windowClosing   " + isConnected);
				if (isConnected) {
					closeConnection();// 关闭连接
				}
				System.exit(0);// 退出程序
			}
		});
	}

	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setFont(new Font("宋体", 1, 50));
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}

	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Demo34_JTabbedPane.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}


	/**
	 * 主动发送心跳包
	 */
	class HeartThread implements Runnable{		
		private Socket socket;	
		@Override
		public void run() {
			try {
				System.out.println("心跳包线程已经启动");
				while(true) {
					Thread.sleep(3000000);
					// 发送心跳包
					writer.println("biubiubiu");
					writer.flush();
					//socket.sendUrgentData(0xFF); 
				}
			}catch(Exception e) {
				e.printStackTrace();
			}			
		}		
	}
	
	/**
	 * 客户端主动关闭连接
	 */
	@SuppressWarnings("deprecation")
	public synchronized boolean closeConnection() {
		try {
			sendMessage("CLOSE");// 发送断开连接命令给服务器
			messageThread.stop();// 停止接受消息线程
			heartThread.stop();//停止心跳包线程
			// 释放资源
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
			isConnected = false;
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			isConnected = true;
			return false;
		}
	}
	/**
	 * 客户端发送信息方法
	 */
	public void sendMessage(String message) {
		System.out.println("sendMessage_" + message);
		writer.println(message);
		writer.flush();
	}

	// 不断接收消息的线程
	class MessageThread extends Observable implements Runnable {
		private BufferedReader reader;

		// 接收消息线程的构造方法
		public MessageThread(BufferedReader reader) {
			this.reader = reader;
		}

		// 被动的关闭连接
		public synchronized void closeCon() throws Exception {
			// 清空用户列表
			listModel.removeAllElements();
			// 被动的关闭连接释放资源
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
			isConnected = false;// 修改状态为断开
		}

		public void run() {
			String message = "";
			while (true) {
				try {
					message = reader.readLine();
					System.out.println(message);
					StringTokenizer stringTokenizer = new StringTokenizer(message, "/@");
					String command = stringTokenizer.nextToken();// 命令
					if (command.equals("CLOSE"))// 服务器已关闭命令
					{
						closeCon();// 被动的关闭连接
						JOptionPane.showMessageDialog(frame, "服务器关闭！", "错误", JOptionPane.ERROR_MESSAGE);
						return;// 结束线程
					} else if (command.equals("ADD")) {// 有用户上线更新在线列表
						String username = "";
						String userIp = "";
						if ((username = stringTokenizer.nextToken()) != null
								&& (userIp = stringTokenizer.nextToken()) != null) {
							User user = new User(username, userIp);
							onLineUsers.put(username, user);
							listModel.addElement(username);
						}
					} else if (command.equals("DELETE")) {// 有用户下线更新在线列表
						String username = stringTokenizer.nextToken();
						User user = (User) onLineUsers.get(username);
						onLineUsers.remove(user);
						listModel.removeElement(username);
					} else if (command.equals("USERLIST")) {// 加载在线用户列表
						int size = Integer.parseInt(stringTokenizer.nextToken());
						String username = null;
						String userIp = null;
						for (int i = 0; i < size; i++) {
							username = stringTokenizer.nextToken();
							userIp = stringTokenizer.nextToken();
							User user = new User(username, userIp);
							onLineUsers.put(username, user);
							listModel.addElement(username);
						}
					} else if (command.equals("MAX")) {// 人数已达上限
						closeCon();// 被动的关闭连接
						JOptionPane.showMessageDialog(frame, "服务器缓冲区已满！", "错误", JOptionPane.ERROR_MESSAGE);
						return;// 结束线程
					} else if (command.equals("SERVERGAME")) {// 服务器间对战
						System.out.println("SERVERGAME=======================");
					} else if (command.equals("GameInfo")) {// 刷新对局列表
						int count = 0;
						int gameID = Integer.parseInt(stringTokenizer.nextToken());
						for (GameInfo game : games) {
							if (game.getGameId() == gameID) {
								count = 1;
								break;
							}
						}
						System.out.println(gameID);
						if (count == 0) {
							GameInfo gameInfo = new GameInfo();
							gameInfo.setGameId(gameID);
							gameInfo.setBlackName(stringTokenizer.nextToken());
							gameInfo.setWhiteName(stringTokenizer.nextToken());
							gameInfo.setWinerName(stringTokenizer.nextToken());
							gameInfo.setStep(Integer.parseInt(stringTokenizer.nextToken()));
							gameInfo.setDate(stringTokenizer.nextToken());
							gameInfo.setReason(stringTokenizer.nextToken());
							games.add(gameInfo);
							datas = new Vector();
							for (GameInfo game : games) {
								Vector data = new Vector();
								data.add(game.getGameId());
								data.add(game.getBlackName());
								data.add(game.getWhiteName());
								data.add(game.getStep());
								data.add(game.getWinerName());
								data.add(game.getDate());
								datas.add(data);
							}
							model = new DefaultTableModel(datas, bt) {
								public boolean isCellEditable(int row, int column) {
									// TODO Auto-generated method stub
									return false;// 返回true表示能编辑，false表示不能编辑
								}
							};
							table.setModel(model);
							System.out.println(datas.size());
							frame.repaint();
						}
						
					} else {
						System.out.println("下棋或聊天");
						System.out.println(message);
						setChanged();
						notifyObservers(message);
					}
					playersScroll.repaint();
				} catch (Exception e) {
					e.printStackTrace();
					//问题：如何结束线程的同时关闭当前窗口
					System.out.println("服务器出现异常，已中断服务");
					JOptionPane.showMessageDialog(frame, "服务器出现异常终止服务！", "错误", JOptionPane.ERROR_MESSAGE);
					if(JOptionPane.ERROR_MESSAGE == 0) {
							System.exit(0);// 退出程序
							closeConnection();// 关闭连接
							}																
				}
			}
		}
	}
}
