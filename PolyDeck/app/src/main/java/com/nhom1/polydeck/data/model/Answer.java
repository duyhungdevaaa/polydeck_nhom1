package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class Answer {

    @SerializedName("answerText")
    private String answerText;

    @SerializedName("isCorrect")
    private boolean isCorrect;

    public Answer(String answerText, boolean isCorrect) {
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
