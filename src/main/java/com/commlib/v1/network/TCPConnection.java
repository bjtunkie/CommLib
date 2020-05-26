package com.commlib.v1.network;

import com.commlib.v1.exception.UniqueIDAlreadyExistsException;
import com.commlib.v1.log.Log;
import com.commlib.v1.log.LogFactory;
import com.commlib.v1.network.utils.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class TCPConnection {
    static final Set<Integer> hashCodes = new HashSet<>();
    private final Log log = LogFactory.create(TCPConnection.class);
    private InputStream inputStream;
    private OutputStream outputStream;
    private final Socket socket;
    private final Thread inThread;
    private final byte[] buffer = new byte[NetworkConstants.BUFFER_SIZE];

    final BlockingQueue<byte[]> inputQueue;
    final String host;
    private final int hashCode;
    private final byte[] hashBytes;

    TCPConnection(int hash, Socket socket, BlockingQueue<byte[]> inputQueue) {
        if (hashCodes.contains(hash)) throw new UniqueIDAlreadyExistsException();
        else hashCodes.add(hash);
        this.hashCode = hash;
        this.hashBytes = Util.intToByteArray(hash);
        this.socket = socket;
        this.host = getAddress().getHostName();
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
        inThread = new Thread(this::receive, "Connection-" + hashCode);
        inThread.start();

        this.inputQueue = inputQueue;
        log.info("Connection with host: %s established", host);
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


    private void receive() {
        while (true) {
            try {
                int count = inputStream.read(buffer);
                if (count > 0) {
                    log.debug("Received input from " + host);
                    byte[] data = new byte[count + 4];
                    System.arraycopy(buffer, 0, data, 0, count);

                    /**
                     * Temp logic
                     */
                    System.arraycopy(hashBytes, 0, data, count, 4);

                    inputQueue.put(data);

                }
            } catch (IOException e) {
                e.printStackTrace();
                /**
                 * Socket disconnected.
                 */
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        return hashCode == that.hashCode;
    }

    private byte[] hashBytes() {
        return this.hashBytes;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
