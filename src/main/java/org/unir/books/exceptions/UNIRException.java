package org.unir.books.exceptions;

public class UNIRException extends RuntimeException {

    public UNIRException(String message) {
        super(message);
    }

    public UNIRException(String message, Throwable cause) {
        super(message, cause);
    }
}
