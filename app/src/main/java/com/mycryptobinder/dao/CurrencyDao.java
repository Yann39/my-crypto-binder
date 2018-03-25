/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListProvider;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mycryptobinder.entities.Currency;

import java.util.List;

@Dao
public interface CurrencyDao {

    @Query("select * from currencies")
    LiveData<List<Currency>> getAll();

    @Query("select * from currencies")
    LivePagedListProvider<Integer, Currency> getAllPaged();

    @Query("select pa.asset_code as iso_code, pa.asset_name as name, null as symbol " +
            "from poloniex_assets pa " +
            "left join currencies c on c.iso_code = pa.asset_code " +
            "where c.iso_code is null")
    List<Currency> getFromPoloniex();

    @Query("select ka.alt_name as iso_code, null as name, null as symbol " +
            "from kraken_assets ka " +
            "left join currencies c on c.iso_code = ka.alt_name " +
            "where c.iso_code is null")
    List<Currency> getFromKraken();

    @Insert
    void insert(Currency... currencies);

    @Update
    void update(Currency... currencies);

    @Delete
    void delete(Currency currency);

    @Query("delete from currencies;")
    void deleteAll();

}