package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class ThongBao {

    @SerializedName("tieu_de")
    private String tieuDe;

    @SerializedName("noi_dung")
    private String noiDung;

    // Constructor, Getters, and Setters

    public ThongBao(String tieuDe, String noiDung) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}
