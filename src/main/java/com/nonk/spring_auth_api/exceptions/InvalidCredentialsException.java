package com.nonk.spring_auth_api.exceptions;

public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
