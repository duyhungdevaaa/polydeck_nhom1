package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Question {

    @SerializedName("questionText")
    private String questionText;

    @SerializedName("answers")
    private List<Answer> answers;

    public Question(String questionText, List<Answer> answers) {
        this.questionText = questionText;
        this.answers = answers;
    }

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
