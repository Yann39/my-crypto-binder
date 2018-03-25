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

import com.mycryptobinder.entities.poloniex.PoloniexWithdrawal;

import java.util.List;

@Dao
public interface PoloniexWithdrawalDao {

    @Query("select * from poloniex_withdrawals")
    LiveData<List<PoloniexWithdrawal>> getAll();

    @Query("select distinct withdrawal_number from poloniex_withdrawals")
    List<Long> getWithdrawalNumbers();

    @Insert
    void insert(PoloniexWithdrawal... poloniexWithdrawals);

    @Delete
    void delete(PoloniexWithdrawal poloniexWithdrawal);

    @Query("delete from poloniex_withdrawals")
    void deleteAll();

}