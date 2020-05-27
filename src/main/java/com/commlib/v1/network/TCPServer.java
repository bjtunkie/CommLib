package com.commlib.v1.network;

import com.commlib.v1.log.Log;
import com.commlib.v1.log.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPServer {
    private static final Log log = LogFactory.create(TCPServer.class);
    public static final TCPServer instance = new TCPServer();

    final Thread thread1 = new Thread(this::run);
    final Thread thread2 = new Thread(this::makeConnection);
    final String host = "0.0.0.0";
    final int port = 3004;
    final AtomicInteger count = new AtomicInteger(0);


    private TCPServer() {
    }

    private synchronized void establishConn(Socket socket) {
        BlockingQueue<byte[]> inputQueue = new LinkedBlockingQueue<>();
        TCPConnection tcpConnection = new TCPConnection(count.incrementAndGet(), socket, inputQueue);
        connectionPool.submitConn(tcpConnection);
        stageArea.register(inputQueue);
    }

    private void run() {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(host, port));
                log.info("TCP Server running on %s listening to port  %d for socket connections", host, port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    establishConn(socket);
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
        if (!thread1.isAlive()) {
            thread1.start();
        }

        if (!thread2.isAlive()) {
            thread2.start();
        }
    }

    private final List<HP> in = new LinkedList<>();

    private void makeConnection() {
        while (true) {

            synchronized (in) {
                if (!in.isEmpty()) {
                    in.forEach(o -> {
                        String host = o.host;
                        int port = o.port;

                        try {
                            Socket socket = new Socket(host, port);
                            establishConn(socket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    in.clear();
                }

                try {
                    in.wait(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void makeConnectionWith(String host, int port) {

        HP object = new HP(host, port);
        synchronized (in) {
            in.add(object);
            in.notifyAll();
        }
    }

    private class HP {
        final String host;
        final int port;

        public HP(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}
