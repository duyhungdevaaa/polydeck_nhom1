package com.nhom1.polydeck.data.model;

public class ChangePasswordRequest {
    public String email;
    public String mat_khau_cu;
    public String mat_khau_moi;

    public ChangePasswordRequest(String email, String oldPassword, String newPassword) {
        this.email = email;
        this.mat_khau_cu = oldPassword;
        this.mat_khau_moi = newPassword;
    }
}


