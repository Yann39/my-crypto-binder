package com.mycryptobinder.service;

import com.mycryptobinder.models.Price;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

public interface PoloniexService {

    @GET("price?tsyms=EUR,USD")
    Call<Price> getCurrentPrice(@Query("fsym") String fsym);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/data/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}