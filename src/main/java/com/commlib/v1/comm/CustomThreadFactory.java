package com.commlib.v1.comm;

import com.commlib.v1.exception.FunctionMustOverrideException;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * It's a pool
 */
public class CustomThreadFactory<T extends CustomThread> {


    private final Deque<T> stack = new LinkedList<>();
    private final AtomicInteger uniqueIDGen = new AtomicInteger(0);

    public CustomThreadFactory() {

    }

    public T createThread() {
        throw new FunctionMustOverrideException(CustomThreadFactory.class.getName() + ".createThread()");
    }

    public final int nextUniqueID() {
        return uniqueIDGen.incrementAndGet();
    }


    public final T getThread() {
        if (stack.isEmpty()) {
            return createThread();
        } else {
            synchronized (stack) {
                T thread = stack.pop();
                return thread;
            }
        }
    }

    public final void putBack(final T thread) {
        synchronized (stack) {
            stack.push(thread);
        }
    }
}
