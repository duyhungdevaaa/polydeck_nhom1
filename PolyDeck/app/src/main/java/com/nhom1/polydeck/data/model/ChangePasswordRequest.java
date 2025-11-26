package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("mat_khau_cu")
    private String oldPassword;

    @SerializedName("mat_khau_moi")
    private String newPassword;

    public ChangePasswordRequest(String email, String oldPassword, String newPassword) {
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
