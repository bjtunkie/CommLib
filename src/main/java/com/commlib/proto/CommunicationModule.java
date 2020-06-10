package com.commlib.proto;

import com.commlib.v1.comm.ThreadPool;
import com.commlib.v1.comm.WorkerThread;
import com.commlib.v1.comm.ConnectionPool;

public interface CommunicationModule {

    ConnectionPool getConnectionPool();

    RequestPool getRequestPool();

    ThreadPool<? extends WorkerThread> getThreadFactory();

}
