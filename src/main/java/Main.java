import entity.BasePacket;
import entity.DeviceParam;
import jpcap.JpcapCaptor;
import jpcap.packet.EthernetPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.Packet;
import receive.Receiver;
import thread.StartCap;
import thread.UpdateState;
import thread.UpdateTable;
import view.JpacpCapturyDialog;
import view.mainFrame;

import javax.swing.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        File file=new File(".\\savePacket");
        if(!file.exists()){
            System.out.println(file.mkdirs());
        }
        mainFrame frame = new mainFrame();
        //frame.setVisible(true);
        JpacpCapturyDialog dialog = new JpacpCapturyDialog(new JFrame());
        dialog.setBounds(100, 40, 600, 450);
        dialog.setVisible(true);
        if (!dialog.isVisible()) {
            frame.validate();
            frame.setVisible(true);
            new StartCap().start();
            new UpdateState(frame).start();
            new UpdateTable(frame).start();
        }
        if (!frame.isVisible()) {

            try {
                DeviceParam.Stop();
                Thread.sleep(2000);
                frame.SavePackets();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
