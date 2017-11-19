package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yann
 * Created on 09/09/2017
 */

public class PoloniexTradeValue {

    @SerializedName("globalTradeID")
    private Long globalTradeID;

    @SerializedName("tradeId")
    private String tradeId;

    @SerializedName("date")
    private String date;

    @SerializedName("rate")
    private String rate;

    @SerializedName("amount")
    private String amount;

    @SerializedName("total")
    private String total;

    @SerializedName("fee")
    private String fee;

    @SerializedName("orderNumber")
    private String orderNumber;

    @SerializedName("type")
    private String type;

    @SerializedName("category")
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