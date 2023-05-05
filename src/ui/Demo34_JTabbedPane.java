package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import entity.GameInfo;
import ui.Demo34_JTabbedPane;
import ui.RowHeaderTable;
import ui.SwingUtils;

/**
 * 选项卡面板 JTabbedPane
 */
public class Demo34_JTabbedPane extends JPanel {
	private JScrollPane rightScroll;
	private DefaultTableModel model;
	private JTable table;

	public static void main(String[] args) {
		JFrame frame = new JFrame("博弈机器人对战平台");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new Demo34_JTabbedPane(), BorderLayout.CENTER);
		SwingUtils.setCenter(frame);// 设置窗体大小600*800并居中
		frame.setVisible(true);
	}

	public Demo34_JTabbedPane() {
		super(new GridLayout(1, 1));
		JTabbedPane tabbedPane = new JTabbedPane();
		ImageIcon icon = createImageIcon("NEW.png");

		JComponent panel1 = makeTextPanel("个人 ");
		// -----创建标题--------------
		Vector bt = new Vector();
		bt.add("GAMEID");
		bt.add("BLACK");
		bt.add("WHITE");
		bt.add("STEP");
		bt.add("WINER");
		bt.add("DATE");
		// -----创建内容--------------
		Vector datas = new Vector();
//		 for (GameInfo game : games) {
//		 Vector data = new Vector();
//		 data.add(game.getGameId());
//		 data.add(game.getBlackName());
//		 data.add(game.getWhiteName());
//		 data.add(game.getStep());
//		 data.add(game.getWinerName());
//		 data.add(game.getDate());
//		 datas.add(data);
//		 }
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
		
		tabbedPane.addTab("个人对战信息", icon, rightScroll, "Does nothing");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		JComponent panel2 = makeTextPanel("硬件");
		tabbedPane.addTab("棋手排名", icon, panel2, "Does twice as much nothing");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		JComponent panel3 = makeTextPanel("高级");
		tabbedPane.addTab("赛事", icon, panel3, "Still does nothing");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		add(tabbedPane);
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
}
