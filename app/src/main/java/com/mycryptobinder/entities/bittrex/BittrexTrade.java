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

@Entity(tableName = "bittrex_trades")
public class BittrexTrade {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "order_uuid")
    private String orderUuid;

    @ColumnInfo(name = "exchange")
    private String exchange;

    @ColumnInfo(name = "timestamp")
    private Date timeStamp;

    @ColumnInfo(name = "order_type")
    private String orderType;

    @ColumnInfo(name = "limit")
    private Double limit;

    @ColumnInfo(name = "quantity")
    private Double quantity;

    @ColumnInfo(name = "quantity_remaining")
    private Double quantityRemaining;

    @ColumnInfo(name = "commission")
    private Double commission;

    @ColumnInfo(name = "price")
    private Double price;

    @ColumnInfo(name = "price_per_unit")
    private Double pricePerUnit;

    public BittrexTrade(String orderUuid, String exchange, Date timeStamp, String orderType, Double limit, Double quantity, Double quantityRemaining, Double commission, Double price, Double pricePerUnit) {
        this.orderUuid = orderUuid;
        this.exchange = exchange;
        this.timeStamp = timeStamp;
        this.orderType = orderType;
        this.limit = limit;
        this.quantity = quantity;
        this.quantityRemaining = quantityRemaining;
        this.commission = commission;
        this.price = price;
        this.pricePerUnit = pricePerUnit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getQuantityRemaining() {
        return quantityRemaining;
    }

    public void setQuantityRemaining(Double quantityRemaining) {
        this.quantityRemaining = quantityRemaining;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}