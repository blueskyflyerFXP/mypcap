package receive;

import analyze.analyicPacket;
import entity.BasePacket;
import entity.InfoNode;
import entity.PacketDescription;
import handle.PacketHandle;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.*;
import utils.PackInfo;

import java.net.InetAddress;
import java.util.Vector;

/**
 * 接收数据帧
 */
public class Receiver implements PacketReceiver {
    //帧计数器
    private int count = 0;
    //网卡id
    private int id;
    //网卡对象
    private NetworkInterface network = null;
    //存储Base对象的容器
    private Vector<BasePacket> packets = new Vector<>();
    //存储PackeDescription对象
    private Vector<PacketDescription> descriptions = new Vector<>();

    private static Receiver receiver = new Receiver();

    private Receiver() {
    }


    private void setId(int id) {
        this.id = id;
    }

    private void setNetwork(NetworkInterface network) {
        this.network = network;
    }

    public static Receiver getInstance(){
        return receiver;
    }
    public static Receiver getIntance(int id, NetworkInterface network) {
        receiver.setId(id);
        receiver.setNetwork(network);
        return receiver;
    }

    //清空捕获数据帧的数据
    public void setCountEmpty() {
        this.count = 0;
        this.id=0;
        this.network=null;
        this.packets = new Vector<>();
        this.descriptions=new Vector<>();
        //通知垃圾回收器回收无用的指针
        System.gc();
    }

    public Vector<BasePacket> getPackets() {
        return packets;
    }
    public Vector<PacketDescription> getDescriptions(){return descriptions;}
    @Override
    public void receivePacket(Packet packet) {
            //帧序列递增
            count++;
            //封装数据包
            BasePacket base = new BasePacket(this.count, this.id, System.currentTimeMillis(), this.network, (EthernetPacket) packet.datalink, packet);
            packets.add(base);
            PacketDescription mydesc=packetDescription(packet,count);

            descriptions.add(mydesc);
            System.out.println(packets.size()+"----------"+count);
    }

    private PacketDescription packetDescription(Packet packet, int count) {
        PacketDescription description = new PacketDescription();
        if (packet instanceof TCPPacket) {
            TCPPacket tcp = (TCPPacket) packet;
            description.setIdent(count);
            description.setSource(tcp.src_ip.toString().replace("/", ""));
            description.setTarget(tcp.dst_ip.toString().replace("/", ""));
            description.setLength(tcp.len);
            description.setProtocol("TCP");
            StringBuilder flag = new StringBuilder("[");
            if (tcp.urg) {
                flag.append("URG ");
            }
            if (tcp.ack) {
                flag.append("ACK ");
            }
            if (tcp.rst) {
                flag.append("RESET ");
            }
            if (tcp.psh) {
                flag.append("PUSH ");
            }
            if (tcp.syn) {
                flag.append("SYN ");
            }
            if (tcp.fin) {
                flag.append("FIN");
            }
            flag.append("] ");
            if (tcp.ack) {
                flag.append("ACK=");
                flag.append(tcp.ack_num);
            }
            description.setDescription(tcp.src_port + "——>" + tcp.dst_port +" "+ flag.toString() + " seq=" + tcp.sequence + " win=" + tcp.window);
        } else if (packet instanceof UDPPacket) {
            UDPPacket udp = (UDPPacket) packet;
            description.setIdent(count);
            description.setSource(udp.src_ip.toString().replace("/", ""));
            description.setTarget(udp.dst_ip.toString().replace("/", ""));
            description.setLength(udp.len);
            description.setProtocol("UDP");
            description.setDescription(udp.src_port + "——>" + udp.dst_port + " Len=" + udp.length );
        } else if (packet instanceof ICMPPacket) {
            ICMPPacket icmp = (ICMPPacket) packet;
            description.setIdent(count);
            description.setSource(icmp.src_ip.toString().replace("/", ""));
            description.setTarget(icmp.dst_ip.toString().replace("/", ""));
            description.setLength(icmp.len);
            description.setProtocol("ICMP");
            StringBuilder type = new StringBuilder("");
            switch (icmp.type) {
                //Echo 应答报文
                case 0:
                    //设置type字段
                    type.append("Echo (ping) reply");
                    break;
                //终点不可达
                case 3:
                    //设置type字段
                    type.append("Unreach");
                    break;
                //源点抑制
                case 4:
                    //设置type字段
                    type.append("Source quench");
                    break;
                //改变路由或重定向
                case 5:
                    //设置type字段
                    type.append("Redirect");
                    break;
                //Echo 请求报文
                case 8:
                    //设置type字段
                    type.append("Echo (ping) request");
                    break;
                //请求超时
                case 11:
                    //设置type字段
                    type.append("Time-to-live eceeded");
                    break;
                //参数问题
                case 12:
                    //设置type字段
                    type.append("parament error");
                    break;
                //时间戳请求
                case 13:
                    //设置type字段
                    type.append("Timestamp request");
                    break;
                //时间戳应答
                case 14:
                    //设置type字段
                    type.append("Timestamp repply");
                    break;
                //其他
                default:
                    break;
            }
            description.setDescription(type + " id=" + icmp.id + " seq=" + icmp.seq + " ttl=" + icmp.hop_limit);
        } else if (packet instanceof ARPPacket) {
            ARPPacket arp = (ARPPacket) packet;
            description.setIdent(count);
            description.setSource(((InetAddress) arp.getSenderProtocolAddress()).toString().replace("/", ""));
            description.setTarget(((InetAddress) arp.getTargetProtocolAddress()).toString().replace("/", ""));
            description.setLength(arp.len);
            description.setProtocol("ARP");
            switch (arp.operation) {
                case 1:
                case 3:
                case 8:
                    StringBuilder request = new StringBuilder("Who has ");
                    request.append(((InetAddress) arp.getTargetProtocolAddress()).toString().replace("/", ""));
                    request.append("? Tell ");
                    request.append(((InetAddress) arp.getSenderProtocolAddress()).toString().replace("/", ""));
                    description.setDescription(request.toString());
                    break;
                case 2:
                case 4:
                case 9:
                    StringBuilder reply = new StringBuilder("");
                    reply.append(((InetAddress) arp.getSenderProtocolAddress()).toString().replace("/", ""));
                    reply.append(" at ");
                    reply.append((String) arp.getSenderHardwareAddress());
                    description.setDescription(reply.toString());
                default:
                    break;
            }
        } else if (packet instanceof IPPacket) {
            IPPacket ip = (IPPacket) packet;
            description.setIdent(count);
            description.setSource(ip.src_ip.toString().replace("/", ""));
            description.setTarget(ip.dst_ip.toString().replace("/", ""));
            description.setLength(ip.len);
            description.setProtocol("IP");
            StringBuilder flag = new StringBuilder("[");
            if (ip.more_frag) {
                flag.append("MF ");
            }
            if (ip.dont_frag) {
                flag.append("DF ");
            }
            flag.append("]");

            description.setDescription(flag.toString() + " version=" + ip.version + " id=" + ip.ident + " offset=" + ip.offset + " TTL=" + ip.hop_limit);
        } else {
            description.setIdent(count);
            description.setSource(((EthernetPacket) packet.datalink).getSourceAddress());
            description.setTarget(((EthernetPacket) packet.datalink).getDestinationAddress());
            description.setLength(packet.len);
            description.setProtocol("Internet");
            description.setDescription("Length=" + packet.len + " Captured=" + packet.caplen);
        }
        return description;
    }
}
