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

package com.mycryptobinder.dao.kraken;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mycryptobinder.entities.kraken.KrakenAsset;

import java.util.List;

@Dao
public interface KrakenAssetDao {

    @Query("select * from kraken_assets")
    LiveData<List<KrakenAsset>> getAll();

    @Query("select asset_name from kraken_assets")
    List<String> getName();

    @Insert
    void insert(KrakenAsset... krakenAssets);

    @Delete
    void delete(KrakenAsset krakenAsset);

    @Query("delete from kraken_assets")
    void deleteAll();

}