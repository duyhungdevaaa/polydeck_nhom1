package com.nhom1.polydeck.data.model;
import com.google.gson.annotations.SerializedName;

public class BoTu {
    @SerializedName("_id")
    private String id;

    @SerializedName("ten_bo_tu")
    private String tenBoTu;

    @SerializedName("mau_sac")
    private String mauSac;

    @SerializedName("so_tu")
    private int soTu;

    @SerializedName("so_nguoi_dung")
    private int soNguoiDung;

    @SerializedName("ngay_tao")
    private String ngayTao;

    @SerializedName("trang_thai")
    private String trangThai;

    public BoTu() {}

    public BoTu(String id, String tenBoTu, String mauSac, int soTu, int soNguoiDung, String ngayTao, String trangThai) {
        this.id = id;
        this.tenBoTu = tenBoTu;
        this.mauSac = mauSac;
        this.soTu = soTu;
        this.soNguoiDung = soNguoiDung;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenBoTu() { return tenBoTu; }
    public void setTenBoTu(String tenBoTu) { this.tenBoTu = tenBoTu; }

    public String getMauSac() { return mauSac; }
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }

    public int getSoTu() { return soTu; }
    public void setSoTu(int soTu) { this.soTu = soTu; }

    public int getSoNguoiDung() { return soNguoiDung; }
    public void setSoNguoiDung(int soNguoiDung) { this.soNguoiDung = soNguoiDung; }

    public String getNgayTao() { return ngayTao; }
    public void setNgayTao(String ngayTao) { this.ngayTao = ngayTao; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}

