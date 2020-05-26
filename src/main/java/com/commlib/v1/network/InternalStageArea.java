package com.commlib.v1.network;

import com.commlib.v1.comm.CustomThreadFactory;
import com.commlib.v1.network.utils.Util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class InternalStageArea extends StagingArea {

    private final CustomThreadFactory threadFactory;
    private final Set<BlockingQueue<byte[]>> listOfInputQueues = new HashSet<>();
    private final Thread thread = new Thread(this::monitorInputs);

    public InternalStageArea(ConnectionPool pool, CustomThreadFactory threadFactory, Util utils) {
        super(pool, utils);
        this.threadFactory = threadFactory;
        this.thread.start();
    }


    @Override
    public final void register(BlockingQueue<byte[]> queue) {
        synchronized (listOfInputQueues) {
            listOfInputQueues.add(queue);
        }
    }


    private void monitorInputs() {
        listOfInputQueues.forEach(queue -> {
            if (!queue.isEmpty()) {
                queue.forEach(bytes -> {
                    while (!threadFactory.getThread().invoke(this, bytes)) ;
                });
            }

        });
    }

    @Override
    public void sendOff(final int transmitCode, final String unique, final byte[] data) {

        byte[] buf = new byte[data.length + 24];
        byte[] code = utils.toByteArray(transmitCode);
        byte[] len = utils.toByteArray(data.length);
        byte[] uniqueID = unique.getBytes();

        System.arraycopy(code, 0, buf, 0, 4);
        System.arraycopy(uniqueID, 0, buf, 4, 16);
        System.arraycopy(len, 0, buf, 20, 4);
        System.arraycopy(data, 0, buf, 24, data.length);

        connectionPool.findConnectionBasedOn(unique).forEach(connection -> {
            connection.send(buf);
        });

    }

}
