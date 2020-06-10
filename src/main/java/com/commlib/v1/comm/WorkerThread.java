package com.commlib.v1.comm;

import com.chat.EasyChef;
import com.commlib.proto.Info;
import com.commlib.proto.RequestPool;
import com.commlib.proto.Util;
import com.commlib.v1.exception.UniqueIDAlreadyExistsException;
import com.commlib.v1.utils.HandshakeInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.function.Function;

public abstract class WorkerThread extends Thread implements Util {
    private static final Set<Integer> hashCodes = new HashSet<>();
    private static final EasyChef chef = new EasyChef();
    private static final byte TASK_RECEIVE = 1;
    private static final byte TASK_SEND = 2;
    private static final byte TASK_CONNECT = 3;

    private final int hashCode;
    private final ThreadPool<? extends WorkerThread> factory;
    private final Object lock = new Object();
    private final List<Object> params;

    private final ConnectionPool connectionPool;
    private final RequestPool requestPool;
    private byte taskID = Byte.MIN_VALUE;

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

    public final void readInputFromConnection(byte[] input, TCPConnection conn) {
        synchronized (lock) {
            taskID = TASK_RECEIVE;
            params.clear();
            params.add(input);
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

    @Override
    public final void run() {
        final int offset = 24;

        while (true) {
            synchronized (lock) {
                if (taskID == Byte.MIN_VALUE) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Iterator<Object> in = params.iterator();
            switch (taskID) {
                case TASK_CONNECT: {
                    String uniqueID = (String) in.next();
                    String host = (String) in.next();
                    int port = (int) in.next();
                    Collection<TCPConnection> x = connectionPool.findConnectionBasedOn(uniqueID);
                    if (x.isEmpty()) {
                        try {
                            Socket socket = new Socket(host, port);
                            TCPConnection connection = new TCPConnection(socket, factory);
                            x.add(connection);
                            connectionPool.addConnection(connection);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                }
                case TASK_RECEIVE: {
                    byte[] data = (byte[]) in.next();
                    TCPConnection conn = (TCPConnection) in.next();
                    int tCode = byteArrayToInt(data, 0);
                    int lengthOfData = byteArrayToInt(data, 20);
                    String uniqueID = new String(data, 4, 16);

                    if (tCode == HandshakeInfo.CODE) {
                        HandshakeInfo info = chef.deSerialize(data, HandshakeInfo.class, offset, lengthOfData);
                        ConnectionPool c = factory.getConnectionPool();
                        c.submitConn(info.getSrcUniqueID(), conn);

                        /**
                         * Entry into dB.
                         * Send Response if any
                         */
                    }
                    work(data, tCode, uniqueID, lengthOfData);
                    break;
                }
                case TASK_SEND:
                    sendMessage((Info) in.next());
                    break;
            }

            release();

        }

    }

    private void sendMessage(Info outMessage) {
        byte[] uniqueBytes = outMessage.uniqueID().getBytes();
        int tCode = outMessage.transmitCode();

        assert uniqueBytes.length <= 16;

        byte[] out = chef.serialize(outMessage, 24);
        int x = out.length - 24;

        System.arraycopy(uniqueBytes, 0, out, 4, 16);
        intToByteArray(tCode, out, 0);
        intToByteArray(x, out, 20);

        Collection<TCPConnection> connections = connectionPool.findConnectionBasedOn(outMessage.uniqueID());
        connections.forEach(conn -> {
            conn.send(out);
        });
    }

    protected abstract void work(byte[] input, int transmitCode, String uniqueID, int lengthOfData);

    public final void release() {
        synchronized (lock) {
            params.clear();
            taskID = Byte.MIN_VALUE;
        }
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
