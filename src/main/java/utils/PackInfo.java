package utils;

import entity.BasePacket;
import entity.InfoNode;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.*;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 包装Packet的数据:
 * 1、网卡——硬件
 * 2、数据链路层——MAC帧
 * 3、互联网首部——网络层
 * 4、ARP和IP——网络层
 * 5、TCP、UDP、ICMP等——传输层
 * 6、HTTP——应用层
 */
public class PackInfo {
    //包装网卡信息
    private static InfoNode packNetworkInterfance(BasePacket base){
        InfoNode node=new InfoNode();
         //网卡
        node.setMessage("网卡信息（NetworkInterfance Infonation）");
        //System.out.println(node.getMessage());

        //网卡序号
        StringBuilder interfanceId=new StringBuilder("网卡序号（Interface id）: ");
        interfanceId.append(base.getID());
        //System.out.println(interfanceId);
        InfoNode idNode=new InfoNode(interfanceId.toString(),null);
        node.addItem(idNode);

        NetworkInterface network=base.getNetwork();

        //网卡名字
        StringBuilder name=new StringBuilder("名称（name）: ");
        name.append(network.name);
        //System.out.println(name);
        InfoNode nameNode=new InfoNode(name.toString(),null);
        node.addItem(nameNode);

        //网卡的描述
        StringBuilder description=new StringBuilder("描述（description）: ");
        description.append(network.description);
        //System.out.println(description);
        InfoNode descriptionNode=new InfoNode(description.toString(),null);
        node.addItem(descriptionNode);

        //网卡的数据链路名称
        StringBuilder datalinkName=new StringBuilder("网卡数据链路的名称（datalink name）: ");
        datalinkName.append(network.datalink_name);
        //System.out.println(datalinkName);
        InfoNode datalinkNameNode=new InfoNode(datalinkName.toString(),null);
        node.addItem(datalinkNameNode);

        //网卡的数据链路描述
        StringBuilder datalinkDesc=new StringBuilder("网卡数据链路的描述（datalink description）: ");
        datalinkDesc.append(network.datalink_description);
        //System.out.println(datalinkDesc);
        InfoNode datalinkDescNode=new InfoNode(datalinkDesc.toString(),null);
        node.addItem(datalinkDescNode);

        NetworkInterfaceAddress[] addresses=network.addresses;
        int len=addresses.length;
        //网卡的网络接口（可能有多个）：包括
        StringBuilder address=new StringBuilder("网络接口地址(Adresses): ");
        address.append(network.name);
        //System.out.println(address);
        InfoNode addressNode=new InfoNode(address.toString());
        node.addItem(addressNode);

        //网络接口的数量
        StringBuilder addressNum=new StringBuilder("网络接口的数量(Internet Interface Number):");
        addressNum.append(len);
        //System.out.println(addressNum);
        InfoNode addressNumNode=new InfoNode(addressNum.toString(),null);
        addressNode.addItem(addressNumNode);

        //所有的网络接口
        for (int i=0;i<len;i++){
            StringBuilder interfance=new StringBuilder("网络接口(Internet Interface)"+(i+1));
            //System.out.println(interfance);
            InfoNode interfanceNode=new InfoNode(interfance.toString());
            StringBuilder interfanceNum;
            InfoNode interfanceNumNode;
            if(addresses[i].address instanceof Inet6Address){
                interfanceNum=new StringBuilder("IPv6地址(IPv6 Adress): ");
                interfanceNum.append(addresses[i].address.toString().replace("/",""));
                interfanceNumNode=new InfoNode(interfanceNum.toString(),null);
                interfanceNode.addItem(interfanceNumNode);
            }else if(addresses[i].address instanceof Inet4Address){
                interfanceNum=new StringBuilder("IPv4地址(IPv4 Adress): ");
                interfanceNum.append(addresses[i].address.toString().replace("/",""));
                interfanceNumNode=new InfoNode(interfanceNum.toString(),null);
                interfanceNode.addItem(interfanceNumNode);
                try {
                    StringBuilder broadcast=new StringBuilder("广播地址(broadcast): ");
                    broadcast.append(addresses[i].broadcast.toString().replace("/",""));
                    InfoNode broadcastNode=new InfoNode(broadcast.toString(),null);
                    interfanceNode.addItem(broadcastNode);

                    StringBuilder destinationNet=new StringBuilder("网关(destination): ");
                    destinationNet.append(addresses[i].destination);
                    InfoNode destinationNetNode=new InfoNode(destinationNet.toString(),null);
                    interfanceNode.addItem(destinationNetNode);

                    StringBuilder subnet=new StringBuilder("子网掩码(subnet): ");
                    subnet.append(addresses[i].subnet.toString().replace("/",""));
                    InfoNode subnetNode=new InfoNode(subnet.toString(),null);
                    interfanceNode.addItem(subnetNode);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                interfanceNum=new StringBuilder("未知IP地址(Unknow): ");
                interfanceNumNode=new InfoNode(interfanceNum.toString(),null);
                interfanceNode.addItem(interfanceNumNode);
            }
            //System.out.println(interfanceNum);
            addressNode.addItem(interfanceNode);
        }

        return node;
    }
    /**
     * 包装Frame的信息
     *
     * @param base
     * @return
     */
    private static InfoNode packFrame(BasePacket base) {
        InfoNode root=packNetworkInterfance(base);
        InfoNode node = new InfoNode();
        Packet packet = base.getPacket();
        NetworkInterface network = base.getNetwork();

        //保存数据链路层MAC帧的基本信息,
        StringBuilder msg = new StringBuilder("以太网帧(Frame) ");
        msg.append(base.getNumber());
        node.setMessage(msg.toString());


        //创建新节点存储帧序号
        StringBuilder frameNumber = new StringBuilder("帧序列(Frame Number): ");
        frameNumber.append(base.getNumber());
        InfoNode numberNode = new InfoNode(frameNumber.toString(), null);
        node.addItem(numberNode);

        //创建新节点保存封装类型
        StringBuilder encapsulation = new StringBuilder("封装类型(Encapsulation type): ");
        encapsulation.append(network.datalink_description);
        encapsulation.append("(");
        encapsulation.append(network.datalink_name);
        encapsulation.append(")");
        InfoNode encapsuateNode = new InfoNode(encapsulation.toString(), null);
        node.addItem(encapsuateNode);

        //创建新节点保存到达时间
        StringBuilder arrivalTime = new StringBuilder("到达时间(Arrival Time): ");
        long arriavl = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(new Date(arriavl));
        arrivalTime.append(format);
        arrivalTime.append(".");
        arrivalTime.append(arriavl);
        arrivalTime.append(" ");
        arrivalTime.append(TimeZone.getDefault().getDisplayName());
        InfoNode arrivalInfo = new InfoNode(arrivalTime.toString(), null);
        node.addItem(arrivalInfo);

        //创建新节点存储时间戳
        StringBuilder otherTime = new StringBuilder("时间戳——毫秒(Timestamp Millisecond): ");
        otherTime.append(packet.sec);
        otherTime.append("\n\t时间戳——微秒(Timestamp Microsecond): ");
        otherTime.append(packet.usec);
        InfoNode otherNode = new InfoNode(otherTime.toString(), null);
        node.addItem(otherNode);

        //创建新节点存储帧长度
        StringBuilder frameLength = new StringBuilder("帧长度(Frame Length): ");
        frameLength.append(packet.len);
        frameLength.append("bytes (");
        frameLength.append(packet.len * 8);
        frameLength.append(" bits)");
        InfoNode lengthNode = new InfoNode(frameLength.toString(), null);
        node.addItem(lengthNode);

        //创建新节点存储捕获字节长度
        StringBuilder frameCaplen = new StringBuilder("捕获的长度(Capture Length): ");
        frameCaplen.append(packet.caplen);
        frameCaplen.append("bytes (");
        frameCaplen.append(packet.caplen * 8);
        frameCaplen.append(" bits)");
        InfoNode caplenNode = new InfoNode(frameCaplen.toString(), null);
        node.addItem(caplenNode);

        root.setNext(node);
        return root;
    }

    public static InfoNode packEthernet(BasePacket base) {
        InfoNode root=packFrame(base);
        InfoNode node = new InfoNode();
        EthernetPacket ethernetPacket = base.getEthernet();

        //互联网首部概况
        StringBuilder ethernetInfo = new StringBuilder("以太网首部(Ethernet II)");
        //System.out.println(ethernetInfo);
        node.setMessage(ethernetInfo.toString());

        //互联网数据包的源MAC地址
        StringBuilder src = new StringBuilder("源MAC地址(Source): ");
        src.append(ethernetPacket.getSourceAddress());
        //System.out.println(src);
        InfoNode srcNode = new InfoNode(src.toString());
        node.addItem(srcNode);


        //互联网数据包的目的MAC地址
        StringBuilder des = new StringBuilder("目的MAC地址(Destination): ");
        des.append(ethernetPacket.getDestinationAddress());
        //System.out.println(des);
        InfoNode desNode = new InfoNode(des.toString());
        node.addItem(desNode);

        //互联网数据包的类型
        StringBuilder type = new StringBuilder("类型(Type): ");
        int typeCode = ethernetPacket.frametype;
        switch (typeCode) {
            case 512:
                type.append("PUP");
                type.append("(0x0200)");
                break;
            case 2048:
                type.append("IP");
                type.append("(0x0800)");
                break;
            case 2054:
                type.append("ARP");
                type.append("(0x0806)");
                break;
            case -32715:
                type.append("RARP");
                type.append("(0x8035)");
                break;
            case -32512:
                type.append("VLAN");
                type.append("(0x8100)");
                break;
            case -31011:
                type.append("IPV6");
                type.append("(0x86dd)");
                break;
            case -28672:
                type.append("LOOPBACK");
                type.append("(0x9000)");
                break;
            default:
                type.append("unknow");
                type.append("(0X000)");
                break;
        }
        //System.out.println(type);
        InfoNode typeNode = new InfoNode(type.toString(), null);
        node.addItem(typeNode);
        root.getNext().setNext(node);
        return root;
    }

    public static InfoNode packARP(BasePacket base) {
        InfoNode root=packEthernet(base);
        InfoNode node=new InfoNode();
        ARPPacket packet=(ARPPacket)base.getPacket();
        //APR数据包概述
        StringBuilder arp = new StringBuilder("");
        arp.append("ARP协议(Address Resolution Protocol)");
        node.setMessage(arp.toString());

        //硬件类型
        StringBuilder hardType = new StringBuilder("硬件类型(Hardware Type): ");
        switch (packet.hardtype) {
            case 1:
                hardType.append("ETHERNET");
                break;
            case 6:
                hardType.append("IEEE802");
                break;
            case 15:
                hardType.append("FRAMERELAY");
                break;
            default:
                hardType.append("unknow");
        }
        InfoNode hardNode = new InfoNode(hardType.toString(), null);
        node.addItem(hardNode);

        //协议类型
        StringBuilder protocolType = new StringBuilder("协议类型(Protocol type): ");
        if(packet.prototype==2048){
            protocolType.append("IPv4");
            protocolType.append("(0x0800)");
        }else {
            protocolType.append("Unknow");
            protocolType.append("(0x0000)");
        }
        InfoNode protocolTypeNode=new InfoNode(protocolType.toString(),null);
        node.addItem(protocolTypeNode);

        //硬件类型字段长度
        StringBuilder hardLength = new StringBuilder("硬件类型字段长度(Hardware size): ");
        hardLength.append(packet.hlen);
        InfoNode hardLenNode = new InfoNode(hardLength.toString(), null);
        node.addItem(hardLenNode);

        //协议类型字段长度
        StringBuilder protocolLength = new StringBuilder("协议类型字段长度(Protocol size): ");
        protocolLength.append(packet.plen);
        InfoNode protoLenNode = new InfoNode(protocolLength.toString(), null);
        node.addItem(protoLenNode);

        //操作
        StringBuilder opCode = new StringBuilder("操作(Opcode): ");
        switch (packet.operation){
            case 1:
                opCode.append("APP_REQUEST");
                opCode.append("(");
                opCode.append(packet.operation);
                opCode.append(")");
                break;
            case 2:
                opCode.append("ARP_REPLY");
                opCode.append("(");
                opCode.append(packet.operation);
                opCode.append(")");
                break;
            case 3:
                opCode.append("RAPP_REQUEST");
                opCode.append("(");
                opCode.append(packet.operation);
                opCode.append(")");
                break;
            case 4:
                opCode.append("RARP_REPLY");
                opCode.append("(");
                opCode.append(packet.operation);
                opCode.append(")");
                break;
            case 8:
                opCode.append("INV_REQUEST");
                opCode.append("(");
                opCode.append(packet.operation);
                opCode.append(")");
                break;
            case 9:
                opCode.append("INV_REPLY");
                opCode.append("(");
                opCode.append(packet.operation);
                opCode.append(")");
                break;
            default:
                opCode.append(packet.operation);
                break;
        }
        InfoNode opcodeNode = new InfoNode(opCode.toString(), null);
        node.addItem(opcodeNode);

        //源MAC地址
        StringBuilder senderMac = new StringBuilder("源MAC地址(Sender MAC address): ");
        senderMac.append(packet.getSenderHardwareAddress());
        InfoNode sendMacNode = new InfoNode(senderMac.toString(), null);
        node.addItem(sendMacNode);

        //源IP地址
        StringBuilder senderIp = new StringBuilder("源IP地址(Sender IP address): ");
        senderIp.append(packet.getSenderProtocolAddress());
        InfoNode sendIpNode = new InfoNode(senderIp.toString(), null);
        node.addItem(sendIpNode);

        //目的MAC地址
        StringBuilder targetMac = new StringBuilder("目的MAC地址(Target MAC address): ");
        targetMac.append(packet.getTargetHardwareAddress());
        InfoNode targetMacNode = new InfoNode(targetMac.toString(), null);
        node.addItem(targetMacNode);

        //目的IP地址
        StringBuilder targetIp = new StringBuilder("目的IP地址(Target IP address): ");
        targetIp.append(packet.getTargetProtocolAddress());
        InfoNode targetIpNode = new InfoNode(targetIp.toString(), null);
        node.addItem(targetIpNode);
        root.getNext().getNext().setNext(node);
        return root;
    }


    public static InfoNode packIP(BasePacket base) {
        InfoNode root=packEthernet(base);
        InfoNode node=packIP2((IPPacket)base.getPacket());
        root.getNext().getNext().setNext(node);
        return root;
    }
    private static InfoNode packIP2(IPPacket packet) {

        InfoNode node=new InfoNode();
        //版本
        byte version=packet.version;
        StringBuilder IPName=new StringBuilder("IP协议(Interne Protocol Version ");
        if(version==4||version==6){
            IPName.append(version);
        }else {
            IPName=new StringBuilder("Unknow Version");
        }
        IPName.append(")");
        node.setMessage(IPName.toString());

        StringBuilder  versionName=new StringBuilder("版本号(Version): ");
        versionName.append(packet.version);
        InfoNode versionNode=new InfoNode(versionName.toString(),null);
        node.addItem(versionNode);

        //IPv4的信息
        if(packet.version==4){
            //报头长度
            StringBuilder headerLength=new StringBuilder("报头长度(Header Length): ");
            headerLength.append(packet.header.length);
            headerLength.append(" bytes");
            InfoNode headerLenNode=new InfoNode(headerLength.toString(),null);
            node.addItem(headerLenNode);

            //服务类型(区分服务)

            StringBuilder seriverField=new StringBuilder("区分服务(Differentiated Services Field): 0x");
            seriverField.append(Transformate.toHexString(packet.rsv_tos,2));
            InfoNode serviceFieldNode=new InfoNode(seriverField.toString());
            node.addItem(serviceFieldNode);

            //优先级
            StringBuilder priority=new StringBuilder("优先级(Priority): ");
            priority.append(packet.priority);
            InfoNode priorityNode=new InfoNode(priority.toString(),null);
            serviceFieldNode.addItem(priorityNode);

            //最小时延(Delay)
            StringBuilder delay=new StringBuilder("最小时延(Delay): ");
            delay.append(packet.d_flag);
            InfoNode delayNode=new InfoNode(delay.toString(),null);
            serviceFieldNode.addItem(delayNode);

            //最大吞吐量(Through)
            StringBuilder through=new StringBuilder("最大吞吐量(Through): ");
            through.append(packet.t_flag);
            InfoNode throughNode=new InfoNode(through.toString(),null);
            serviceFieldNode.addItem(throughNode);

            //最高可靠性(Reliability)
            StringBuilder reliability=new StringBuilder("最高可靠性(Reliability): ");
            reliability.append(packet.r_flag);
            InfoNode reliabilityNode=new InfoNode(reliability.toString(),null);
            serviceFieldNode.addItem(reliabilityNode);

            //最小费用(Cost)
            String binSercive=Integer.toBinaryString(packet.rsv_tos);
            int cost=Integer.valueOf(binSercive.charAt(binSercive.length()-1));
            StringBuilder mycost=new StringBuilder("最小费用(Cost): ");
            mycost.append(packet.r_flag);
            InfoNode costNode=new InfoNode(mycost.toString(),null);
            serviceFieldNode.addItem(costNode);


            //总长度
            StringBuilder totalLength=new StringBuilder("总长度(Total Length): ");
            totalLength.append(packet.length);
            InfoNode totalLenNode=new InfoNode(totalLength.toString(),null);
            node.addItem(totalLenNode);

            //标识
            StringBuilder identification=new StringBuilder("标识(Identification): 0x");
            identification.append(Integer.toHexString(packet.ident));
            identification.append("(");
            identification.append(packet.ident);
            identification.append(")");
            InfoNode identNode=new InfoNode(identification.toString(),null);
            node.addItem(identNode);

            //标志
            StringBuilder flags=new StringBuilder("标志(Flags): \n\t");
            //RB标志
            flags.append("RF标志(Reserved bit): ");
            if(!packet.r_flag){
                flags.append("Not ");
            }
            flags.append("Set\n\t");
            //DF标志
            flags.append("DF标志(Don't Fragment): ");
            if(!packet.dont_frag){
                flags.append("Not ");
            }
            flags.append("Set\n\t");
            //MF标志
            flags.append("MF标志(More Frament): ");
            if(!packet.more_frag){
                flags.append("Not ");
            }
            flags.append("Set");
            InfoNode flagsNode=new InfoNode(flags.toString(),null);
            node.addItem(flagsNode);

            //生存时间
            StringBuilder liveTime=new StringBuilder("生存时间TTL(Time to Live): ");
            liveTime.append(packet.hop_limit);
            InfoNode liveTimeNode=new InfoNode(liveTime.toString(),null);
            node.addItem(liveTimeNode);

            //片偏移
            StringBuilder offset=new StringBuilder("片偏移(Frame Offset): ");
            offset.append(packet.offset);
            InfoNode offsetNode=new InfoNode(offset.toString(),null);
            node.addItem(offsetNode);

            //协议类型
            StringBuilder protocol=new StringBuilder("协议类型(Protocol): ");
            int myprotocol=packet.protocol;
            switch(myprotocol)
            {
                case 0: protocol.append( "HOPOPT");break;
                case 1: protocol.append( "ICMP");break;
                case 2:protocol.append( "IGMP");;break;
                case 6:protocol.append( "TCP");break;
                case 8:protocol.append("EGP");break;
                case 9:protocol.append("IGP");break;
                case 17:protocol.append("UDP");break;
                case 41:protocol.append("IPv6");break;
                case 89:protocol.append("OSPF");break;
                default :protocol.append("Other"); break;
            }
            protocol.append("(0x");
            protocol.append(Transformate.toHexString(myprotocol,4));
            protocol.append(")");
            InfoNode protocolNode=new InfoNode(protocol.toString(),null);
            node.addItem(protocolNode);

        }

        if(packet.version==6){
            //种类
            StringBuilder clc=new StringBuilder("通讯类(Traffic Class): 0x");
            clc.append(Transformate.toHexString(packet.priority,2));
            InfoNode clcNode=new InfoNode(clc.toString(),null);
            node.addItem(clcNode);

            //流标签
            StringBuilder flowLabel=new StringBuilder("流标签(Flow Label): 0x");
            flowLabel.append(packet.flow_label);
            InfoNode labelNode=new InfoNode(flowLabel.toString(),null);
            node.addItem(labelNode);

            //有效载荷长度
            StringBuilder payload=new StringBuilder("有效载荷长度(Payload): ");
            payload.append(packet.length);
            InfoNode loadNode=new InfoNode(payload.toString(),null);
            node.addItem(loadNode);


            //下一个首部
            StringBuilder nextHeaner=new StringBuilder("下一个首部(Next Header): ");
            switch (packet.protocol){
                case 0: nextHeaner.append( "HOPOPT");break;
                case 1: nextHeaner.append( "ICMP");break;
                case 2:nextHeaner.append( "IGMP");;break;
                case 6:nextHeaner.append( "TCP");break;
                case 8:nextHeaner.append("EGP");break;
                case 9:nextHeaner.append("IGP");break;
                case 17:nextHeaner.append("UDP");break;
                case 41:nextHeaner.append("IPv6");break;
                case 89:nextHeaner.append("OSPF");break;
                case 43:nextHeaner.append("Routev6");break;
                case 44:nextHeaner.append("Fragv6");break;
                case 58:nextHeaner.append("ICMPv6");break;
                case 59:nextHeaner.append("NoNxtv6");break;
                case 60:nextHeaner.append("Optsv6");break;
                default:nextHeaner.append("Other"); break;
            }
            nextHeaner.append("(0x");
            nextHeaner.append(Transformate.toHexString(packet.protocol,4));
            nextHeaner.append(")");
            InfoNode nextHeaderNode=new InfoNode(nextHeaner.toString(),null);
            node.addItem(nextHeaderNode);

            //跳数限制
            StringBuilder hopLimit=new StringBuilder("跳数限制(Hop Limit): ");
            hopLimit.append(packet.hop_limit);
            InfoNode hopLimitNode=new InfoNode(hopLimit.toString(),null);
            node.addItem(hopLimitNode);

        }
        //源IP
        StringBuilder srcIp=new StringBuilder("源IP地址(Source IPAdress): ");
        srcIp.append(packet.src_ip.toString().replace("/",""));
        InfoNode srcNode=new InfoNode(srcIp.toString(),null);
        node.addItem(srcNode);

        //目的IP
        StringBuilder destIP=new StringBuilder("目标IP地址(Destination IPAdress): ");
        destIP.append(packet.dst_ip.toString().replace("/",""));
        InfoNode destNode=new InfoNode(destIP.toString(),null);
        node.addItem(destNode);

        return node;
    }
    public static InfoNode packTCP(BasePacket base) {
        InfoNode root=packIP(base);
        InfoNode node=new InfoNode();
        TCPPacket packet=(TCPPacket)base.getPacket();
        //TCP协议
        StringBuilder tcpName=new StringBuilder("TCP协议(Transmission Control Protocol)");
        node.setMessage(tcpName.toString());

        //源端口
        StringBuilder srcProt=new StringBuilder("源端口(Source Port): ");
        srcProt.append(packet.src_port);
        InfoNode srcPortNode=new InfoNode(srcProt.toString(),null);
        node.addItem(srcPortNode);

        //目的端口
        StringBuilder destPort=new StringBuilder("目的端口(Destination Port): ");
        destPort.append(packet.dst_port);
        InfoNode destPortNode=new InfoNode(destPort.toString(),null);
        node.addItem(destPortNode);

        //顺序号
        StringBuilder seqNum=new StringBuilder("顺序号(Sequence Number): ");
        seqNum.append(packet.sequence);
        InfoNode seqNumNode=new InfoNode(seqNum.toString(),null);
        node.addItem(seqNumNode);

        //确认号
        StringBuilder ackNum=new StringBuilder("确认号(Acknowledgment Number): ");
        ackNum.append(packet.ack_num);
        InfoNode ackNumNode=new InfoNode(ackNum.toString(),null);
        node.addItem(ackNumNode);

        //TCP头部长度
        StringBuilder headerLength=new StringBuilder("头部长度(Header Length): ");
        headerLength.append(packet.header.length);
        headerLength.append(" bytes");
        InfoNode headerLenNode=new InfoNode(headerLength.toString(),null);
        node.addItem(headerLenNode);
        //保留位
        StringBuilder retain1=new StringBuilder("保留位1(Retain1): ");
        retain1.append(packet.rsv1);
        InfoNode retain1Node=new InfoNode(retain1.toString(),null);
        node.addItem(retain1Node);

        StringBuilder retain2=new StringBuilder("保留位2(Retain2): ");
        retain2.append(packet.rsv2);
        InfoNode retain2Node=new InfoNode(retain2.toString(),null);
        node.addItem(retain2Node);

        //标志
        //Urgent
        StringBuilder urg=new StringBuilder("紧急指针标志(Urgent): ");
        if(!packet.urg){
            urg.append("Not ");
        }
        urg.append("Set");
        InfoNode urgNode=new InfoNode(urg.toString(),null);
        node.addItem(urgNode);

        //Acknowledgment
        StringBuilder ack=new StringBuilder("确认字段标志(Acknowledgment): ");
        if(!packet.ack){
            ack.append("Not ");
        }
        ack.append("Set");
        InfoNode ackNode=new InfoNode(ack.toString(),null);
        node.addItem(ackNode);

        //Rush
        StringBuilder push=new StringBuilder("推送标志(Push): ");
        if(!packet.psh){
            push.append("Not ");
        }
        push.append("Set");
        InfoNode pushNode=new InfoNode(push.toString(),null);
        node.addItem(pushNode);

        //Reset
        StringBuilder reset=new StringBuilder("复位标志(Reset): ");
        if(!packet.rst){
            reset.append("Not ");
        }
        reset.append("Set");
        InfoNode resetNode=new InfoNode(reset.toString(),null);
        node.addItem(resetNode);

        //SYN
        StringBuilder syn=new StringBuilder("同步标志(SYN): ");
        if(!packet.syn){
            syn.append("Not ");
        }
        syn.append("Set");
        InfoNode synNode=new InfoNode(syn.toString(),null);
        node.addItem(synNode);

        //FIN
        StringBuilder fin=new StringBuilder("终止标志(FIN): ");
        if(!packet.fin){
            fin.append("Not ");
        }
        fin.append("Set");
        InfoNode finNode=new InfoNode(fin.toString(),null);
        node.addItem(finNode);

        //窗口大小
        StringBuilder window = new StringBuilder("窗口大小(Window size Value): ");
        window.append(packet.window);
        InfoNode windowNode=new InfoNode(window.toString(),null);
        node.addItem(windowNode);

        //紧急指针
        StringBuilder urgentPointer=new StringBuilder("紧急指针(Urgent Pointer): ");
        urgentPointer.append(packet.urgent_pointer);
        InfoNode pointerNode=new InfoNode(urgentPointer.toString(),null);
        node.addItem(pointerNode);

        //数据
        StringBuilder data=new StringBuilder("数据(Data):");
        InfoNode dataNode=new InfoNode(data.toString());
        node.addItem(dataNode);
        StringBuilder dataLen=new StringBuilder("数据长度(Data Length): ");
        dataLen.append(packet.data.length);
        dataLen.append("bytes");
        InfoNode dataLenNode=new InfoNode(dataLen.toString(),null);
        dataNode.addItem(dataLenNode);
        StringBuilder dataContent=new StringBuilder("内容（content）: 0x");
        dataContent.append(Transformate.hexString(packet.data,false));
        InfoNode dataContentNode=new InfoNode(dataContent.toString(),null);
        dataNode.addItem(dataContentNode);

        root.getNext().getNext().getNext().setNext(node);
        return root;
    }

    public static InfoNode packUDP(BasePacket base) {
        InfoNode root=packIP(base);
        InfoNode node=new InfoNode();
        UDPPacket packet=(UDPPacket) base.getPacket();
        //UDP协议
       StringBuilder  udpName=new StringBuilder("UDP协议(User Datagram Protocol)");
       node.setMessage(udpName.toString());

        //源端口
        StringBuilder srcProt=new StringBuilder("源端口(Source Port): ");
        srcProt.append(packet.src_port);
        InfoNode srcPortNode=new InfoNode(srcProt.toString(),null);
        node.addItem(srcPortNode);

        //目的端口
        StringBuilder destPort=new StringBuilder("目的端口(Destination Port): ");
        destPort.append(packet.dst_port);
        InfoNode destPortNode=new InfoNode(destPort.toString(),null);
        node.addItem(destPortNode);

        //数据包长度
        StringBuilder length=new StringBuilder("数据包长度(Length): ");
        length.append(packet.length);
        InfoNode lengthNode=new InfoNode(length.toString(),null);
        node.addItem(lengthNode);

        //数据
        StringBuilder data=new StringBuilder("数据(Data):");
        InfoNode dataNode=new InfoNode(data.toString());
        node.addItem(dataNode);
        StringBuilder dataLen=new StringBuilder("数据长度(Data Length): ");
        dataLen.append(packet.data.length);
        dataLen.append("bytes");
        InfoNode dataLenNode=new InfoNode(dataLen.toString(),null);
        dataNode.addItem(dataLenNode);
        StringBuilder dataContent=new StringBuilder("内容（content）: 0x");
        dataContent.append(Transformate.hexString(packet.data,false));
        InfoNode dataContentNode=new InfoNode(dataContent.toString(),null);
        dataNode.addItem(dataContentNode);

        root.getNext().getNext().getNext().setNext(node);
        return root;
    }
    public static InfoNode packICMP(BasePacket base){
        InfoNode root=packIP(base);
        InfoNode node=new InfoNode();
        ICMPPacket packet=(ICMPPacket) base.getPacket();
        //ICMP协议
        StringBuilder icmpName=new StringBuilder("Internet Control Message Protocol");
        //System.out.println(icmpName);
        node.setMessage(icmpName.toString());

        //类型
        StringBuilder type=new StringBuilder("类型(Type): ");
        type.append(packet.type);
        //System.out.println(type);
        InfoNode typeNode=new InfoNode(type.toString(),null);
        node.addItem(typeNode);

        //代码
        StringBuilder code=new StringBuilder("代码(Code): ");
        code.append(packet.code);
        //System.out.println(code);
        InfoNode codeNode=new InfoNode(code.toString(),null);
        node.addItem(codeNode);

        //校验和
        StringBuilder checksum=new StringBuilder("校验和(Checknum): 0x");
        checksum.append(Integer.toHexString(packet.checksum));
        //System.out.println(checksum);
        InfoNode checkNumNode=new InfoNode(checksum.toString(),null);
        node.addItem(checkNumNode);

        //id
        StringBuilder ident=new StringBuilder("标识(Identifier): ");
        ident.append(packet.id);
        ident.append("(0x");
        ident.append(Integer.toHexString(packet.id));
        ident.append(")");
        //System.out.println(ident);
        InfoNode identNode=new InfoNode(ident.toString(),null);
        node.addItem(identNode);

        //序列号
        StringBuilder seq=new StringBuilder("序列号(Sequence number): ");
        seq.append(packet.seq);
        seq.append("(0x");
        seq.append(Integer.toHexString(packet.seq));
        seq.append(")");
        //System.out.println(seq);
        InfoNode seqNode=new InfoNode(seq.toString(),null);
        node.addItem(seqNode);

        //不同种类的ICMP报文
        switch (packet.type){
            //Echo 应答报文
            case 0:
                //设置type字段
                type.append("(");
                type.append("Echo (ping) reply");
                type.append(")");
                typeNode.setMessage(type.toString());
                break;
            //终点不可达
            case 3:
                //设置type字段
                type.append("(");
                type.append("Unreach");
                type.append(")");
                typeNode.setMessage(type.toString());

                //需要进行分片但设置不分片位
                if(packet.code==4){
                    StringBuilder mtu=new StringBuilder("下一跳最大传送单元(Next Hop MTU): ");
                    mtu.append(packet.mtu);
                    InfoNode mtuNode=new InfoNode(mtu.toString(),null);
                    node.addItem(mtuNode);
                }

                InfoNode ipNode=PackInfo.packIP2(packet.ippacket);
                node.addItem(ipNode);
                break;
            //源点抑制
            case 4:
                //设置type字段
                type.append("(");
                type.append("Source quench");
                type.append(")");
                typeNode.setMessage(type.toString());
                InfoNode ipNode1=PackInfo.packIP2(packet.ippacket);
                node.addItem(ipNode1);
                break;
            //改变路由或重定向
            case 5:
                //设置type字段
                type.append("(");
                type.append("Redirect");
                type.append(")");
                typeNode.setMessage(type.toString());
                StringBuilder redit=new StringBuilder("重定向的IP地址(Redirect IP Address): ");
                redit.append(packet.redir_ip);
                InfoNode reditNode=new InfoNode(redit.toString(),null);
                node.addItem(reditNode);
                InfoNode ipNode2=PackInfo.packIP2(packet.ippacket);
                node.addItem(ipNode2);
                break;
            //Echo 请求报文
            case 8:
                //设置type字段
                type.append("(");
                type.append("Echo (ping) request");
                type.append(")");
                typeNode.setMessage(type.toString());
                break;
            //请求超时
            case 11:
                //设置type字段
                type.append("(");
                type.append("Time-to-live eceeded");
                type.append(")");
                typeNode.setMessage(type.toString());
                InfoNode ipNode3=PackInfo.packIP2(packet.ippacket);
                node.addItem(ipNode3);
                break;
            //参数问题
            case 12:
                //设置type字段
                type.append("(");
                type.append("parament error");
                type.append(")");
                typeNode.setMessage(type.toString());
                InfoNode ipNode4=PackInfo.packIP2(packet.ippacket);
                node.addItem(ipNode4);
                break;
            //时间戳请求
            case 13:
                //设置type字段
                type.append("(");
                type.append("Timestamp request");
                type.append(")");
                typeNode.setMessage(type.toString());

                //发起时间戳
                StringBuilder origTime=new StringBuilder("发起时间戳(Origical Timestamp): ");
                origTime.append(packet.orig_timestamp);
                InfoNode origTimeNode=new InfoNode(origTime.toString(),null);
                node.addItem(origTimeNode);

                //接收时间戳
                StringBuilder recvTime=new StringBuilder("接收时间戳(Recviced Timestamp): ");
                recvTime.append(packet.recv_timestamp);
                InfoNode recvTimeNode=new InfoNode(recvTime.toString(),null);
                node.addItem(recvTimeNode);

                //传送时间戳
                StringBuilder transTime=new StringBuilder("传送时间戳(Transmit Timestamp): ");
                transTime.append(packet.trans_timestamp);
                InfoNode transTimeNode=new InfoNode(transTime.toString(),null);
                node.addItem(transTimeNode);
                break;
            //时间戳应答
            case 14:
                //设置type字段
                type.append("(");
                type.append("Timestamp repply");
                type.append(")");
                typeNode.setMessage(type.toString());
                //发起时间戳
                StringBuilder origTime1=new StringBuilder("发起时间戳(Origical Timestamp): ");
                origTime1.append(packet.orig_timestamp);
                InfoNode origTimeNode1=new InfoNode(origTime1.toString(),null);
                node.addItem(origTimeNode1);

                //接收时间戳
                StringBuilder recvTime1=new StringBuilder("接收时间戳(Recviced Timestamp): ");
                recvTime1.append(packet.recv_timestamp);
                InfoNode recvTimeNode1=new InfoNode(recvTime1.toString(),null);
                node.addItem(recvTimeNode1);

                //传送时间戳
                StringBuilder transTime1=new StringBuilder("传送时间戳(Transmit Timestamp): ");
                transTime1.append(packet.trans_timestamp);
                InfoNode transTimeNode1=new InfoNode(transTime1.toString(),null);
                node.addItem(transTimeNode1);
                break;
            //其他
            default:break;
        }
        root.getNext().getNext().getNext().setNext(node);
        return root;
    }

    public static InfoNode packIPv6Option(InfoNode node, IPPacket packet) {

        return node;
    }
    public static InfoNode packHTTP(InfoNode node, TCPPacket packet) {

        return node;
    }

}
