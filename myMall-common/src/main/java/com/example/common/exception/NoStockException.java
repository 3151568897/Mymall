package com.example.common.exception;

public class NoStockException extends RuntimeException {

    public NoStockException(String msg) {
        super(msg);
    }
}
