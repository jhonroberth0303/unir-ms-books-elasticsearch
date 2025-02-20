package org.unir.books.exceptions;

public class ProcessorException extends Exception {

    public ProcessorException(String message) {
        super(message);
    }

    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
