package com.mycryptobinder.models;

/**
 * Created by Yann
 * Created on 09/09/2017
 */

public class PoloniexAsset {

    private long id;
    private String assetCode;
    private String assetName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
}