package com.example.common.exception;

public class PhoneExistException  extends RuntimeException {
    public PhoneExistException(String msg) {
        super(msg);
    }
}
