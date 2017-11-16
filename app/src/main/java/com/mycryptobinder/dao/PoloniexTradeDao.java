package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mycryptobinder.entities.PoloniexTrade;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface PoloniexTradeDao {

    @Query("select * from poloniex_trades")
    LiveData<List<PoloniexTrade>> getAll();

    @Insert
    void insert(PoloniexTrade... poloniexTrades);

    @Delete
    void delete(PoloniexTrade poloniexTrade);

}