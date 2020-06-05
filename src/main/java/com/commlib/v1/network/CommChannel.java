package com.commlib.v1.network;

import com.commlib.specs.Info;
import com.commlib.v1.comm.WorkerThread;

import java.util.function.Function;

public class CommChannel {
    private static final CommChannel instance = new CommChannel();
    private static StagingArea area;

    private CommChannel() {
    }

    public static <T extends WorkerThread> void instantiate(Class<T> customWorkerThread, ConnectionPool c, RequestPool r) {
        synchronized (CommChannel.class) {
            if (area == null) {
                area = new StagingArea(customWorkerThread, c, r);
            }
        }
    }


    public static <T, M extends Info> void send(Info info, Function<M, Boolean> reply) {
        area.send(info, reply);
    }


    public static void makeConnection() {

    }
}

