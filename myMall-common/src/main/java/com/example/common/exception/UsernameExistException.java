package com.example.common.exception;

public class UsernameExistException  extends RuntimeException {
    public UsernameExistException(String s) {
        super(s);
    }
}
