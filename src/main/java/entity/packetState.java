package entity;

public class packetState {
    private int recvice =0;

    private int drop=0;

    private int handle=0;

    private int show=0;

    private static packetState state=new packetState();
    public static packetState getInstance(){
        return state;
    }
    private packetState(){}

    public int getRecvice() {
        return recvice;
    }

    public void setRecvice(int recvice) {
        this.recvice = recvice;
    }

    public int getDrop() {
        return drop;
    }

    public void setDrop(int drop) {
        this.drop = drop;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public void clear(){
        this.recvice=0;
        this.drop=0;
        this.handle=0;
        this.show=0;
    }
}
