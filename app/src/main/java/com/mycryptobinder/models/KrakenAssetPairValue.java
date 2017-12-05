package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Yann
 * Created on 18/11/2017
 */

public class KrakenAssetPairValue {

    @SerializedName("altname")
    public String altName;
    public String base;
    public String quote;

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
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
    }
}
