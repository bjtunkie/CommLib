package com.commlib.v1.utils;

public class MarkableReference<T> {

    private T o;
    private boolean m;

    public MarkableReference() {
        this(false);
    }

    public MarkableReference(boolean mark) {
        this(null, mark);
    }

    public MarkableReference(T in, boolean mark) {
        o = in;
        m = mark;
    }

    public void setReference(T in, boolean mark) {
        o = in;
        m = mark;
    }

    public void setReference(T in) {
        setReference(in, !m);
    }

    public void setMark(boolean mark) {
        m = mark;
    }

    public T getReference() {
        return o;
    }

    public boolean isMarked() {
        return m;
    }
}
