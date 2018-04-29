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
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mycryptobinder.entities.Exchange;

import java.util.List;

@Dao
public interface ExchangeDao {

    @Query("select * from exchanges")
    LiveData<List<Exchange>> getAll();

    @Query("select * from exchanges")
    List<Exchange> exportAll();

    @Query("select * from exchanges where name = :name")
    Exchange getByName(String name);

    @Insert
    void insert(Exchange... exchanges);

    @Update
    void update(Exchange... exchanges);

    @Delete
    void delete(Exchange exchange);

    @Query("delete from exchanges;")
    void deleteAll();

}