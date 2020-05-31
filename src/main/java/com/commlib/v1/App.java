package com.commlib.v1;

import com.commlib.v1.serializer.ComplexObject;
import com.commlib.v1.serializer.LWSerializerEngine;
import com.commlib.v1.serializer.TestObject;

import java.io.DataInputStream;
import java.lang.reflect.Field;
import java.util.function.Function;

public class App {

    public static void main(String... args) {
//        StagingArea stagingArea = new InternalStageArea(new ConnectionPool(), new DefaultCustomThreadFactory(), new Util());
//        TCPServer tcpServer = TCPServer.instance;
//        tcpServer.start(stagingArea);

        TestObject testObject = new TestObject();
        testObject.setVariable(11);

        Field[] fields = ComplexObject.class.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getType().toString());
        }

        String string;

//        LWSerializerEngine lwSerializerEngine = new LWSerializerEngine();
//        byte[] data = lwSerializerEngine.serialize(testObject);
//
//        TestObject newObject = lwSerializerEngine.deSerialize(data, TestObject.class);
//        System.out.println(newObject.getVariable());

    }

    public static int check(String ip) {
        byte[] addr = new byte[4];
        String[] parts = ip.split("\\.");

        int ipAddr;
        for (ipAddr = 0; ipAddr < 4; ++ipAddr) {
            addr[ipAddr] = (byte) Integer.parseInt(parts[ipAddr]);
        }


        ipAddr = (addr[0] & 0xFF) << 24 | (addr[1] & 0xFF) << 16 | (addr[2] & 0xFF) << 8 | addr[3] & 0xFF;
        System.out.println(ipAddr);
        return ipAddr;
    }

    private static void back(int m) {
        Function<Byte, Integer> parse = (b) -> {
            return b < 0 ? 256 + b : b;
        };
        String value = parse.apply((byte) (m >> 24))
                + "." + parse.apply((byte) (m >> 16))
                + "." + parse.apply((byte) (m >> 8))
                + "." + parse.apply((byte) m);

        System.out.println(value);
    }

}
