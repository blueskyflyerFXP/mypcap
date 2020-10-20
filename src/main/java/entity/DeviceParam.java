package entity;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.io.IOException;


/**
 * 保存网卡捕获数据包的参数
 */
public class DeviceParam {
    //网卡编号
    private int id = 0;
    //网卡
    private NetworkInterface device = null;
    //混杂模式
    private boolean promisc = true;
    //超时模式
    private boolean toms = false;
    //NoBlocking模式
    private boolean noblock = false;
    //是否指定捕获的数目
    private boolean capFlag = false;
    //提取的字节数
    private int caplen = 1514;
    //超时的参数
    private int tomsNum = 1000;
    //指定捕获的数目
    private int capcount = 1000;
    //过滤器
    private String fiterStr = "";
    //优化模式
    private boolean optimize = true;

    private static boolean stop = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NetworkInterface getDevice() {
        return device;
    }

    public void setDevice(NetworkInterface device) {
        this.device = device;
    }

    public boolean isPromisc() {
        return promisc;
    }

    public void setPromisc(boolean promisc) {
        this.promisc = promisc;
    }

    public boolean isToms() {
        return toms;
    }

    public void setToms(boolean toms) {
        this.toms = toms;
    }

    public boolean isNoblock() {
        return noblock;
    }

    public void setNoblock(boolean noblock) {
        this.noblock = noblock;
    }

    public boolean isCapFlag() {
        return capFlag;
    }

    public void setCapFlag(boolean capFlag) {
        this.capFlag = capFlag;
    }

    public int getCaplen() {
        return caplen;
    }

    public void setCaplen(int caplen) {
        this.caplen = caplen;
    }

    public int getTomsNum() {
        return tomsNum;
    }

    public void setTomsNum(int tomsNum) {
        this.tomsNum = tomsNum;
    }

    public int getCapcount() {
        return capcount;
    }

    public void setCapcount(int capcount) {
        this.capcount = capcount;
    }

    private static DeviceParam deviceParam = new DeviceParam();

    public static DeviceParam getInstance() {
        return deviceParam;
    }

    private DeviceParam() {
    }

    public String getFiterStr() {
        return fiterStr;
    }

    public void setFiterStr(String fiterStr) {
        this.fiterStr = fiterStr;
    }

    public boolean isOptimize() {
        return optimize;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    public static boolean isStop() {
        return stop;
    }

    public static DeviceParam getIntance() {
        return deviceParam;
    }

    public static void Stop() {
        stop = true;
        clear();
    }

    public static void Start() {
        stop = false;
    }

    private static void clear(){
        DeviceParam deviceParam=DeviceParam.deviceParam;
        deviceParam.setId(0);
        deviceParam.setDevice(null);
        deviceParam.setCapcount(1000);
        deviceParam.setFiterStr("");
        deviceParam.setOptimize(true);
        deviceParam.setTomsNum(50);
        deviceParam.setCaplen(1514);
        deviceParam.setCapFlag(false);
        deviceParam.setNoblock(false);
        deviceParam.setToms(false);
        deviceParam.setPromisc(true);
    }
    public static void startCaptury(PacketReceiver receiver) throws IOException {
        DeviceParam mypara = DeviceParam.getIntance();
        NetworkInterface interfance = mypara.getDevice();
        packetState state = packetState.getInstance();
        JpcapCaptor captor = JpcapCaptor.openDevice(interfance, mypara.getCaplen(), mypara.isPromisc(), mypara.getTomsNum());
        captor.setNonBlockingMode(mypara.isNoblock());
        if (mypara.fiterStr != "") {
            captor.setFilter(mypara.fiterStr, mypara.optimize);
        }
        if (getIntance().isToms()) {
            captor.setPacketReadTimeout(getIntance().getTomsNum());
        }
        int count = 0;
        DeviceParam.Start();
        if (mypara.capFlag) {
            while (!stop && count < mypara.capcount) {
                Packet packet = captor.getPacket();

                if (packet != null) {
                    receiver.receivePacket(packet);
                    count++;
                    state.setHandle(count);
                    captor.updateStat();
                    state.setRecvice(captor.received_packets);
                    state.setDrop(captor.dropped_packets);
                }
            }
            DeviceParam.Stop();
            System.out.println("设备关闭，停止捕获数据包");
            captor.close();
        } else {
            while (!stop) {
                Packet packet = captor.getPacket();
                if (packet != null) {
                    receiver.receivePacket(packet);
                    count++;
                    state.setHandle(count);
                    captor.updateStat();
                    state.setRecvice(captor.received_packets);
                    state.setDrop(captor.dropped_packets);
                }
            }

            System.out.println("设备关闭，停止捕获数据包");
            DeviceParam.Stop();
            captor.close();
        }
    }



    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DeviceParam{");
        sb.append("device=").append(device);
        sb.append(", promisc=").append(promisc);
        sb.append(", toms=").append(toms);
        sb.append(", noblock=").append(noblock);
        sb.append(", capFlag=").append(capFlag);
        sb.append(", caplen=").append(caplen);
        sb.append(", tomsNum=").append(tomsNum);
        sb.append(", fiterStr='").append(fiterStr).append('\'');
        sb.append(", optimize=").append(optimize);
        sb.append('}');
        return sb.toString();
    }
}
