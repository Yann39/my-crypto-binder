package com.mycryptobinder.models;

/**
 * Created by Yann
 * Created on 07/09/2017
 */

public class KrakenAsset {

    private long id;
    private String assetName;
    private String altName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }
}