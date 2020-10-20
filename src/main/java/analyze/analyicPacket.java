package analyze;

import entity.BasePacket;
import entity.InfoNode;
import handle.*;
import jpcap.packet.*;
import utils.PackInfo;

public class analyicPacket {
    public static InfoNode analyze(BasePacket base){
        Packet packet = base.getPacket();
        InfoNode root;
        if (packet instanceof TCPPacket) {
           root= PackInfo.packTCP(base);
        } else if (packet instanceof UDPPacket) {
           root= PackInfo.packUDP(base);
        } else if (packet instanceof ICMPPacket) {
            root=PackInfo.packICMP(base);
        } else if (packet instanceof ARPPacket) {
            root=PackInfo.packARP(base);
        } else if (packet instanceof IPPacket) {
            root=PackInfo.packIP(base);
        } else {
            root=PackInfo.packEthernet(base);
        }
        return root;
    }
}
