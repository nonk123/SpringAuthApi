package com.nonk.spring_auth_api.api;

import com.nonk.spring_auth_api.exceptions.ApiException;

public class ApiResponse {

    public static final ApiResponse SUCCESS = new ApiResponse(0, "Успешное выполнение запроса");

    private int errorCode;
    private String errorMessage;

    public ApiResponse(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ApiResponse(int errorCode, ApiException apiException) {
        this(errorCode, apiException.getMessage());
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
