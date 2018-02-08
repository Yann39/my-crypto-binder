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

package com.mycryptobinder.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "currencies")
public class Currency {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "iso_code")
    private String isoCode;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "symbol")
    private String symbol;

    public Currency(String isoCode, String name, String symbol) {
        this.isoCode = isoCode;
        this.name = name;
        this.symbol = symbol;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;

        if (isoCode != null ? !isoCode.equals(currency.isoCode) : currency.isoCode != null)
            return false;
        if (name != null ? !name.equals(currency.name) : currency.name != null) return false;
        return symbol != null ? symbol.equals(currency.symbol) : currency.symbol == null;
    }

    @Override
    public int hashCode() {
        int result = isoCode != null ? isoCode.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        return result;
    }
}
