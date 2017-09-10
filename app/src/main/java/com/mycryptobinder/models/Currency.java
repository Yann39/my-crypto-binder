package com.mycryptobinder.models;

/**
 * Created by Yann
 * Created on 25/05/2017
 */

public class Currency {

    private String isoCode;
    private String name;
    private String symbol;

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
