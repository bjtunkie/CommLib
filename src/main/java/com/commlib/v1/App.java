package com.commlib.v1;


import com.commlib.v1.comm.CommChannel;
import com.commlib.v1.comm.WorkerThread;

import java.util.function.Function;

public class App {

    public static void main(String... args) {


        CommChannel.instantiate(WorkerThread.class, null, true);

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
