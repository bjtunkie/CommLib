package com.commlib.v1.log;

public class LogFactory {

    public static Log create(Class<?> clazz) {
        return new Log(clazz.getCanonicalName());
    }
}
