package entity;

import jpcap.NetworkInterface;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;


public class BasePacket {
    //数据包的序号
    private int number=0;
    //网卡的ID
    private int ID;
    //捕获数据包的时间
    private long catchTime=System.currentTimeMillis();
    //网卡信息
    private NetworkInterface network;
    //以太网首部
    private EthernetPacket ethernet;
    //网络数据包
    private Packet packet;

    public void revice(){
        System.out.println(network.addresses.length);;
    }

    public BasePacket() {
    }

    public BasePacket(int number, int ID, long catchTime, NetworkInterface network, EthernetPacket ethernet, Packet packet) {
        this.number = number;
        this.ID = ID;
        this.catchTime = catchTime;
        this.network = network;
        this.ethernet = ethernet;
        this.packet = packet;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public long getCatchTime() {
        return catchTime;
    }

    public void setCatchTime(long catchTime) {
        this.catchTime = catchTime;
    }

    public NetworkInterface getNetwork() {
        return network;
    }

    public void setNetwork(NetworkInterface network) {
        this.network = network;
    }

    public EthernetPacket getEthernet() {
        return ethernet;
    }

    public void setEthernet(EthernetPacket ethernet) {
        this.ethernet = ethernet;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
