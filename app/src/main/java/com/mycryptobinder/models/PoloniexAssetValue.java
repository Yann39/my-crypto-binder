package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yann
 * Created on 09/09/2017
 */

public class PoloniexAssetValue {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}