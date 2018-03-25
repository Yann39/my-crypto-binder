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

package com.mycryptobinder.entities.kraken;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "kraken_asset_pairs")
public class KrakenAssetPair {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "asset_pair")
    private String assetPair;

    @ColumnInfo(name = "alt_name")
    private String altName;

    @ColumnInfo(name = "base")
    private String base;

    @ColumnInfo(name = "quote")
    private String quote;

    public KrakenAssetPair(String assetPair, String altName, String base, String quote) {
        this.assetPair = assetPair;
        this.altName = altName;
        this.base = base;
        this.quote = quote;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssetPair() {
        return assetPair;
    }

    public void setAssetPair(String assetpair) {
        this.assetPair = assetpair;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altname) {
        this.altName = altname;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }
}