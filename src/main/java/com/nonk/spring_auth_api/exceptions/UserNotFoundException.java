package com.nonk.spring_auth_api.exceptions;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException(String email) {
        super(String.format("Пользователь с почтой %s не найден", email));
    }
}
