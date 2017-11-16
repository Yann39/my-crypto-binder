package com.mycryptobinder.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Yann
 * Created on 05/09/2017
 */

@Entity(tableName = "kraken_asset_pairs")
public class KrakenAssetPair {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "asset_pair")
    private String assetPair;

    @ColumnInfo(name = "alt_name")
    private String altName;

    @ColumnInfo(name = "base")
    private String base;

    @ColumnInfo(name = "quote")
    private String quote;

    public KrakenAssetPair(String assetPair, String altName, String base, String quote) {
        this.assetPair = assetPair;
        this.altName = altName;
        this.base = base;
        this.quote = quote;
    }

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