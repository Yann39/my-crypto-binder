package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by Yann
 * Created on 07/09/2017
 */

public class KrakenTradeTrades {

    @SerializedName("trades")
    public Map<String, KrakenTradeValue> trades;

    @SerializedName("count")
    public Integer count;

    public Map<String, KrakenTradeValue> getTrades() {
        return trades;
    }

    public void setTrades(Map<String, KrakenTradeValue> trades) {
        this.trades = trades;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}