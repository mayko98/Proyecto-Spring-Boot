package com.example.App.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(HttpStatus.CONFLICT) // Devuelve 409 Conflict al cliente
public class DuplicateResourceException extends  RuntimeException {
    public DuplicateResourceException(String s) {
        super(s);
    }
}
