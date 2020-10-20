package thread;

import entity.DeviceParam;
import jpcap.PacketReceiver;
import receive.Receiver;

import java.io.IOException;

public class StartCap extends Thread{
    @Override
    public void run() {
        DeviceParam param=DeviceParam.getIntance();
        System.out.println("设备启动，开始捕获数据包");
        Receiver receiver=Receiver.getIntance(param.getId(),param.getDevice());
        try {
            DeviceParam.Start();
            DeviceParam.startCaptury(receiver);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
