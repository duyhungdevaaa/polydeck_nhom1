package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class LichSuLamBai {

    @SerializedName("_id")
    private String id;

    @SerializedName("ma_nguoi_dung")
    private String maNguoiDung;

    @SerializedName("ma_chu_de")
    private String maChuDe;

    // FIX: Changed field name and added getter/setter to match server response and fragment usage
    @SerializedName("diem_so")
    private int diemSo;

    @SerializedName("so_cau_dung")
    private int soCauDung;

    @SerializedName("tong_so_cau")
    private int tongSoCau;

    @SerializedName("ngay_lam_bai")
    private Date ngayLamBai;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getMaChuDe() {
        return maChuDe;
    }

    public void setMaChuDe(String maChuDe) {
        this.maChuDe = maChuDe;
    }

    public int getDiemSo() {
        return diemSo;
    }

    public void setDiemSo(int diemSo) {
        this.diemSo = diemSo;
    }

    public int getSoCauDung() {
        return soCauDung;
    }

    public void setSoCauDung(int soCauDung) {
        this.soCauDung = soCauDung;
    }

    public int getTongSoCau() {
        return tongSoCau;
    }

    public void setTongSoCau(int tongSoCau) {
        this.tongSoCau = tongSoCau;
    }

    public Date getNgayLamBai() {
        return ngayLamBai;
    }

    public void setNgayLamBai(Date ngayLamBai) {
        this.ngayLamBai = ngayLamBai;
    }
}

