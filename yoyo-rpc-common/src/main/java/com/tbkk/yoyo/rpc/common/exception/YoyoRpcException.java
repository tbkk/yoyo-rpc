package com.tbkk.yoyo.rpc.common.exception;


public class YoyoRpcException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public YoyoRpcException(String msg) {
        super(msg);
    }

    public YoyoRpcException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public YoyoRpcException(Throwable cause) {
        super(cause);
    }

}