package com.mycryptobinder.service;

import com.mycryptobinder.models.PoloniexAssetValue;
import com.mycryptobinder.models.PoloniexTradeValue;
import com.mycryptobinder.models.Price;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

public interface PoloniexService {

    @GET("public?command=returnCurrencies")
    Call<Map<String, PoloniexAssetValue>> getAssets();

    @FormUrlEncoded
    @POST("tradingApi")
    Call<Map<String, List<PoloniexTradeValue>>> getTradeHistory(@HeaderMap Map<String, String> headers,
                                                                @FieldMap Map<String, String> params);

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://poloniex.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();

}