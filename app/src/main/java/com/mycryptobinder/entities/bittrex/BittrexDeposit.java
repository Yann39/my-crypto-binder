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

package com.mycryptobinder.entities.bittrex;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "bittrex_deposits")
public class BittrexDeposit {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "deposit_id")
    private long depositId;

    @ColumnInfo(name = "amount")
    private Double amount;

    @ColumnInfo(name = "currency")
    private String currency;

    @ColumnInfo(name = "confirmations")
    private int confirmations;

    @ColumnInfo(name = "last_updated")
    private Date lastUpdated;

    @ColumnInfo(name = "tx_id")
    private String txId;

    @ColumnInfo(name = "crypto_address")
    private String cryptoAddress;

    public BittrexDeposit(long depositId, Double amount, String currency, int confirmations, Date lastUpdated, String txId, String cryptoAddress) {
        this.depositId = depositId;
        this.amount = amount;
        this.currency = currency;
        this.confirmations = confirmations;
        this.lastUpdated = lastUpdated;
        this.txId = txId;
        this.cryptoAddress = cryptoAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDepositId() {
        return depositId;
    }

    public void setDepositId(long depositId) {
        this.depositId = depositId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getCryptoAddress() {
        return cryptoAddress;
    }

    public void setCryptoAddress(String cryptoAddress) {
        this.cryptoAddress = cryptoAddress;
    }
}