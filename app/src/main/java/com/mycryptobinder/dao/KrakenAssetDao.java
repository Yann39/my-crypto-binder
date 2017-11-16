package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mycryptobinder.entities.KrakenAsset;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface KrakenAssetDao {

    @Query("select * from kraken_assets")
    LiveData<List<KrakenAsset>> getAll();

    @Insert
    void insert(KrakenAsset... krakenAssets);

    @Delete
    void delete(KrakenAsset krakenAsset);

}