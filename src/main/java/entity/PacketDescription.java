package entity;

import java.util.Vector;

/**
 * 存储数据包的概述
 */
public class PacketDescription {
    //捕获的序号
    private int ident;
    //源地址
    private String source;
    //目的地址
    private String target;
    //协议
    private String protocol;
    //数据包的长度
    private int length;
    //数据包的概述
    private String description;

    public PacketDescription() {
    }

    public PacketDescription(int ident, String source, String target, String protocol, int length, String description) {
        this.ident = ident;
        this.source = source;
        this.target = target;
        this.protocol = protocol;
        this.length = length;
        this.description = description;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Vector<String> toVector(){
        Vector<String> packetDesc=new Vector<>();
        packetDesc.add(String.valueOf(this.ident));
        packetDesc.add(this.source);
        packetDesc.add(this.target);
        packetDesc.add(this.protocol);
        packetDesc.add(String.valueOf(this.length));
        packetDesc.add(this.description);
        return packetDesc;
    }
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PacketDescription{");
        sb.append("ident=").append(ident);
        sb.append(", source='").append(source).append('\'');
        sb.append(", target='").append(target).append('\'');
        sb.append(", protocol='").append(protocol).append('\'');
        sb.append(", length=").append(length);
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
