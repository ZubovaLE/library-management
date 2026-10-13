package com.zubova.library.exception;

public class InvalidLoanRequestException extends RuntimeException {

    public InvalidLoanRequestException(String message) {
        super(message);
    }

}