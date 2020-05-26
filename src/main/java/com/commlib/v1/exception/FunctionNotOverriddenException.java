package com.commlib.v1.exception;

public class FunctionNotOverriddenException extends RuntimeException {

    public FunctionNotOverriddenException(String function) {
        super(String.format("Function %s is supposed to be overridden.", function));
    }
}
