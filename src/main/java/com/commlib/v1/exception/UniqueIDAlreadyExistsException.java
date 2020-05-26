package com.commlib.v1.exception;

public class UniqueIDAlreadyExistsException extends RuntimeException{
    public UniqueIDAlreadyExistsException() {
        super("Assigned unique ID already exists.");
    }
}
