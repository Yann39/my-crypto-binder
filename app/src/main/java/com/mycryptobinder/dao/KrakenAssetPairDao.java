package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mycryptobinder.entities.KrakenAsset;
import com.mycryptobinder.entities.KrakenAssetPair;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface KrakenAssetPairDao {

    @Query("select * from kraken_asset_pairs")
    LiveData<List<KrakenAssetPair>> getAll();

    @Insert
    void insert(KrakenAssetPair... krakenAssetPairs);

    @Delete
    void delete(KrakenAssetPair krakenAssetPair);

}