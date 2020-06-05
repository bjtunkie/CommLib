package com.commlib.v1.network;

import com.commlib.v1.utils.MarkableReference;
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
    private InputStream inputStream;
    private OutputStream outputStream;
    private final Socket socket;
    private final byte[] buffer = new byte[NetworkConstants.BUFFER_SIZE];

    private String associatedUniqueID;

    private volatile boolean running = false;

    public TCPConnection(Socket socket) {
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Connection with host: %s established", getAddress().getHostName());


    }

    public InetAddress getAddress() {
        return socket.getInetAddress();
    }


    public void send(byte[] data) {
        try {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive(final MarkableReference<byte[]> in) {
        running = true;
        try {
            while (!in.isMarked()) {
                int count = inputStream.read(buffer);
                if (count > 0) {
                    log.debug("Received input from " + getAddress().getHostName());

                    byte[] data = new byte[count];
                    System.arraycopy(buffer, 0, data, 0, count);
                    in.setReference(data, true);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            /**
             * Socket disconnected.
             */
            close();
        } finally {
            running = false;
        }

    }

    public boolean isRunning() {
        return running;
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
