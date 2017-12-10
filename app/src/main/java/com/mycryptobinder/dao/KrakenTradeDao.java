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

    @Query("select distinct order_tx_id from kraken_trades")
    List<String> getTradeIds();

    @Insert
    void insert(KrakenTrade... krakenTrades);

    @Delete
    void delete(KrakenTrade krakenTrade);

    @Query("delete from kraken_trades")
    void deleteAll();

}