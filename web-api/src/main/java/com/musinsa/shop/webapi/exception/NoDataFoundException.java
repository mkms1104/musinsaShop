package com.musinsa.shop.webapi.exception;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String id) {
        super(id);
    }

    public NoDataFoundException(Long id) {
        super(id.toString());
    }
}
