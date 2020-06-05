package com.commlib.v1.comm;

import com.commlib.v1.network.ConnectionPool;
import com.commlib.v1.network.RequestPool;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * It's a pool
 */
public class ThreadPool<T extends WorkerThread> {


    private final Deque<T> stack = new LinkedList<>();
    private final Class<T> threadClass;

    private final AtomicInteger count;
    private final RequestPool requestPool;
    private final ConnectionPool connectionPool;

    public ThreadPool(ConnectionPool c, RequestPool r, Class<T> t) {
        requestPool = r;
        threadClass = t;
        connectionPool = c;
        count = new AtomicInteger(0);
    }

    public T createThread() {

        try {
            synchronized (count) {
                int hash = count.incrementAndGet();
                T thread = threadClass.getConstructor(int.class, ThreadPool.class).newInstance(hash, this);
                thread.start();
                return thread;
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final RequestPool getRequestPool() {
        return requestPool;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public final synchronized T getThread() {
        if (stack.isEmpty()) {
            T t;
            while ((t = createThread()) != null) {
                return t;
            }
        } else {
            return stack.pop();

        }

        return null;
    }

    public final void putBack(final WorkerThread thread) {
        synchronized (stack) {
            stack.push((T) thread);
        }
    }
}
