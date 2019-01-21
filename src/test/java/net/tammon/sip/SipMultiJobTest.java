package net.tammon.sip;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;


class SipMultiJobTest {

    @Test
    void testIdn() {
        Properties props = new Properties();
        props.setProperty("host", "127.0.0.1");
        props.setProperty("sipPort", "35021");
        props.setProperty("leaseTimeout", "50000");
        props.setProperty("busyTimeout", "30000");
        props.setProperty("sipVersion", "1");
        props.put("keepAlive", false);

        try {
            SipConnection tcp = SipFactory.newInstance(props);
            assertNotNull(tcp);
            SipJob[] requests = new SipJob[1];
            requests[0] = new SipJObject(0, 0, "S-0-0000");
            SipJobState[] datas = tcp.readDatas(requests);
            assertNotNull(datas);
            tcp.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testIdns() {
        Properties props = new Properties();
        props.setProperty("host", "127.0.0.1");
        props.setProperty("sipPort", "35021");
        props.setProperty("leaseTimeout", "50000");
        props.setProperty("busyTimeout", "30000");
        props.setProperty("sipVersion", "1");
        props.put("keepAlive", false);

        try {
            SipConnection tcp = SipFactory.newInstance(props);
            assertNotNull(tcp);
            SipJob[] requests = new SipJob[3];
            requests[0] = new SipJObject(0, 0, "S-0-0000");
            requests[1] = new SipJObject(0, 0, "S-0-0030");
            requests[2] = new SipJObject(0, 0, "S-0-0026");
            SipJobState[] datas = tcp.readDatas(requests);
            assertNotNull(datas);
            tcp.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Ignore
    // @Test
    void testIdnError() {
        Properties props = new Properties();
        props.setProperty("host", "127.0.0.1");
        props.setProperty("sipPort", "35021");
        props.setProperty("leaseTimeout", "50000");
        props.setProperty("busyTimeout", "30000");
        props.setProperty("sipVersion", "1");
        props.put("keepAlive", false);

        try {
            SipConnection tcp = SipFactory.newInstance(props);
            assertNotNull(tcp);
            SipJob[] requests = new SipJob[1];
            requests[0] = new SipJObject(0, 0, "S-0-0001");
            SipJobState[] datas = tcp.readDatas(requests);
            assertNotNull(datas);
            tcp.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testException() {
        Properties props = new Properties();
        props.setProperty("host", "127.0.0.1");
        props.setProperty("sipPort", "35021");
        props.setProperty("leaseTimeout", "50000");
        props.setProperty("busyTimeout", "30000");
        props.setProperty("sipVersion", "1");
        props.put("keepAlive", false);

        try {
            // Assertions.assertThrows(SipException.class, () -> {
            SipConnection tcp = SipFactory.newInstance(props);
            assertNotNull(tcp);
            SipJob[] requests = new SipJob[1];
            requests[0] = new SipJObject(0, 0, "S-0-1111");
            SipJobState[] datas = tcp.readDatas(requests);
            assertNotNull(datas);
            assertNotNull(datas[0].getException());
            tcp.disconnect();
            // }
            // );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
