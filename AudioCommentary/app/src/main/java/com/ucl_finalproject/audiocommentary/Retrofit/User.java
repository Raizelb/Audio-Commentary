package com.ucl_finalproject.audiocommentary.Retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hoang on 03/01/2017.
 */

public class User {
    @SerializedName("error")
    private boolean error;
    @SerializedName("uid")
    private String uid;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("created_at")
    private String created_at;

    public User(String error, String uid, String name, String email, String created_at) {
        this.error = Boolean.parseBoolean(error);
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.created_at = created_at;
    }

    public boolean getError() {
        return error;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedAt() {
        return created_at;
    }
}
