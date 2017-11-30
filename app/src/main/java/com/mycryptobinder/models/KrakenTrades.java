package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Yann
 * Created on 07/09/2017
 */

public class KrakenTrades {

    @SerializedName("error")
    public List<String> error;

    @SerializedName("result")
    public KrakenTradeTrades result;

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public KrakenTradeTrades getResult() {
        return result;
    }

    public void setResult(KrakenTradeTrades result) {
        this.result = result;
    }
}