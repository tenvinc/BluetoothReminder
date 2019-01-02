package com.project.tenvinc.bluetoothreminder.exceptions;

public class DuplicateNameException extends Exception {
    private String message;
    private Throwable cause;

    public DuplicateNameException() {
        super();
    }

    public DuplicateNameException(String message, Throwable cause) {
        super(message, cause);

        this.cause = cause;
        this.message = message;
    }

    public DuplicateNameException(String message) {
        super(message);
        this.message = message;
    }
}
