package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mycryptobinder.entities.KrakenLedger;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface KrakenLedgerDao {

    @Query("select * from kraken_ledger")
    LiveData<List<KrakenLedger>> getAll();

    @Insert
    void insert(KrakenLedger... krakenLedgers);

    @Delete
    void delete(KrakenLedger krakenLedger);

}