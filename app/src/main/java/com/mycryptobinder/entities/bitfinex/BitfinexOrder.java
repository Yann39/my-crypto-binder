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

package com.mycryptobinder.entities.bitfinex;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "bitfinex_orders")
public class BitfinexOrder {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "order_id")
    private long orderId;

    @ColumnInfo(name = "symbol")
    private String symbol;

    @ColumnInfo(name = "exchange")
    private String exchange;

    @ColumnInfo(name = "price")
    private Double price;

    @ColumnInfo(name = "avg_execution_price")
    private Double avgExecutionPrice;

    @ColumnInfo(name = "side")
    private String side;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "timestamp")
    private Double timestamp;

    @ColumnInfo(name = "is_live")
    private boolean isLive;

    @ColumnInfo(name = "is_cancelled")
    private boolean isCancelled;

    @ColumnInfo(name = "is_hidden")
    private boolean isHidden;

    @ColumnInfo(name = "was_forced")
    private boolean wasForced;

    @ColumnInfo(name = "original_amount")
    private Double originalAmount;

    @ColumnInfo(name = "remaining_amount")
    private Double remainingAmount;

    @ColumnInfo(name = "executed_amount")
    private Double executedAmount;

    public BitfinexOrder(long orderId, String symbol, String exchange, Double price, Double avgExecutionPrice, String side, String type, Double timestamp, boolean isLive, boolean isCancelled, boolean isHidden, boolean wasForced, Double originalAmount, Double remainingAmount, Double executedAmount) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.exchange = exchange;
        this.price = price;
        this.avgExecutionPrice = avgExecutionPrice;
        this.side = side;
        this.type = type;
        this.timestamp = timestamp;
        this.isLive = isLive;
        this.isCancelled = isCancelled;
        this.isHidden = isHidden;
        this.wasForced = wasForced;
        this.originalAmount = originalAmount;
        this.remainingAmount = remainingAmount;
        this.executedAmount = executedAmount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAvgExecutionPrice() {
        return avgExecutionPrice;
    }

    public void setAvgExecutionPrice(Double avgExecutionPrice) {
        this.avgExecutionPrice = avgExecutionPrice;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isWasForced() {
        return wasForced;
    }

    public void setWasForced(boolean wasForced) {
        this.wasForced = wasForced;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Double getExecutedAmount() {
        return executedAmount;
    }

    public void setExecutedAmount(Double executedAmount) {
        this.executedAmount = executedAmount;
    }
}