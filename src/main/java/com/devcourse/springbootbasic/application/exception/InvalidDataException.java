package com.devcourse.springbootbasic.application.exception;

public class InvalidDataException extends RuntimeException {

    public InvalidDataException() {
        super();
    }

    public InvalidDataException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidDataException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
