package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Quiz {

    @SerializedName("ma_chu_de")
    private String maChuDe;

    @SerializedName("questions")
    private List<Question> questions;

    public Quiz(String maChuDe, List<Question> questions) {
        this.maChuDe = maChuDe;
        this.questions = questions;
    }

    // Getters and Setters
    public String getMaChuDe() {
        return maChuDe;
    }

    public void setMaChuDe(String maChuDe) {
        this.maChuDe = maChuDe;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
