package com.mycryptobinder.models;

import java.util.Date;

/**
 * Created by Yann
 * Created on 26/10/2017
 */

public class Ico {

    private String name;
    private String currency;
    private Double amount;
    private Double fees;
    private Date invest_date;
    private String token;
    private Date token_date;
    private Double token_quantity;
    private Double bonus;
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    public Date getInvest_date() {
        return invest_date;
    }

    public void setInvest_date(Date invest_date) {
        this.invest_date = invest_date;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getToken_date() {
        return token_date;
    }

    public void setToken_date(Date token_date) {
        this.token_date = token_date;
    }

    public Double getToken_quantity() {
        return token_quantity;
    }

    public void setToken_quantity(Double token_quantity) {
        this.token_quantity = token_quantity;
    }

    public Double getBonus() {
        return bonus;
    }

    public void setBonus(Double bonus) {
        this.bonus = bonus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}