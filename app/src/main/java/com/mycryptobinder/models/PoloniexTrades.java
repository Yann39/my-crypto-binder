package com.mycryptobinder.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Yann
 * Created on 07/09/2017
 */

public class PoloniexTrades {

    @SerializedName("result")
    public Map<String, List<PoloniexTradeValue>> result;

    public Map<String, List<PoloniexTradeValue>> getResult() {
        return result;
    }

    public void setResult(Map<String, List<PoloniexTradeValue>> result) {
        this.result = result;
    }
}