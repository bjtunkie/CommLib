package com.commlib.v1.comm;

public class DefaultCustomThreadFactory extends CustomThreadFactory<DefaultCustomThread> {

    public DefaultCustomThreadFactory() {
    }

    @Override
    public DefaultCustomThread createThread() {
        return new DefaultCustomThread(super.nextUniqueID(), this);
    }



}
