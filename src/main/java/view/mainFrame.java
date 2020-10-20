package view;

import analyze.analyicPacket;
import entity.*;
import handle.PacketHandle;
import receive.Receiver;
import thread.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;

public class mainFrame extends JFrame {
    private int mycount = 0;

    private Vector<String> columNames = new Vector<>();
    private JPanel mainPanel = new JPanel();
    private JPanel contentPanel=new JPanel();
    private DefaultTableModel tableModel;
    private JTable packetsTable;
    private JScrollPane tablePanel = new JScrollPane();

    private JTextArea analyicData = new JTextArea();
    private JScrollPane analyzePanel = new JScrollPane();

    private JTextArea dataHex = new JTextArea("");
    private JScrollPane dataHexPanel = new JScrollPane();


    private JButton startBtn = new JButton("开始");
    private JButton stopBtn = new JButton("停止");
    private JButton saveBtn = new JButton("保存");
    private JButton exitBtn = new JButton("退出");
    private JPanel btnPanel = new JPanel();

    private JLabel recive = new JLabel("捕获： 0");
    private JLabel drop = new JLabel("丢弃： 0");
    private JLabel handle = new JLabel("已处理： 0");
    private JLabel show = new JLabel("已显示： 0");

    private JPanel statePanel = new JPanel();

    public mainFrame() {
        super("我的捕获器");
        //初始化界面
        init();

    }

    private void init() {
        //初始化表格
        columNames.add("序列号");
        columNames.add("源地址");
        columNames.add("目的地址");
        columNames.add("协议");
        columNames.add("长度");
        columNames.add("描述");
        tableModel = new DefaultTableModel(null, columNames);

        packetsTable = new JTable(tableModel);
        packetsTable.setRowHeight(20);

        packetsTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        packetsTable.getColumnModel().getColumn(5).setPreferredWidth(350);
        packetsTable.setMaximumSize(new Dimension(1000, 400));
        mainFrame that = this;

        packetsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (mycount % 2 == 0) {
                    int selectCoulm = packetsTable.getSelectedRow();
                    new UpdateAnalyic(that, selectCoulm).start();
                    new UpdateHex(that, selectCoulm).start();
                }
                mycount++;
            }
        });
        tablePanel.setViewportView(packetsTable);
        tablePanel.setMaximumSize(new Dimension(1000, 400));


        //初始化数据包分析面板
        analyicData.setText("");
        analyicData.setEditable(false);
        analyicData.setMaximumSize(new Dimension(1000, 150));
        analyzePanel.setViewportView(analyicData);
        analyzePanel.setMaximumSize(new Dimension(1000, 150));

        //初始化十六进制面板
        dataHex.setEditable(false);
        dataHex.setMaximumSize(new Dimension(1000, 150));
        dataHex.setMinimumSize(new Dimension(1000, 150));
        dataHexPanel.setViewportView(dataHex);


        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeviceParam.Stop();
                SavePackets();
                clearTableData();
                Receiver.getInstance().setCountEmpty();
                packetState.getInstance().clear();

                JpacpCapturyDialog dialog = new JpacpCapturyDialog(new JFrame());
                dialog.setBounds(100, 40, 600, 450);
                dialog.setVisible(true);
                if (!dialog.isVisible()) {
                    DeviceParam.Start();
                    new StartCap().start();
                    new UpdateState(that).start();
                    new UpdateTable(that).start();
                    startBtn.setEnabled(false);
                    stopBtn.setEnabled(true);
                    saveBtn.setEnabled(false);
                    exitBtn.setEnabled(false);
                }
            }
        });

        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeviceParam.Stop();
                startBtn.setEnabled(true);
                saveBtn.setEnabled(true);
                exitBtn.setEnabled(true);
            }
        });
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SavePackets();
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                that.setVisible(false);
                DeviceParam.Stop();
                System.exit(0);
            }
        });
        //初始化按钮面板
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        saveBtn.setEnabled(false);
        exitBtn.setEnabled(false);
        btnPanel.add(startBtn);
        btnPanel.add(stopBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(exitBtn);
        btnPanel.setLayout(new FlowLayout());

        //初始化状态面板
        statePanel.add(recive);
        statePanel.add(drop);
        statePanel.add(handle);
        statePanel.add(show);
        statePanel.setLayout(new FlowLayout());


        tablePanel.setBounds(0,0,995,300);
        analyzePanel.setBounds(0,300,995,200);
        dataHexPanel.setBounds(0,500,995,100);
        contentPanel.add(tablePanel);
        contentPanel.add(analyzePanel);
        contentPanel.add(dataHexPanel);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0,0,995,600);
        btnPanel.setBounds(0,600,995,80);
        statePanel.setBounds(0,650,995,50);
        mainPanel.add(contentPanel);
        mainPanel.add(btnPanel);
        mainPanel.add(statePanel);
        mainPanel.setLayout(null);
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                DeviceParam.Stop();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        this.setFont(new Font("微软雅黑",Font.PLAIN,12));
        this.setBounds(50, 50, 1010, 750);
        this.setResizable(false);
    }

    public void setState(int recvNum, int dropNum, int handleNum, int showNum) {
        recive.setText("捕获: " + recvNum);
        drop.setText("丢弃: " + dropNum);
        handle.setText("已处理: " + handleNum);
        show.setText("已显示: " + showNum);
    }

    public void addTableItem(Vector<String> tableData) {
        tableModel.addRow(tableData);
        packetsTable.validate();
    }

    public void clearTableData() {
        tableModel.setDataVector(null,columNames);
        packetsTable.setRowHeight(20);

        packetsTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        packetsTable.getColumnModel().getColumn(5).setPreferredWidth(350);
        packetsTable.setMaximumSize(new Dimension(1000, 400));
        packetsTable.validate();
        analyicData.setText("");
        dataHex.setText("");
    }

    public void UpdateAnalyicContent(String data) {
        analyicData.setText(data);
    }

    public void setHexData(String data) {
        dataHex.setText(data);
    }
    public void resetButton(){
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        saveBtn.setEnabled(true);
        exitBtn.setEnabled(true);
    }

    public void SavePackets() {

        Date date = new Date();

        String filename = ".\\savePacket\\Packets" + System.currentTimeMillis() + ".txt";

        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fw = null;
        BufferedWriter wos = null;
        try {
            fw = new FileWriter(file, true);
            wos = new BufferedWriter(fw);
            for (BasePacket packet : Receiver.getInstance().getPackets()) {
                InfoNode node = analyicPacket.analyze(packet);
                wos.write(node.toString());
                wos.write("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (wos != null) {
                    wos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("保存成功");
    }

}
