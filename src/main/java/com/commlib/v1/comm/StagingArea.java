package com.commlib.v1.comm;

import com.commlib.proto.CommunicationModule;
import com.commlib.proto.Info;
import com.commlib.proto.RequestPool;
import com.commlib.v1.log.Log;
import com.commlib.v1.log.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

final class StagingArea implements CommunicationModule {


    Log log = LogFactory.create(StagingArea.class);
    private final ThreadPool<? extends WorkerThread> threadFactory;
    private final RequestPool requestPool;
    private final ConnectionPool connectionPool;
    private final Thread TCPServer = new Thread(this::acceptConnections);
    private final Thread TCPMonitory = new Thread(this::monitorConnections);
    private final boolean localServer;

    <T extends WorkerThread> StagingArea(Class<T> customWorkerThread, ConnectionPool c, RequestPool r, boolean enableLocalServer) {
        threadFactory = new ThreadPool<>(c, r, customWorkerThread);
        requestPool = r;
        connectionPool = c;
        localServer = enableLocalServer;
        TCPServer.start();
        TCPMonitory.start();
    }

    public <T, M extends Info> void send(Info info, Function<M, Boolean> reply) {
        WorkerThread t = threadFactory.getThread();
        t.assignOutMessage(info, reply);
    }


    @Override
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    @Override
    public RequestPool getRequestPool() {
        return requestPool;
    }

    @Override
    public ThreadPool<? extends WorkerThread> getThreadFactory() {
        return threadFactory;
    }


    private void monitorConnections() {

        while (true) {
            try {
                Thread.sleep(Constant.FIVE_MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connectionPool.removeClosedConnections();

        }
    }

    private void acceptConnections() {

        final String host = "0.0.0.0";
        final int port = 3004;
        while (localServer) {
            try {
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(host, port));
                log.info("TCP Server running on %s listening to port  %d for socket connections", host, port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    TCPConnection conn = new TCPConnection(socket, threadFactory);
                    connectionPool.addConnection(conn);

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
}
