package thread;

import entity.DeviceParam;
import entity.packetState;
import view.mainFrame;

import javax.swing.*;
import java.util.Vector;

public class UpdateState extends Thread{
private mainFrame main;

public UpdateState(mainFrame main){
    this.main=main;
}

    @Override
    public void run() {
        packetState state=packetState.getInstance();
        System.out.println("状态更新线程启动");
        while (true){
            main.setState(state.getRecvice(),state.getDrop(),state.getHandle(),state.getShow());
            try {
                if(DeviceParam.isStop()&&state.getShow()==state.getHandle()){
                    System.out.println("已停止捕获数据包,退出状态更新线程");
                    main.resetButton();
                    break;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
