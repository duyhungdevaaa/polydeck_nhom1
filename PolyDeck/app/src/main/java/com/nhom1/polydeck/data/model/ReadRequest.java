package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class ReadRequest {
    @SerializedName("userId")
    private String userId;

    public ReadRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }


