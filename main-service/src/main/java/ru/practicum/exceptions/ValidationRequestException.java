package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ValidationRequestException extends RuntimeException {
    public ValidationRequestException(String message) {
        super(message);
    }
}