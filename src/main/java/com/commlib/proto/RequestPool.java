package com.commlib.proto;

import java.util.function.Function;

public interface RequestPool {
    int TIME_TO_LIVE = 5000; // In milliseconds

    <T extends Info> void insert(Info hash, Function<T, Boolean> reply);

    void execute(Info reply);

    void register(Listener listener);

    interface Listener {
        void onResponse(Info o);
    }
}
