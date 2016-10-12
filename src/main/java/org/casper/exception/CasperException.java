package org.casper.exception;

public class CasperException extends Exception {
    public CasperException(String message) {
        super(message);
    }

    public CasperException(Throwable throwable) {
        super(throwable);
    }

    public CasperException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
