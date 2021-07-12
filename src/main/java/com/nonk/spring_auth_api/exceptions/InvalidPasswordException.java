package com.nonk.spring_auth_api.exceptions;

public class InvalidPasswordException extends ApiException {

    public InvalidPasswordException() {
        super("Указан недопустимый пароль");
    }
}
