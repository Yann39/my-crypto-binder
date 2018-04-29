/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.services;

import com.mycryptobinder.models.bittrex.BittrexAssets;
import com.mycryptobinder.models.bittrex.BittrexDeposits;
import com.mycryptobinder.models.bittrex.BittrexTrades;
import com.mycryptobinder.models.bittrex.BittrexWithdrawals;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.QueryMap;

public interface BittrexService {

    @GET("public/getcurrencies")
    Call<BittrexAssets> getAssets();

    @GET("account/getorderhistory")
    Call<BittrexTrades> getTradeHistory(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @GET("account/getdeposithistory")
    Call<BittrexDeposits> getDeposits(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @GET("account/getwithdrawalhistory")
    Call<BittrexWithdrawals> getWithdrawals(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://bittrex.com/api/v1.1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();

}