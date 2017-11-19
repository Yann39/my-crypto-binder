package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Yann
 * Created on 07/09/2017
 */

public class KrakenAssets {

    @SerializedName("error")
    public List<String> error;

    @SerializedName("result")
    public Map<String, KrakenAssetValue> result;

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public Map<String, KrakenAssetValue> getResult() {
        return result;
    }

    public void setResult(Map<String, KrakenAssetValue> result) {
        this.result = result;
    }
}