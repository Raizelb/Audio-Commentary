package com.ucl_finalproject.audiocommentary.Retrofit;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.Body;

/**
 * Created by Hoang on 04/12/2016.
 */

public class Token {
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private String expiresIn;
    @SerializedName("scope")
    private String scope;

    public Token(String tokenType, String accessToken, String expiresIn, String scope) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }
}
