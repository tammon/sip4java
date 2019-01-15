package net.tammon.sip;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.tammon.sip.exceptions.SipException;
import net.tammon.sip.packets.Data;

class TCPConnectionTest {

    //@Disabled
    @Test
    void testReadDataSimple() {
        Properties props = new Properties();
        props.setProperty("host", "192.168.0.1");
        props.setProperty("sipPort", "35021");
        props.setProperty("leaseTimeout", "20000");
        props.setProperty("busyTimeout", "10000");
        props.setProperty("maxDelay", "500");
        props.setProperty("sipVersion", "1");
        props.put("keepAlive", false);
        
        try {
            TCPConnection tcp = new TCPConnection(props);
            Data data = tcp.readData(0, 0, "S-0-0000");
            assertNotNull(data);
            tcp.disconnect();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }    
    //@Disabled
    @Test
    void testReadData() {
        Properties props = new Properties();
        props.setProperty("host", "192.168.0.1");
        props.setProperty("sipPort", "35021");
        props.setProperty("leaseTimeout", "20000");
        props.setProperty("busyTimeout", "10000");
        props.setProperty("maxDelay", "500");
        props.setProperty("sipVersion", "1");
        props.put("keepAlive", false);
        
        try {
            TCPConnection tcp = new TCPConnection(props);
            Data data = tcp.readData(0, 0, "S-0-0017");
            assertNotNull(data);
            data = tcp.readData(0, 0, "S-0-0000");
            assertNotNull(data);
            tcp.disconnect();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

    @Disabled
    @Test
    void testReadData2() {
        Properties props = new Properties();

        props.setProperty("sipVersion", "1");
        props.put("keepAlive", false);
    }

}
