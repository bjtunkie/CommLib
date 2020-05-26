package com.commlib.v1.comm;

public class DefaultCustomThread extends CustomThread {
    public DefaultCustomThread(int hash, CustomThreadFactory factory) {
        super(hash, factory);
    }

    @Override
    protected void work(BaseInfo info) {

    }
}
