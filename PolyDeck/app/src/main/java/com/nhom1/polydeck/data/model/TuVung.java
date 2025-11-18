package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class TuVung {
    @SerializedName("_id")
    private String id;

    @SerializedName("tu_vung")
    private String tuVung;

    @SerializedName("phien_am")
    private String phienAm;

    @SerializedName("nghia")
    private String nghia;

    @SerializedName("vi_du")
    private String viDu;

    @SerializedName("bo_tu_id")
    private String boTuId;

    public TuVung() {}

    public TuVung(String id, String tuVung, String phienAm, String nghia, String viDu, String boTuId) {
        this.id = id;
        this.tuVung = tuVung;
        this.phienAm = phienAm;
        this.nghia = nghia;
        this.viDu = viDu;
        this.boTuId = boTuId;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTuVung() { return tuVung; }
    public void setTuVung(String tuVung) { this.tuVung = tuVung; }

    public String getPhienAm() { return phienAm; }
    public void setPhienAm(String phienAm) { this.phienAm = phienAm; }

    public String getNghia() { return nghia; }
    public void setNghia(String nghia) { this.nghia = nghia; }

    public String getViDu() { return viDu; }
    public void setViDu(String viDu) { this.viDu = viDu; }

    public String getBoTuId() { return boTuId; }
    public void setBoTuId(String boTuId) { this.boTuId = boTuId; }
}