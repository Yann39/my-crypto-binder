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

@Entity(tableName = "poloniex_withdrawals")
public class PoloniexWithdrawal {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "withdrawal_number")
    private long withdrawalNumber;

    @ColumnInfo(name = "currency")
    private String currency;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "amount")
    private Double amount;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name = "status")
    private String status;

    public PoloniexWithdrawal(long withdrawalNumber, String currency, String address, Double amount, long timestamp, String status) {
        this.withdrawalNumber = withdrawalNumber;
        this.currency = currency;
        this.address = address;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWithdrawalNumber() {
        return withdrawalNumber;
    }

    public void setWithdrawalNumber(long withdrawalNumber) {
        this.withdrawalNumber = withdrawalNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}