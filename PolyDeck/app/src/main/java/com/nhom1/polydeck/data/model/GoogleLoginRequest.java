package com.nhom1.polydeck.data.model;

import com.google.gson.annotations.SerializedName;

public class GoogleLoginRequest {
    @SerializedName("id_token")
    private String idToken;

    // Tương thích backend có thể dùng tên khóa khác
    @SerializedName("idToken")
    private String idTokenCamelCase;

    @SerializedName("token")
    private String tokenAlias;

    public GoogleLoginRequest(String idToken) {
        this.idToken = idToken;
        this.idTokenCamelCase = idToken;
        this.tokenAlias = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
        this.idTokenCamelCase = idToken;
        this.tokenAlias = idToken;
    }
}

