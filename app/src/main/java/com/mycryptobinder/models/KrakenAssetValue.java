package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yann
 * Created on 18/11/2017
 */

public class KrakenAssetValue {

    @SerializedName("altname")
    public String altName;

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }
}
