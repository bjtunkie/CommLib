package com.commlib.v1.log;

public class Log {

    private final String tag;
    private final StringBuilder stb;

    Log(String tag) {
        this.tag = tag;
        this.stb = new StringBuilder();
    }

    public void info(String format, Object... arr) {
        this.d(String.format(format, arr));
    }

    public void debug(String format, Object... arr) {
        this.d(String.format(format, arr));
    }

    public void d(String info) {
        synchronized (stb) {
            stb.setLength(0);
            stb.append(tag)
                    .append(": ")
                    .append(info);

            System.out.println(stb.toString());
        }
    }
}
