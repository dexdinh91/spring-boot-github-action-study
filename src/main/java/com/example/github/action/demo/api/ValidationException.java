package com.example.github.action.demo.api;

import java.util.List;
import java.util.Map;

public class ValidationException extends RuntimeException {
    private final Object errors;

    public ValidationException(String message, Object errors) {
        super(message);
        this.errors = errors;
    }

    public Object getErrors() { return errors; }
}
