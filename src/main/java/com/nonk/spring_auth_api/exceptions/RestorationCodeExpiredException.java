package com.nonk.spring_auth_api.exceptions;

public class RestorationCodeExpiredException extends ApiException {

    public RestorationCodeExpiredException(String email) {
        super(String
                .format("Код восстановления пароля для пользователя %s просрочен или не существует",
                        email));
    }
}
