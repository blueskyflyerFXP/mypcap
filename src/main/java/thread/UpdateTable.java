package thread;

import entity.DeviceParam;
import entity.PacketDescription;
import entity.packetState;
import receive.Receiver;
import view.mainFrame;

import java.util.Vector;

public class UpdateTable extends Thread{
    private mainFrame main;

    public UpdateTable(mainFrame main){
        this.main=main;
    }

    @Override
    public void run() {
        System.out.println("表格更新线程开始");
        Receiver receiver=Receiver.getInstance();
        Vector<PacketDescription> description= receiver.getDescriptions();
        packetState state=packetState.getInstance();
        int count=0;
        while (true){
           if(count<description.size()){
               main.addTableItem(description.get(count).toVector());
               count++;
               state.setShow(count);
           }else if(DeviceParam.isStop()&&count==description.size()){
               System.out.println("表格更新线程结束");
               break;
           }else {
               try {
                   Thread.sleep(1000);
                   continue;
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }

        }
    }
}
