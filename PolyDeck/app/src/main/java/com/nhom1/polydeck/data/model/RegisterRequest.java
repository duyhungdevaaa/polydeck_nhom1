package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("ho_ten")
    private String hoTen;

    @SerializedName("email")
    private String email;

    @SerializedName("mat_khau")
    private String matKhau;

    public RegisterRequest(String hoTen, String email, String matKhau) {
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}

