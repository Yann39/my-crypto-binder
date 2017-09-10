package com.mycryptobinder.models;

/**
 * Created by Yann
 * Created on 05/09/2017
 */

public class KrakenAssetPair {

    private long id;
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
    }
}