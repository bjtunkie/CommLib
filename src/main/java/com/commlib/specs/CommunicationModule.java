package com.commlib.specs;

import com.commlib.v1.comm.ThreadPool;
import com.commlib.v1.comm.WorkerThread;
import com.commlib.v1.network.ConnectionPool;
import com.commlib.v1.network.RequestPool;

public interface CommunicationModule {

    ConnectionPool getConnectionPool();

    RequestPool getRequestPool();

    ThreadPool<? extends WorkerThread> getThreadFactory();

}
