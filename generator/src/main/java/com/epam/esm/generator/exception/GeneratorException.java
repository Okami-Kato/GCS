package com.epam.esm.generator.exception;

public class GeneratorException extends RuntimeException{
    public GeneratorException() {
    }

    public GeneratorException(String message) {
        super(message);
    }

    public GeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
