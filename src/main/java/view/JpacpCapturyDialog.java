package view;

import entity.DeviceParam;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import thread.UpdateInterfanceTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.*;


public class JpacpCapturyDialog extends JDialog{
    private NetworkInterface[] devices;
    private UpdateInterfanceTable[] updates;
    private DeviceParam para = DeviceParam.getIntance();

    private JTableHeader tableHeader;
    private JTable table;
    private JPanel devicesPanel = new JPanel();

    private JCheckBox promiscBox = new JCheckBox("混杂模式");
    private JCheckBox isTomsBox = new JCheckBox("超时模式");
    private JCheckBox noBlockBox = new JCheckBox("No-Blocking模式");
    private JCheckBox isCapCountBox = new JCheckBox("指定捕获的数据包的数目");
    private JPanel modelPanel = new JPanel();

    private JLabel tomsTip = new JLabel("超时的值：", JLabel.RIGHT);
    private JTextField tomsValue = new JTextField("50");
    private JPanel tomsPanel = new JPanel();
    private JRadioButton wholeBtn = new JRadioButton("整个数据报");
    private JRadioButton headBtn = new JRadioButton("仅头部");
    private JRadioButton userBtn = new JRadioButton("自定义");
    private ButtonGroup caplenGroup = new ButtonGroup();
    private JLabel caplenTip = new JLabel("提取的字节数:");
    private JTextField caplenValue = new JTextField("1514");
    private JPanel caplenPanel = new JPanel();
    private JLabel capcountTip=new JLabel("捕获的数目: ");
    private JTextField capcountValue=new JTextField("1000");
    private JPanel capcountPanel=new JPanel();
    private JPanel paraPanel = new JPanel();
    private JPanel setPanel = new JPanel();

    private JCheckBox optimizeBox = new JCheckBox("优化模式");
    private JLabel optimizeTip = new JLabel("过滤器字符串: ", JLabel.RIGHT);
    private JTextField optimizeValue = new JTextField("");
    private JPanel fiterStrPanel = new JPanel();
    private JPanel fiterPanel = new JPanel();

    private JButton okBtn = new JButton("确认");
    private JButton noBtn = new JButton("取消");
    private JPanel btnPanel = new JPanel();

    private JPanel mainPanel = new JPanel();

    public JpacpCapturyDialog(JFrame frame) {
        super(frame, "设置网卡及其参数", true);
        setTablePanel();
        setModelPanel();
        setParaPanel();
        setFiterPanel();
        setButtonPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        this.setContentPane(mainPanel);
        this.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                for (int i = 0; i < updates.length; i++) {
                    updates[i].setStop(true);
                }
                if(table.getSelectedRow()<0){
                    para.setId(0);
                    para.setDevice(devices[0]);
                }else {
                    para.setId(table.getSelectedRow());
                    para.setDevice(devices[table.getSelectedRow()]);
                }

                para.setPromisc(promiscBox.isSelected());
                para.setToms(isTomsBox.isSelected());
                para.setNoblock(noBlockBox.isSelected());
                para.setCapFlag(isCapCountBox.isSelected());
                para.setCaplen(Integer.parseInt(caplenValue.getText().trim()));
                para.setTomsNum(Integer.parseInt(tomsValue.getText().trim()));
                para.setCapcount(Integer.parseInt(capcountValue.getText().trim()));
                para.setOptimize(optimizeBox.isSelected());
                para.setFiterStr(optimizeValue.getText().trim());
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
        this.setResizable(false);
        frame.setLayout(new FlowLayout());
    }


