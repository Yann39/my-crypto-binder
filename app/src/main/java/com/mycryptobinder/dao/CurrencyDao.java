package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.models.HoldingData;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface CurrencyDao {

    @Query("select * from currencies")
    LiveData<List<Currency>> getAll();

    @Query("select * from currencies where iso_code = :isoCode")
    Currency getByIsoCode(String isoCode);

    @Query("select c.* " +
            "from currencies c " +
            "inner join transactions t1 on c.iso_code = t1.currency1_iso_code " +
            "inner join transactions t2 on c.iso_code = t2.currency2_iso_code " +
            "where iso_code = :isoCode")
    LiveData<List<Currency>> getUsed();

    @Query("select pa.asset_code, pa.asset_name, null " +
            "from poloniex_assets pa " +
            "left join currencies c on pa.asset_code = c.iso_code " +
            "where pa.asset_code is null")
    LiveData<List<Currency>> getFromPoloniex();

    @Query("select ka.alt_name, null, null " +
            "from kraken_assets ka " +
            "left join currencies c on ka.alt_name = c.iso_code " +
            "where ka.alt_name is null")
    LiveData<List<Currency>> getFromKraken();

    @Insert
    void insert(Currency... currencies);

    @Update
    void update(Currency... currencies);

    @Delete
    void delete(Currency currency);

}