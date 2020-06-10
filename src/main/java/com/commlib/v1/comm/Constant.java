package com.commlib.v1.comm;

import java.util.Arrays;

public class Constant {
    public static final int FIVE_MINUTES = 1000 * 60 * 5;
    public static final int BUFFER_SIZE = 1024 * 1024 * 1;
    public static final String MAIN_HOST = "127.0.0.1";
    public static final Integer MAIN_PORT = 3004;

    public static final String DEFAULT_ID;

    static {

        byte[] x = new byte[16];
        Arrays.fill(x, (byte) 1);
        DEFAULT_ID = new String(x);
    }

}
