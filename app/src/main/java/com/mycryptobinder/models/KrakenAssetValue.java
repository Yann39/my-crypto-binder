package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yann
 * Created on 18/11/2017
 */

public class KrakenAssetValue {

    @SerializedName("altname")
    public String altname;

    public String getAltname() {
        return altname;
    }

    public void setAltname(String altname) {
        this.altname = altname;
    }
}
