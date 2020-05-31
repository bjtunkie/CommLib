package com.commlib.v1.network;

import com.commlib.v1.network.utils.Util;

import java.util.concurrent.BlockingQueue;

public abstract class StagingArea {
    protected final ConnectionPool connectionPool;
    protected final RequestPool requestPool;
    protected final Util utils;

    public StagingArea(ConnectionPool connectionPool, RequestPool requestPool, Util utils) {
        this.connectionPool = connectionPool;
        this.utils = utils;
        this.requestPool = requestPool;

    }

    public final ConnectionPool getConnectionPool() {
        return connectionPool;
    }


    public abstract void register(BlockingQueue<byte[]> queue);

    public abstract void sendOff(int transmitCode, String uniqueID, byte[] data);


}
