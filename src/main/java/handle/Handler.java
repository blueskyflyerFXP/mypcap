package handle;

import entity.BasePacket;
import entity.InfoNode;

public interface Handler {
    public void handlePacket(BasePacket base);
}
