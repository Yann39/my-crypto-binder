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

import com.mycryptobinder.models.poloniex.PoloniexAssetValue;
import com.mycryptobinder.models.poloniex.PoloniexDepositsWithdrawals;
import com.mycryptobinder.models.poloniex.PoloniexError;
import com.mycryptobinder.models.poloniex.PoloniexTradeValue;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface PoloniexService {

    @GET("public?command=returnCurrencies")
    Call<Map<String, PoloniexAssetValue>> getAssets();

    @GET("public?command=returnCurrencies")
    Call<PoloniexError> getAssetsError();

    @FormUrlEncoded
    @POST("tradingApi")
    Call<Map<String, List<PoloniexTradeValue>>> getTradeHistory(@HeaderMap Map<String, String> headers,
                                                                @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("tradingApi")
    Call<PoloniexError> getTradeHistoryError(@HeaderMap Map<String, String> headers,
                                             @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("tradingApi")
    Call<PoloniexDepositsWithdrawals> getDepositsWithdrawals(@HeaderMap Map<String, String> headers,
                                                             @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("tradingApi")
    Call<PoloniexError> getDepositsWithdrawalsError(@HeaderMap Map<String, String> headers,
                                                    @FieldMap Map<String, String> params);


    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging).addInterceptor(chain -> {
        Request original = chain.request();

        // sort request body parameters alphabetically so they are in the same order as the one used to generate the HMAC
        RequestBody body = original.body();
        FormBody formBody = (FormBody) body;
        Map<String, String> unsortedParams = new HashMap<>();
        if (formBody != null) {
            for (int i = 0; i < formBody.size(); i++) {
                unsortedParams.put(formBody.name(i), formBody.value(i));
            }
        }
        Map<String, String> sortedParams = new TreeMap<>(unsortedParams);

        // convert map to URL string
        StringBuilder sb = new StringBuilder();
        for (HashMap.Entry<String, String> e : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
        }
        String newPostBody = sb.toString();

        // set as new POST body
        RequestBody requestBody = null;
        if (body != null) {
            requestBody = RequestBody.create(body.contentType(), newPostBody);
        }

        // replace body only for POST and PUT requests, GET does not have body
        Request request;
        if (requestBody != null) {
            switch (original.method()) {
                case "POST":
                    request = original.newBuilder().method(original.method(), original.body()).post(requestBody).build();
                    break;
                case "PUT":
                    request = original.newBuilder().method(original.method(), original.body()).put(requestBody).build();
                    break;
                default:
                    request = original.newBuilder().method(original.method(), original.body()).build();
                    break;
            }
        } else {
            request = original.newBuilder().method(original.method(), original.body()).build();
        }

        return chain.proceed(request);
    });

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://poloniex.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();

}