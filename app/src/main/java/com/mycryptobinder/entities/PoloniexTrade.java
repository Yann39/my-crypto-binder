package com.mycryptobinder.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by Yann
 * Created on 09/09/2017
 */

@Entity(tableName = "poloniex_trades")
public class PoloniexTrade {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "pair")
    private String pair;

    @ColumnInfo(name = "global_trade_id")
    private Long globalTradeID;

    @ColumnInfo(name = "trade_id")
    private Long tradeId;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "rate")
    private Double rate;

    @ColumnInfo(name = "amount")
    private Double amount;

    @ColumnInfo(name = "total")
    private Double total;

    @ColumnInfo(name = "fee")
    private Double fee;

    @ColumnInfo(name = "order_number")
    private Long orderNumber;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "category")
    private String category;

    public PoloniexTrade(String pair, Long globalTradeID, Long tradeId, Date date, Double rate, Double amount, Double total, Double fee, Long orderNumber, String type, String category) {
        this.pair = pair;
        this.globalTradeID = globalTradeID;
        this.tradeId = tradeId;
        this.date = date;
        this.rate = rate;
        this.amount = amount;
        this.total = total;
        this.fee = fee;
        this.orderNumber = orderNumber;
        this.type = type;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public long getGlobalTradeID() {
        return globalTradeID;
    }

    public void setGlobalTradeID(long globalTradeID) {
        this.globalTradeID = globalTradeID;
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}