package com.mycryptobinder.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Yann
 * Created on 09/09/2017
 */

@Entity(tableName = "poloniex_assets")
public class PoloniexAsset {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "asset_code")
    private String assetCode;

    @ColumnInfo(name = "asset_name")
    private String assetName;

    public PoloniexAsset(String assetCode, String assetName) {
        this.assetCode = assetCode;
        this.assetName = assetName;
    }

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