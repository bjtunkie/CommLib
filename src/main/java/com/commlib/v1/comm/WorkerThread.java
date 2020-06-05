package com.commlib.v1.comm;

import com.commlib.specs.Info;
import com.commlib.specs.Util;
import com.commlib.v1.exception.UniqueIDAlreadyExistsException;
import com.commlib.v1.network.ConnectionPool;
import com.commlib.v1.network.RequestPool;
import com.commlib.v1.network.TCPConnection;
import com.commlib.v1.utils.MarkableReference;
import com.lib.Chef;
import com.lib.EasyChef;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.function.Function;

public abstract class WorkerThread extends Thread implements Releasable, Util {
    private static final Set<Integer> hashCodes = new HashSet<>();
    private static final Chef chef = new EasyChef();
    private static final byte TASK_RECEIVE = 1;
    private static final byte TASK_SEND = 2;
    private static final byte TASK_CONNECT = 3;

    private final int hashCode;
    private final ThreadPool<? extends WorkerThread> factory;
    private final Object lock = new Object();
    private final List<Object> params;

    private final ConnectionPool connectionPool;
    private final RequestPool requestPool;
    private byte taskID;

    public <T extends WorkerThread> WorkerThread(int hash, ThreadPool<T> factory) {
        super("CustomWorkerThread-" + hash);
        if (hashCodes.contains(hash)) throw new UniqueIDAlreadyExistsException();
        else hashCodes.add(hash);
        this.hashCode = hash;
        this.factory = factory;

        this.requestPool = factory.getRequestPool();
        this.connectionPool = factory.getConnectionPool();
        this.params = new ArrayList<>(5);
    }


    public final <T extends Info> void assignOutMessage(Info info, Function<T, Boolean> reply) {

        requestPool.insert(info, reply);
        synchronized (lock) {
            taskID = TASK_SEND;
            params.clear();
            params.add(info);
            lock.notifyAll();
        }

    }


    public final void assignInMessageConnection(TCPConnection conn) {

        synchronized (lock) {
            taskID = TASK_RECEIVE;
            params.clear();
            params.add(conn);
            lock.notifyAll();
        }
    }


    public final void makeConnection(String uniqueID, String host, int port) {
        synchronized (lock) {
            taskID = TASK_CONNECT;
            params.clear();
            params.add(uniqueID);
            params.add(host);
            params.add(port);
            lock.notifyAll();
        }
    }

    private void createConnection(String uniqueID, String host, int port) {

        Collection<TCPConnection> x = connectionPool.findConnectionBasedOn(uniqueID);
        if (x.isEmpty()) {
            try {
                Socket socket = new Socket(host, port);
                TCPConnection connection = new TCPConnection(socket);
                x.add(connection);
                connectionPool.addConnection(connection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void run() {

        while (true) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Iterator<Object> in = params.iterator();
            switch (taskID) {
                case TASK_CONNECT:
                    String uniqueId = (String) in.next();
                    String host = (String) in.next();
                    int port = (int) in.next();
                    makeConnection(uniqueId, host, port);
                    break;
                case TASK_RECEIVE:
                    readMessage((TCPConnection) in.next());
                    break;

                case TASK_SEND:
                    sendMessage((Info) in.next());
                    break;
            }
            params.clear();
            release();

        }

    }

    private void readMessage(TCPConnection connection) {
        MarkableReference<byte[]> input = new MarkableReference<>(false);
        connection.receive(input);
        work(input.getReference(), this);
    }

    private void sendMessage(Info outMessage) {
        byte[] out = chef.serialize(outMessage, 5);
        Collection<TCPConnection> connections = connectionPool.findConnectionBasedOn(outMessage.uniqueID());
        connections.forEach(conn -> {
            conn.send(out);
        });

    }

    protected abstract <T extends WorkerThread> void work(byte[] input, T thread);

    @Override
    public final void release() {
        factory.putBack(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerThread that = (WorkerThread) o;
        return hashCode == that.hashCode;
    }

    @Override
    public final int hashCode() {
        return hashCode;
    }
}
