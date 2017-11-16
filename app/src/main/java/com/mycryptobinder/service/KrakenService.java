package com.mycryptobinder.service;

import com.mycryptobinder.models.Price;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

public interface KrakenService {

    @GET("AssetPairs")
    Call<Price> getAssetPairs();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.kraken.com/0/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}