package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Yann
 * Created on 05/09/2017
 */

public class KrakenAssetPairs {

    @SerializedName("error")
    public List<String> error;

    @SerializedName("result")
    public Map<String, KrakenAssetPairValue> result;

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public Map<String, KrakenAssetPairValue> getResult() {
        return result;
    }

    public void setResult(Map<String, KrakenAssetPairValue> result) {
        this.result = result;
    }

    /*private long id;
    private String assetPair;
    private String altName;
    private String base;
    private String quote;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssetPair() {
        return assetPair;
    }

    public void setAssetPair(String assetpair) {
        this.assetPair = assetpair;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altname) {
        this.altName = altname;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }*/
}