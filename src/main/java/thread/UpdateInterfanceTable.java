package thread;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import javax.swing.*;
import java.io.IOException;


public class UpdateInterfanceTable extends Thread{
    private int id;
    private NetworkInterface device;
    private JTable table;
    private boolean stop=false;
    public UpdateInterfanceTable(int id, NetworkInterface device,JTable table){
        this.id=id;
        this.device=device;
        this.table=table;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        try {
            System.out.println("设备"+device.name+"捕获状态更新线程启动");
            JpcapCaptor captor=  JpcapCaptor.openDevice(device,1514,true,1000);

            while (!stop){
                while (captor.getPacket() instanceof Packet){
                    captor.updateStat();
                    table.setValueAt(captor.received_packets,id,1);
                    table.setValueAt(captor.dropped_packets,id,2);
                }
            }
            System.out.println("设备"+device.name+"捕获状态更新线程关闭");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
