package com.commlib.v1.network;

import com.commlib.v1.network.ConnectionPool;
import com.commlib.v1.network.utils.Util;

import java.util.concurrent.BlockingQueue;

public abstract class StagingArea {
    public final ConnectionPool connectionPool;
    public final Util utils;

    public StagingArea(ConnectionPool connectionPool, Util utils) {
        this.connectionPool = connectionPool;
        this.utils = utils;

    }

    public final ConnectionPool getConnectionPool() {
        return connectionPool;
    }


    public abstract void register(BlockingQueue<byte[]> queue);

    public abstract void sendOff(int transmitCode, String uniqueID, byte[] data);


}
