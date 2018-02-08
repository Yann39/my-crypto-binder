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

@Entity(tableName = "kraken_trades")
public class KrakenTrade {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "order_tx_id")
    private String orderTxId;

    @ColumnInfo(name = "pair")
    private String pair;

    @ColumnInfo(name = "time")
    private Double time;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "order_type")
    private String orderType;

    @ColumnInfo(name = "price")
    private Double price;

    @ColumnInfo(name = "cost")
    private Double cost;

    @ColumnInfo(name = "fee")
    private Double fee;

    @ColumnInfo(name = "vol")
    private Double vol;

    @ColumnInfo(name = "margin")
    private Double margin;

    @ColumnInfo(name = "misc")
    private String misc;

    public KrakenTrade(String orderTxId, String pair, Double time, String type, String orderType, Double price, Double cost, Double fee, Double vol, Double margin, String misc) {
        this.orderTxId = orderTxId;
        this.pair = pair;
        this.time = time;
        this.type = type;
        this.orderType = orderType;
        this.price = price;
        this.cost = cost;
        this.fee = fee;
        this.vol = vol;
        this.margin = margin;
        this.misc = misc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderTxId() {
        return orderTxId;
    }

    public void setOrderTxId(String orderTxId) {
        this.orderTxId = orderTxId;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getVol() {
        return vol;
    }

    public void setVol(Double vol) {
        this.vol = vol;
    }

    public Double getMargin() {
        return margin;
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }
}