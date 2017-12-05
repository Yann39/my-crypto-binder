package com.mycryptobinder.models;

import java.util.List;
import java.util.Map;

/**
 * Created by Yann
 * Created on 05/09/2017
 */

public class KrakenAssetPairs {

    public List<String> error;
    public Map<String, KrakenAssetPairValue> result;

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public Map<String, KrakenAssetPairValue> getResult() {
        return result;
    }

    public void setResult(Map<String, KrakenAssetPairValue> result) {
        this.result = result;
    }

}