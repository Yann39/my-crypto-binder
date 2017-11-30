package com.mycryptobinder.service;

import com.mycryptobinder.models.KrakenAssetPairs;
import com.mycryptobinder.models.KrakenAssets;
import com.mycryptobinder.models.KrakenTrades;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

public interface KrakenService {

    @GET("0/public/AssetPairs")
    Call<KrakenAssetPairs> getAssetPairs();

    @GET("0/public/Assets")
    Call<KrakenAssets> getAssets();

    @FormUrlEncoded
    @POST("0/private/TradesHistory")
    Call<KrakenTrades> getTradeHistory(@HeaderMap Map<String, String> headers,
                                       @FieldMap Map<String, String> fields);

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.kraken.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();

}