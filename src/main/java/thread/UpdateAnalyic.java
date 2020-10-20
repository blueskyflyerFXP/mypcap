package thread;

import entity.BasePacket;
import handle.PacketHandle;
import receive.Receiver;
import utils.Transformate;
import view.mainFrame;

import java.util.Vector;

public class UpdateAnalyic extends Thread{
    private mainFrame main;
    private int selectId=0;

    public UpdateAnalyic(mainFrame main,int selectId){
        this.main=main;
        this.selectId=selectId;
    }

    @Override
    public void run() {
        Receiver receiver=Receiver.getInstance();
        Vector<BasePacket> packets= receiver.getPackets();
        PacketHandle handle=new PacketHandle();
        handle.handlePacket(packets.get(selectId));
        main.UpdateAnalyicContent(handle.getNode().toString());
    }
}
