package com.mycryptobinder.models;

import java.util.List;

/**
 * Created by Yann
 * Created on 07/09/2017
 */

public class KrakenTrades {

    public List<String> error;
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