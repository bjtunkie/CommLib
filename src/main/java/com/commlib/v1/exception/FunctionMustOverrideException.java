package com.commlib.v1.exception;

public class FunctionMustOverrideException extends RuntimeException {
    static final long serialVersionUID = -8350980980790306044L;

    public FunctionMustOverrideException() {
        this("Function is designed to be Overridden!");
    }

    public FunctionMustOverrideException(String message) {
        super(message);
    }


}
