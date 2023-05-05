package ui;

import core.player.AI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;

public class UploadJar extends JFrame {

    private String hostIp = "114.215.139.240";
    private int port = 6668;

    private JTextField className;//AI名字（Jar包名字）
    private JTextField AIName;//AI类名
    private JTextField fileAddress;//选择的文件名称
    private JLabel example;//示例文字
    private String filepath = null;//文件地址
    File[] arrfiles = null;//发送的文件

    public UploadJar()  {
        //JFrame jframe = new JFrame("上传文件");// 实例化一个JFrame
        this.setTitle("上传文件");
        init();
        // 设置布局为绝对定位
        this.setLayout(null);
        this.setSize(360, 340);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setLocation((screen_width - this.getWidth()) / 2, (screen_height - this.getHeight()) / 2);
        // 窗体大小不能改变
        this.setResizable(false);
        // 居中显示
        this.setLocationRelativeTo(null);
        //设置窗体可见
        this.setVisible(true);
        //close的方式,隐藏并释放窗体
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * 窗体组件初始化
     */
    public void init() {
        Container container = this.getContentPane();
        JLabel CName = new JLabel();
        CName.setBounds(40, 40, 130, 30);
        CName.setText("请输入您的AI名字：");
        className = new JTextField("如：Group01");
        className.setBounds(170, 40, 150, 30);

        JLabel AName = new JLabel("如：baseline.player.AI");
        AName.setBounds(40, 100, 130, 30);
        AName.setText("请输入您的AI类名：");
        AIName = new JTextField();
        AIName.setBounds(170, 100, 150, 30);

        fileAddress = new JTextField("请选择您的文件");
        fileAddress.setBounds(40, 165, 180, 30);
        fileAddress.setEnabled(false);
        JButton fileChoose = new JButton("选择");
        fileChoose.setFont(new Font("宋体", Font.PLAIN, 12));
        fileChoose.setBounds(240, 167, 80, 25);


        example = new JLabel();
        example.setBounds(55, 205, 300, 30);
        example.setForeground(new Color(178, 48, 96));

        JButton confirm = new JButton("确定");
        // 设置字体和颜色和手形指针
        confirm.setFont(new Font("宋体", Font.PLAIN, 12));
        confirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirm.setBounds(30, 250, 140, 25);
        JButton cancel = new JButton("取消");
        // 设置字体和颜色和手形指针
        cancel.setFont(new Font("宋体", Font.PLAIN, 12));
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.setBounds(190, 250, 140, 25);

        // 所有组件用容器装载
        container.add(CName);
        container.add(className);
        container.add(AName);
        container.add(AIName);
        container.add(fileAddress);
        container.add(fileChoose);
        container.add(example);
        container.add(confirm);
        container.add(cancel);


        // 给取消按钮添加事件
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getFrame().dispose();
            }
        });

        //给选择文件按钮添加事件
        fileChoose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //设置选择器
                JFileChooser chooser = new JFileChooser();
                //设置多选
                chooser.setMultiSelectionEnabled(true);
                //过滤文件类型
                FileNameExtensionFilter filter = new FileNameExtensionFilter("jar",
                        "jar", "txt");
                chooser.setFileFilter(filter);
                //是否打开文件选择框
                int returnVal = chooser.showOpenDialog(confirm);
                if (returnVal == JFileChooser.APPROVE_OPTION) {   //如果符合文件类型
                    filepath = chooser.getSelectedFile().getAbsolutePath();  //获取绝对路径
                    arrfiles = chooser.getSelectedFiles();    //得到选择的文件
                    if (arrfiles == null || arrfiles.length == 0) {
                        return;
                    }
                    fileAddress.setText(filepath);
                    //System.out.println(arrfiles);
                }
            }
        });

        // 给提交按钮添加点击事件
        confirm.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                try {

                    eventOnImport(new JButton());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("5555");
                }
            }
        });

    }

    private void eventOnImport(JButton confirm) throws IOException {
        if (className.getText().equals("") || AIName.getText().equals("") || fileAddress.getText().equals("")) {
            example.setText("请检查所有内容填写完毕后再提交");
            //className.setBackground(new Color(250,128,114));
        } else {
            getIPInfo();
            //建立socket服务
            System.out.println(hostIp+port);
            Socket fileSocket = new Socket(hostIp, port);// 根据端口号和服务器ip建立连接
            System.out.println(port + "端口已经连接成功");
            //使用Socket中的方法getOutputStream，获取网络字节输出流OutputStream对象
            OutputStream socketOutputStream = fileSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(socketOutputStream);
            //BufferedWriter OutputStream = new BufferedWriter(new OutputStreamWriter(fileSocket.getOutputStream()));
            // 发送用户输入的基本信息(jar包名字、AI类名、上传地址)
            writer.println(className.getText() + "@" + AIName.getText() + "@" + fileAddress.getText());
            writer.flush();

            //接收服务端信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileSocket.getInputStream()));
            String SeverMes = null;
            SeverMes = reader.readLine();// 接收客户端消息
            System.out.println(SeverMes);

            if (SeverMes.equals("HADclassname")) {
                example.setText("已经有该AI名称，请更换后重新提交");
            } else if (arrfiles != null) {
                FileInputStream input = null;
                String path = System.getProperty("user.dir") + "\\lib";
                try {
                    for (File f : arrfiles) {
                        File dir = new File(path);
                        /** 目标文件夹 * */
                        File[] fs = dir.listFiles();
                        HashSet<String> set = new HashSet<String>();
                        if (fs != null) {
                            for (File file : fs) {
                                set.add(file.getName());
                            }
                        }
                        /** 判断是否已有该文件* */
                        if (set.contains(f.getName())) {
                            JOptionPane.showMessageDialog(new JDialog(),
                                    f.getName() + ":该文件已存在！");
                            return;
                        }
                        input = new FileInputStream(f);
                        byte[] buffer = new byte[1024 * 1024];
                        int len = 0;
                        while (-1 != (len = input.read(buffer))) {
                            socketOutputStream.write(buffer, 0, len);
                            socketOutputStream.flush();
                        }
                        fileSocket.shutdownOutput();
                    }
                    SeverMes = reader.readLine();// 接收客户端消息
                    if (SeverMes.equals("UPLOADOK")) {
                        System.out.println(SeverMes);
                        JOptionPane.showMessageDialog(null, "上传成功！", "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                        getFrame().dispose();
                        input.close();
                        fileSocket.close();

                    }
                } catch (FileNotFoundException e1) {
                    JOptionPane.showMessageDialog(null, "上传失败！", "提示",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "上传失败！", "提示",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        }
    }

    public UploadJar getFrame() {
        return this;
    }


    //读取ip设置文件
    private void getIPInfo() {
        // TODO Auto-generated method stub
        BufferedReader reader = null;
        //本地棋手类名从文件中读取
        try {
            reader = new BufferedReader(new FileReader(new File("IPsettings.txt")));
            //reader = new BufferedReader(new FileReader(new File("../AIContest/IPsettings.txt")));
            this.hostIp = reader.readLine().replace("ip:", "").trim(); //先手后手
            //this.port = Integer.parseInt(reader.readLine().replace("port:", "").trim());
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }



}



