package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yann
 * Created on 09/09/2017
 */

public class PoloniexTradeValue {

    private Long globalTradeID;
    @SerializedName("tradeID")
    private String tradeId;
    private String date;
    private String rate;
    private String amount;
    private String total;
    private String fee;
    private String orderNumber;
    private String type;
    private String category;

    public Long getGlobalTradeID() {
        return globalTradeID;
    }

    public void setGlobalTradeID(Long globalTradeID) {
        this.globalTradeID = globalTradeID;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
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