package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class GoogleLoginRequest {
    @SerializedName("id_token")
    private String idToken;

    public GoogleLoginRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}

