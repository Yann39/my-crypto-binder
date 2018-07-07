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
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mycryptobinder.helpers.DateTypeConverter;

import java.util.Date;

@Entity(
        tableName = "icos",
        foreignKeys = @ForeignKey(
                entity = Currency.class,
                parentColumns = "iso_code",
                childColumns = "currency_iso_code"),
        indices = {@Index("currency_iso_code")}
)
public class Ico {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "currency_iso_code")
    private String currencyIsoCode;

    @ColumnInfo(name = "amount")
    private Double amount;

    @ColumnInfo(name = "fee")
    private Double fee;

    @ColumnInfo(name = "invest_date")
    @TypeConverters({DateTypeConverter.class})
    private Date investDate;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "token_date")
    private Date tokenDate;

    @ColumnInfo(name = "token_quantity")
    private Double tokenQuantity;

    @ColumnInfo(name = "bonus")
    private Double bonus;

    @ColumnInfo(name = "comment")
    private String comment;

    public Ico(Long id, String name, String currencyIsoCode, Double amount, Double fee, Date investDate, String token, Date tokenDate, Double tokenQuantity, Double bonus, String comment) {
        if(id != null && id > 0) {
            this.id = id;
        }
        this.name = name;
        this.currencyIsoCode = currencyIsoCode;
        this.amount = amount;
        this.fee = fee;
        this.investDate = investDate;
        this.token = token;
        this.tokenDate = tokenDate;
        this.tokenQuantity = tokenQuantity;
        this.bonus = bonus;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public void setCurrencyIsoCode(String currencyIsoCode) {
        this.currencyIsoCode = currencyIsoCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Date getInvestDate() {
        return investDate;
    }

    public void setInvestDate(Date investDate) {
        this.investDate = investDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTokenDate() {
        return tokenDate;
    }

    public void setTokenDate(Date tokenDate) {
        this.tokenDate = tokenDate;
    }

    public Double getTokenQuantity() {
        return tokenQuantity;
    }

    public void setTokenQuantity(Double tokenQuantity) {
        this.tokenQuantity = tokenQuantity;
    }

    public Double getBonus() {
        return bonus;
    }

    public void setBonus(Double bonus) {
        this.bonus = bonus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}