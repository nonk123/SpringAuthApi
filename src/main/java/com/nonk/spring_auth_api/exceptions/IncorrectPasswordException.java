package com.nonk.spring_auth_api.exceptions;

public class IncorrectPasswordException extends ApiException {

    public IncorrectPasswordException() {
        super("Введён неверный пароль");
    }
}
