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

package com.mycryptobinder.dao.poloniex;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mycryptobinder.entities.poloniex.PoloniexTrade;

import java.util.List;

@Dao
public interface PoloniexTradeDao {

    @Query("select * from poloniex_trades")
    LiveData<List<PoloniexTrade>> getAll();

    @Query("select distinct global_trade_id from poloniex_trades")
    List<Long> getTradeIds();

    @Insert
    void insert(PoloniexTrade... poloniexTrades);

    @Delete
    void delete(PoloniexTrade poloniexTrade);

    @Query("delete from poloniex_trades")
    void deleteAll();

}