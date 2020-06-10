package com.commlib.v1.comm;

import com.commlib.proto.Info;
import com.commlib.proto.RequestPool;

import java.util.function.Function;

public final class CommChannel {
    private static StagingArea area;

    private CommChannel() {
    }

    public static <T extends WorkerThread> void instantiate(Class<T> customWorkerThread, RequestPool r, boolean enableLocalServer) {
        synchronized (CommChannel.class) {
            if (area == null) {
                area = new StagingArea(customWorkerThread, new ConnectionPool(), r == null ? new CustomRequestPool() : r, enableLocalServer);
            }
        }
    }


    public static <T, M extends Info> void send(Info info, Function<M, Boolean> reply) {
        area.send(info, reply);
    }


    public static void makeConnection(String uniqueID, String host, int port) {

        area.getThreadFactory()
                .getThread()
                .makeConnection(uniqueID, host, port);
    }

    public static void registerRequestListeners(RequestPool.Listener listener) {
        area.getRequestPool().register(listener);
    }
}

