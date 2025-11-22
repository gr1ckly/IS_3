package org.example.lab1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BadDataException extends RuntimeException {
    public BadDataException(String message) {
        super(message);
    }
}
