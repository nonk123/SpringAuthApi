package com.nonk.spring_auth_api.api;

public class SubmitRestorationCodeRequest {

    private String email;
    private String restorationCode;
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRestorationCode() {
        return restorationCode;
    }

    public void setRestorationCode(String restorationCode) {
        this.restorationCode = restorationCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
