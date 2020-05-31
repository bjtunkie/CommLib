package com.coipmmlib.v1.comm;

import com.commlib.v1.comm.CustomThreadFactory;
import com.commlib.v1.comm.DefaultCustomThread;
import com.commlib.v1.network.RequestPool;

public class DefaultCustomThreadFactory extends CustomThreadFactory<DefaultCustomThread> {

    public DefaultCustomThreadFactory() {
        super(RequestPool.defaultRequestPool);
    }

    @Override
    public DefaultCustomThread createThread() {
        return new DefaultCustomThread(super.nextUniqueID(), this);
    }


}
