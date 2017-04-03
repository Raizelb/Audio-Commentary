package com.ucl_finalproject.audiocommentary.Retrofit;


import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Hoang on 01/12/2016.
 */

public class ServiceGenerator {
    //public static final String API_BASE_URL = "https://wamsbayclus001rest-hs.cloudapp.net/api/";

    private static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static Retrofit getClient(String API_BASE_URL) {
        // set desired logging level
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //add logging as last interceptor
        httpClient.addInterceptor(httpLoggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(API_BASE_URL)
                .build();
        return retrofit;
    }

    /*public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass);
    }*/

    /*public static <S> S createService(Class<S> serviceClass) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }*/
}
