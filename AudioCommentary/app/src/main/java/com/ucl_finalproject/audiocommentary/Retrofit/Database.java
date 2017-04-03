package com.ucl_finalproject.audiocommentary.Retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Hoang on 02/01/2017.
 */

public interface Database {

    @FormUrlEncoded
    @POST("/WebScripts/login.php")
    Call<ResponseBody> login(
            @Field("email") String inputEmail,
            @Field("password") String inputPassword
    );

    @FormUrlEncoded
    @POST("/WebScripts/register.php")
    Call<ResponseBody> register(
            @Field("email") String inputEmail,
            @Field("password") String inputPassword,
            @Field("name") String... name
    );

    @FormUrlEncoded
    @POST("/WebScripts/user_search.php")
    Call<ResponseBody> user_search(
            @Field("name") String inputName
    );

    @FormUrlEncoded
    @POST("/WebScripts/register_commentator.php")
    Call<ResponseBody> register_commentator(
            @Field("email") String inputEmail,
            @Field("team") String inputTeam,
            @Field("description") String inputDescription
    );

    @FormUrlEncoded
    @POST("/WebScripts/load_comments.php")
    Call<ResponseBody> load_comments(
            @Field("user_id") String userID
    );

    @FormUrlEncoded
    @POST("/WebScripts/submit_comment.php")
    Call<ResponseBody> submit_comment(
            @Field("user_id") String userID,
            @Field("comment_content") String commentContent,
            @Field("comment_rating") String commentRating
    );
}
