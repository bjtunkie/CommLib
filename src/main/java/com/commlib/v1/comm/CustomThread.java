package com.commlib.v1.comm;

import com.commlib.v1.exception.UniqueIDAlreadyExistsException;
import com.commlib.v1.log.Log;
import com.commlib.v1.log.LogFactory;
import com.commlib.v1.network.ConnectionPool;
import com.commlib.v1.network.StagingArea;
import com.commlib.v1.network.TCPConnection;
import com.commlib.v1.network.TransmitCode;
import com.commlib.v1.network.utils.Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public abstract class CustomThread extends Thread implements Releasable {
    static final Log log = LogFactory.create(CustomThread.class);
    private static final Set<Integer> hashCodes = new HashSet<>();
    private final int hashCode;
    private final CustomThreadFactory factory;
    private final Object lock = new Object();


    public CustomThread(int hash, CustomThreadFactory factory) {
        super("CustomThread-" + hash);
        if (hashCodes.contains(hash)) throw new UniqueIDAlreadyExistsException();
        else hashCodes.add(hash);

        this.hashCode = hash;
        this.factory = factory;
    }

    private int transmitCode;
    private byte[] data;
    private StagingArea stagingArea;

    public final boolean invoke(StagingArea inputStageArea, byte[] data) {

        /**
         * information will come only when invoke is called from the staging area.
         * So no need to worry whether data will be available during processing. However putting additional lock.
         */
        if (this.transmitCode == Integer.MIN_VALUE) {
            return false;
        }
        synchronized (lock) {
            transmitCode = 0;
            this.data = data;
            this.stagingArea = inputStageArea;
            lock.notify();
            return true;
        }

    }


    @Override
    public final void run() {
        while (true) {

            while (true) {
                synchronized (lock) {
                    try {
                        lock.wait(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        log.info("lock.wait() for thread %s was interrupted as new data is available.", getName());
                        break;
                    }
                }
            }
            int lengthOfData = ((data[20] & 0xFF) << 24) | ((data[21] & 0xFF) << 16) | ((data[22] & 0xFF) << 8) | ((data[23] & 0xFF));
            int hashCodeOfConnection = Util.byteArrayToInt(data, lengthOfData + 24);
            BaseInfo baseInfo = null;
            ByteArrayInputStream bIS;
            ObjectInputStream oIS;
            transmitCode = ((data[0] & 0xFF) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | ((data[3] & 0xFF));

            bIS = new ByteArrayInputStream(data, 24, lengthOfData);
            try {
                oIS = new ObjectInputStream(bIS);
                baseInfo = (BaseInfo) oIS.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (baseInfo == null) {
                /**
                 * print some error
                 */
            } else {

                try {
                    String uniqueID = new String(data, 4, 16);

                    Field toUniqueID = BaseInfo.class.getField("toUniqueID");
                    Field transmitCode = BaseInfo.class.getField("transmitCode");

                    toUniqueID.setAccessible(true);
                    toUniqueID.set(baseInfo, uniqueID);

                    transmitCode.setAccessible(true);
                    transmitCode.set(baseInfo, transmitCode);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }


                if (transmitCode == TransmitCode.HANDSHAKE_CONNECTION_WITH_SERVER) {
                    /**
                     * todo
                     */
                    ConnectionPool connectionPool = stagingArea.getConnectionPool();
                    TCPConnection connection = connectionPool.findConnectionBasedOn(hashCodeOfConnection);
                    connectionPool.submitConn(baseInfo.getFromUniqueID(), connection);
                } else {
                    work(baseInfo);
                }
            }
            release();
        }

    }


    protected abstract void work(BaseInfo info);

    @Override
    public final void release() {
        synchronized (lock) {
            transmitCode = Integer.MIN_VALUE;
            data = null;
        }
        factory.putBack(this);

    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomThread that = (CustomThread) o;
        return hashCode == that.hashCode;
    }

    @Override
    public final int hashCode() {
        return hashCode;
    }
}
