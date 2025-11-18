package com.nhom1.polydeck.data.model;
import com.google.gson.annotations.SerializedName;

public class AdminStats {
    @SerializedName("tong_nguoi_dung")
    private int tongNguoiDung;

    @SerializedName("tong_bo_tu")
    private int tongBoTu;

    @SerializedName("nguoi_hoat_dong")
    private int nguoiHoatDong;

    @SerializedName("tong_tu_vung")
    private int tongTuVung;

    @SerializedName("ty_le_nguoi_dung")
    private String tyLeNguoiDung;

    @SerializedName("ty_le_bo_tu")
    private String tyLeBoTu;

    @SerializedName("ty_le_hoat_dong")
    private String tyLeHoatDong;
    @SerializedName("ty_le_tu_vung")
    private String tyLeTuVung;
    public AdminStats() {}

    public int getTongNguoiDung() { return tongNguoiDung; }
    public void setTongNguoiDung(int tongNguoiDung) { this.tongNguoiDung = tongNguoiDung; }

    public int getTongBoTu() { return tongBoTu; }
    public void setTongBoTu(int tongBoTu) { this.tongBoTu = tongBoTu; }

    public int getNguoiHoatDong() { return nguoiHoatDong; }
    public void setNguoiHoatDong(int nguoiHoatDong) { this.nguoiHoatDong = nguoiHoatDong; }

    public int getTongTuVung() { return tongTuVung; }
    public void setTongTuVung(int tongTuVung) { this.tongTuVung = tongTuVung; }

    public String getTyLeNguoiDung() { return tyLeNguoiDung; }
    public void setTyLeNguoiDung(String tyLeNguoiDung) { this.tyLeNguoiDung = tyLeNguoiDung; }

    public String getTyLeBoTu() { return tyLeBoTu; }
    public void setTyLeBoTu(String tyLeBoTu) { this.tyLeBoTu = tyLeBoTu; }

    public String getTyLeHoatDong() { return tyLeHoatDong; }
    public void setTyLeHoatDong(String tyLeHoatDong) { this.tyLeHoatDong = tyLeHoatDong; }

    public String getTyLeTuVung() { return tyLeTuVung; }
    public void setTyLeTuVung(String tyLeTuVung) { this.tyLeTuVung = tyLeTuVung; }
}