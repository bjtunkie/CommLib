package com.commlib.v1;

import com.commlib.v1.comm.DefaultCustomThreadFactory;
import com.commlib.v1.network.ConnectionPool;
import com.commlib.v1.network.InternalStageArea;
import com.commlib.v1.network.StagingArea;
import com.commlib.v1.network.TCPServer;
import com.commlib.v1.network.utils.Util;

public class App {

    public static void main(String... args) {
        StagingArea stagingArea = new InternalStageArea(new ConnectionPool(), new DefaultCustomThreadFactory(), new Util());
        TCPServer tcpServer = TCPServer.instance;
        tcpServer.start(stagingArea);
    }
}
