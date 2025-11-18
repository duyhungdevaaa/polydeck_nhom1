package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("_id")
    private String id;

    @SerializedName("ho_ten")
    private String hoTen;

    @SerializedName("email")
    private String email;

    @SerializedName("level")
    private int level;

    @SerializedName("xp")
    private int xp;

    @SerializedName("ngay_tham_gia")
    private String ngayThamGia;

    @SerializedName("trang_thai")
    private String trangThai;

    public User() {}

    public User(String id, String hoTen, String email, int level, int xp, String ngayThamGia, String trangThai) {
        this.id = id;
        this.hoTen = hoTen;
        this.email = email;
        this.level = level;
        this.xp = xp;
        this.ngayThamGia = ngayThamGia;
        this.trangThai = trangThai;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public String getNgayThamGia() { return ngayThamGia; }
    public void setNgayThamGia(String ngayThamGia) { this.ngayThamGia = ngayThamGia; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getInitials() {
        if (hoTen == null || hoTen.isEmpty()) return "?";
        String[] words = hoTen.trim().split("\\s+");
        if (words.length >= 2) {
            return (words[0].charAt(0) + "" + words[words.length - 1].charAt(0)).toUpperCase();
        }
        return hoTen.substring(0, Math.min(2, hoTen.length())).toUpperCase();
    }

    public boolean isActive() {
        return "Hoạt động".equals(trangThai);
    }
}