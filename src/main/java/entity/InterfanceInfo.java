package entity;

import java.net.NetworkInterface;

public class InterfanceInfo {
    //网卡的描述
    private String descript;
    //捕获的包数
    private int capNum=0;
    //丢弃的包数
    private int dropNum=0;

    public InterfanceInfo() {
    }

    public InterfanceInfo(String descript, int capNum, int dropNum) {
        this.descript = descript;
        this.capNum = capNum;
        this.dropNum = dropNum;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public int getCapNum() {
        return capNum;
    }

    public void setCapNum(int capNum) {
        this.capNum = capNum;
    }

    public int getDropNum() {
        return dropNum;
    }

    public void setDropNum(int dropNum) {
        this.dropNum = dropNum;
    }
}
