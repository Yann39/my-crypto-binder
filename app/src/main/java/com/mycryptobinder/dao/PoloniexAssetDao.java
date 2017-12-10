package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mycryptobinder.entities.PoloniexAsset;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface PoloniexAssetDao {

    @Query("select * from poloniex_assets")
    LiveData<List<PoloniexAsset>> getAll();

    @Query("select distinct asset_code from poloniex_assets")
    List<String> getCodes();

    @Insert
    void insert(PoloniexAsset... poloniexAssets);

    @Delete
    void delete(PoloniexAsset poloniexAsset);

    @Query("delete from poloniex_assets")
    void deleteAll();

}