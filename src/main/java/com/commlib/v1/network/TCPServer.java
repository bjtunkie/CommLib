package com.commlib.v1.network;

import com.commlib.v1.log.Log;
import com.commlib.v1.log.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPServer {
    private static final Log log = LogFactory.create(TCPServer.class);
    public static final TCPServer instance = new TCPServer();

    final Thread thread = new Thread(this::run);
    final String host = "0.0.0.0";
    final int port = 3004;
    final AtomicInteger count = new AtomicInteger(0);

    private TCPServer() {
    }

    private void run() {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(host, port));
                log.info("TCP Server running on %s listening to port  %d for socket connections", host, port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    BlockingQueue<byte[]> inputQueue = new LinkedBlockingQueue<>();
                    TCPConnection tcpConnection = new TCPConnection(count.incrementAndGet(), socket, inputQueue);
                    connectionPool.submitConn(tcpConnection);
                    stageArea.register(inputQueue);

                }
            } catch (IOException e) {
                e.printStackTrace();
                log.d("Unable to start tcp server, will try again in 5 seconds.");
            }


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private StagingArea stageArea;
    private ConnectionPool connectionPool;

    public void start(StagingArea aStage) {
        synchronized (instance) {
            if (stageArea == null) {
                stageArea = aStage;
            }

            if (connectionPool == null) {
                connectionPool = stageArea.getConnectionPool();
            }

        }
        if (!thread.isAlive()) {
            thread.start();
        }
    }
}
