package net.tammon.sip.packets;

public class BusyResponse extends AbstractPacket implements Response {
    
    public final static int MSG_BUSY = 68;


    @Override
    public int getMessageType() {
        return MSG_BUSY;
    }

    @Override
    public void setData(byte[] rawData) {
        
    }
    
    public static int getFixLength() {
        return 0;
    } 
}
