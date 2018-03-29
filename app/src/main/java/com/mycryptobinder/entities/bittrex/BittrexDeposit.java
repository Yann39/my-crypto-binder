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

    @ColumnInfo(name = "payment_uuid")
    private String paymentUuid;

    @ColumnInfo(name = "currency")
    private String currency;

    @ColumnInfo(name = "amount")
    private Double amount;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "opened")
    private Date opened;

    @ColumnInfo(name = "authorized")
    private boolean authorized;

    @ColumnInfo(name = "pending_payment")
    private boolean pendingPayment;

    @ColumnInfo(name = "tx_cost")
    private Double txCost;

    @ColumnInfo(name = "tx_id")
    private String txId;

    @ColumnInfo(name = "canceled")
    private boolean canceled;

    @ColumnInfo(name = "invalid_address")
    private boolean invalidAddress;

    public BittrexDeposit(String paymentUuid, String currency, Double amount, String address, Date opened, boolean authorized, boolean pendingPayment, Double txCost, String txId, boolean canceled, boolean invalidAddress) {
        this.paymentUuid = paymentUuid;
        this.currency = currency;
        this.amount = amount;
        this.address = address;
        this.opened = opened;
        this.authorized = authorized;
        this.pendingPayment = pendingPayment;
        this.txCost = txCost;
        this.txId = txId;
        this.canceled = canceled;
        this.invalidAddress = invalidAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPaymentUuid() {
        return paymentUuid;
    }

    public void setPaymentUuid(String paymentUuid) {
        this.paymentUuid = paymentUuid;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getOpened() {
        return opened;
    }

    public void setOpened(Date opened) {
        this.opened = opened;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public boolean isPendingPayment() {
        return pendingPayment;
    }

    public void setPendingPayment(boolean pendingPayment) {
        this.pendingPayment = pendingPayment;
    }

    public Double getTxCost() {
        return txCost;
    }

    public void setTxCost(Double txCost) {
        this.txCost = txCost;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isInvalidAddress() {
        return invalidAddress;
    }

    public void setInvalidAddress(boolean invalidAddress) {
        this.invalidAddress = invalidAddress;
    }
}