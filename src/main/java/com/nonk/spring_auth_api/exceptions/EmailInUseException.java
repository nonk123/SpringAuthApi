package com.nonk.spring_auth_api.exceptions;

public class EmailInUseException extends ApiException {

    public EmailInUseException(String email) {
        super(String.format("Пользователь с адресом %s уже зарегистрирован", email));
    }
}
