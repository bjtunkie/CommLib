package com.commlib.v1.comm;

import com.commlib.v1.log.Log;
import com.commlib.v1.log.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPConnection {
    static final int TIME_TO_LIVE = 1000 * 60 * 2; // 2 minutes to live.
    private final Log log = LogFactory.create(TCPConnection.class);
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Socket socket;
    private final ThreadPool<? extends WorkerThread> tFactory;
    private final Thread thread;
    private final byte[] buffer = new byte[Constant.BUFFER_SIZE];

    private String associatedUniqueID;


    public <T extends WorkerThread> TCPConnection(Socket socket, ThreadPool<T> threadFactory) {
        this("", socket, threadFactory);
    }

    public <T extends WorkerThread> TCPConnection(String uniqueID, Socket socket, ThreadPool<T> threadFactory) {
        this.socket = socket;
        InputStream is;
        OutputStream os;
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            is = null;
            os = null;
            close();
        }

        inputStream = is;
        outputStream = os;

        log.info("Connection with host: %s established", getAddress().getHostName());

        tFactory = threadFactory;
        thread = new Thread(this::receive);
        thread.start();

    }

    public InetAddress getAddress() {
        return socket.getInetAddress();
    }


    public void send(byte[] data) {
        try {
            System.out.println("Sending data: " + new String(data, 24, data.length - 24));
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receive() {

        try {
            while (true) {
                int count = inputStream.read(buffer);
                if (count > 0) {
                    log.debug("Received input from " + getAddress().getHostName());

                    byte[] data = new byte[count];
                    System.arraycopy(buffer, 0, data, 0, count);
                    tFactory.getThread().readInputFromConnection(data, this);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            /**
             * Socket disconnected.
             */
            close();
        }

    }


    public boolean isOpen() {
        return socket.isConnected();
    }

    public String getAssociatedUniqueID() {
        return associatedUniqueID;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TCPConnection that = (TCPConnection) o;
        return hashCode() == that.hashCode();
    }

}
