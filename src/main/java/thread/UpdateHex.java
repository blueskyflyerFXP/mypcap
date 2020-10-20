package thread;

import entity.BasePacket;
import entity.PacketDescription;
import receive.Receiver;
import utils.Transformate;
import view.mainFrame;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class UpdateHex extends Thread{
    private mainFrame main;
    private int selectId=0;

    public UpdateHex(mainFrame main,int selectId){
        this.main=main;
        this.selectId=selectId;
    }

    @Override
    public void run() {
        Receiver receiver=Receiver.getInstance();
        Vector<BasePacket> packets= receiver.getPackets();
        byte[] heaher=receiver.getPackets().get(selectId).getPacket().header;
        byte[] data=receiver.getPackets().get(selectId).getPacket().data;
        byte[] result=new byte[heaher.length+data.length];
        for (int i=0;i<heaher.length+data.length;i++){
            if(i<heaher.length){
                result[i]=heaher[i];
            }else {
                result[i]=data[i-heaher.length];
            }
        }
        main.setHexData(Transformate.hexString(result,true));
    }
}