    //设置设备面板
    private void setTablePanel(){
        //获取网络设备，开启线程监听捕获包数和丢包数
        this.devices = JpcapCaptor.getDeviceList();
        //初始化表格
        String[] columNames = {"设备", "捕获包数", "丢包数"};
        Object[][] rowData = new Object[devices.length][3];
        for (int i = 0; i < devices.length; i++) {
            rowData[i][0] = devices[i].description;
            rowData[i][1] = "0";
            rowData[i][2] = "0";
        }
        table = new JTable(rowData, columNames);
        // 设置表格内容颜色
        table.setForeground(Color.BLACK);                   // 字体颜色
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));      // 字体样式
        table.setSelectionForeground(Color.blue);      // 选中后字体颜色
        table.setSelectionBackground(Color.LIGHT_GRAY);     // 选中后字体背景
        table.setGridColor(Color.GRAY);                     // 网格颜色

        // 设置表头
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));  // 设置表头名称字体样式
        table.getTableHeader().setForeground(Color.BLACK);                // 设置表头名称字体颜色
        table.getTableHeader().setResizingAllowed(false);               // 设置不允许手动改变列宽
        table.getTableHeader().setReorderingAllowed(false);             // 设置不允许拖动重新排序各列
        table.getTableHeader().setVisible(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // 设置行高
        table.setRowHeight(20);

        // 第一列列宽设置为300
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
        devicesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "网卡"));
        devicesPanel.add(table.getTableHeader());
        devicesPanel.add(table);
        //devicesPanel.setSize(800,250);
        //开启线程更新设备的捕获包数和丢包数
        openUpdate(devices);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                para.setDevice(devices[table.getSelectedRow()]);
            }
        });

        mainPanel.add(devicesPanel);
    }
    //设置模式面板
    private void setModelPanel(){
        promiscBox.setSelected(true);
        modelPanel.add(promiscBox);
        modelPanel.add(isTomsBox);
        modelPanel.add(noBlockBox);
        modelPanel.add(isCapCountBox);
        isCapCountBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isCapCountBox.isSelected()){
                    capcountValue.setEnabled(true);
                }else {
                    capcountValue.setEnabled(false);
                }
            }
        });
        modelPanel.setLayout(new BoxLayout(modelPanel, BoxLayout.Y_AXIS));
        modelPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "模式"));
        setPanel.add(modelPanel);
    }
    //设置参数面板
    private void setParaPanel(){
        tomsValue.setMaximumSize(new Dimension(300, 25));
        tomsPanel.add(tomsTip);
        tomsPanel.add(tomsValue);
        tomsPanel.setSize(300, 30);
        tomsPanel.setLayout(new BoxLayout(tomsPanel, BoxLayout.X_AXIS));

        wholeBtn.setSelected(true);
        wholeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(wholeBtn.isSelected()){
                    caplenValue.setEnabled(false);
                    caplenValue.setText("1514");
                }
            }
        });
        headBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(headBtn.isSelected()){
                    caplenValue.setEnabled(false);
                    caplenValue.setText("68");
                }
            }
        });
        userBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(userBtn.isSelected()){
                    caplenValue.setEnabled(true);
                    caplenValue.setText("68");
                }
            }
        });
        caplenGroup.add(wholeBtn);
        caplenGroup.add(headBtn);
        caplenGroup.add(userBtn);

        caplenValue.setMaximumSize(new Dimension(300, 25));
        caplenValue.setEnabled(false);
        caplenPanel.add(caplenTip);
        caplenPanel.add(caplenValue);
        caplenPanel.setSize(300, 30);
        caplenPanel.setLayout(new BoxLayout(caplenPanel, BoxLayout.X_AXIS));

        capcountValue.setMaximumSize(new Dimension(300, 25));
        capcountValue.setEnabled(false);
        capcountPanel.add(capcountTip);
        capcountPanel.add(capcountValue);
        capcountPanel.setLayout(new BoxLayout(capcountPanel, BoxLayout.X_AXIS));

        paraPanel.add(wholeBtn);
        paraPanel.add(headBtn);
        paraPanel.add(userBtn);
        paraPanel.add(caplenPanel);
        paraPanel.add(tomsPanel);
        paraPanel.add(capcountPanel);
        paraPanel.setLayout(new BoxLayout(paraPanel, BoxLayout.Y_AXIS));
        paraPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "参数"));
        setPanel.add(paraPanel);
    }
    //设置过滤器面板
    private void setFiterPanel(){
        optimizeValue.setMaximumSize(new Dimension(300, 25));
        optimizeBox.setSelected(true);
        fiterStrPanel.add(optimizeTip);
        fiterStrPanel.add(optimizeValue);
        fiterStrPanel.setLayout(new BoxLayout(fiterStrPanel, BoxLayout.X_AXIS));
        fiterPanel.add(optimizeBox);
        fiterPanel.add(fiterStrPanel);
        fiterPanel.setLayout(new BoxLayout(fiterPanel, BoxLayout.Y_AXIS));
        fiterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "过滤器"));
        setPanel.add(fiterPanel);
        setPanel.setLayout(new BoxLayout(setPanel, BoxLayout.X_AXIS));
        mainPanel.add(setPanel);

    }
    //设置按钮面板
    private void setButtonPanel(){
        JDialog that=this;
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < updates.length; i++) {
                    updates[i].setStop(true);
                }
                if(table.getSelectedRow()<0){
                    para.setId(0);
                    para.setDevice(devices[0]);
                }else {
                    para.setId(table.getSelectedRow());
                    para.setDevice(devices[table.getSelectedRow()]);
                }

                para.setPromisc(promiscBox.isSelected());
                para.setToms(isTomsBox.isSelected());
                para.setNoblock(noBlockBox.isSelected());
                para.setCapFlag(isCapCountBox.isSelected());
                para.setCaplen(Integer.parseInt(caplenValue.getText().trim()));
                para.setTomsNum(Integer.parseInt(tomsValue.getText().trim()));
                para.setCapcount(Integer.parseInt(capcountValue.getText().trim()));
                para.setOptimize(optimizeBox.isSelected());
                para.setFiterStr(optimizeValue.getText().trim());
                that.setVisible(false);
            }
        });
        noBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < updates.length; i++) {
                    updates[i].setStop(true);
                }
                para.setId(0);
                para.setDevice(devices[0]);
                para.setPromisc(true);
                para.setToms(false);
                para.setNoblock(false);
                para.setCapFlag(false);
                para.setCaplen(1514);
                para.setTomsNum(50);
                para.setCapcount(1000);
                para.setOptimize(true);
                para.setFiterStr("");
                that.setVisible(false);
            }
        });
        okBtn.setBounds(100, 10, 70, 30);
        noBtn.setBounds(400, 10, 70, 30);
        btnPanel.add(okBtn);
        btnPanel.add(noBtn);
        btnPanel.setLayout(null);
        mainPanel.add(btnPanel);

    }
    private UpdateInterfanceTable[] openUpdate(NetworkInterface[] devices) {
        updates = new UpdateInterfanceTable[devices.length];
        for (int i = 0; i < devices.length; i++) {
            updates[i] = new UpdateInterfanceTable(i, devices[i], table);
            updates[i].start();
        }
        return updates;
    }

    public void stop() {
        for (UpdateInterfanceTable update : updates) {
            update.setStop(true);
        }
    }

}
