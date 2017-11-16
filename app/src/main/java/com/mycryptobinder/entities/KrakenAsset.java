package com.mycryptobinder.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Entity(tableName = "kraken_assets")
public class KrakenAsset {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "asset_name")
    private String assetName;

    @ColumnInfo(name = "alt_name")
    private String altName;

    public KrakenAsset(String assetName, String altName) {
        this.assetName = assetName;
        this.altName = altName;
    }

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