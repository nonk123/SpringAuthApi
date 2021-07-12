package com.nonk.spring_auth_api.exceptions;

public class RestorationCodeMismatchException extends ApiException {

    public RestorationCodeMismatchException() {
        super("Введённый код восстановления не совпадает с действительным");
    }
}
