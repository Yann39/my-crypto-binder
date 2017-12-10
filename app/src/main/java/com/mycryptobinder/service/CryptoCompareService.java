package com.mycryptobinder.service;

import com.mycryptobinder.models.Price;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

public interface CryptoCompareService {

    @GET("price?tsyms=EUR,USD")
    Call<Price> getCurrentPrice(@Query("fsym") String fsym);

    @GET("pricemulti?tsyms=EUR,USD")
    Call<Map<String, Price>> getCurrentPrices(@Query("fsyms") String fsyms);

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/data/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();
}