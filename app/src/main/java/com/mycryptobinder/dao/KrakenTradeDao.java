package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mycryptobinder.entities.KrakenTrade;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface KrakenTradeDao {

    @Query("select * from kraken_trades")
    LiveData<List<KrakenTrade>> getAll();

    @Insert
    void insert(KrakenTrade... krakenTrades);

    @Delete
    void delete(KrakenTrade krakenTrade);

}