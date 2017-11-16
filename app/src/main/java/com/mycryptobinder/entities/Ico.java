package com.mycryptobinder.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mycryptobinder.helpers.DateTypeConverter;

import java.util.Date;

/**
 * Created by Yann
 * Created on 26/10/2017
 */

@Entity(tableName = "icos", foreignKeys =
@ForeignKey(entity = Exchange.class,
        parentColumns = "iso_code",
        childColumns = "currency_iso_code"))
public class Ico {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "currency_iso_code")
    private String currencyIsoCode;

    @ColumnInfo(name = "amount")
    private Double amount;

    @ColumnInfo(name = "fee")
    private Double fee;

    @ColumnInfo(name = "invest_date")
    @TypeConverters({DateTypeConverter.class})
    private Date investDate;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "token_date")
    private Date tokenDate;

    @ColumnInfo(name = "token_quantity")
    private Double tokenQuantity;

    @ColumnInfo(name = "bonus")
    private Double bonus;

    @ColumnInfo(name = "comment")
    private String comment;

    public Ico(String name, String currencyIsoCode, Double amount, Double fee, Date investDate, String token, Date tokenDate, Double tokenQuantity, Double bonus, String comment) {
        this.name = name;
        this.currencyIsoCode = currencyIsoCode;
        this.amount = amount;
        this.fee = fee;
        this.investDate = investDate;
        this.token = token;
        this.tokenDate = tokenDate;
        this.tokenQuantity = tokenQuantity;
        this.bonus = bonus;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public void setCurrencyIsoCode(String currencyIsoCode) {
        this.currencyIsoCode = currencyIsoCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Date getInvestDate() {
        return investDate;
    }

    public void setInvestDate(Date investDate) {
        this.investDate = investDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTokenDate() {
        return tokenDate;
    }

    public void setTokenDate(Date tokenDate) {
        this.tokenDate = tokenDate;
    }

    public Double getTokenQuantity() {
        return tokenQuantity;
    }

    public void setTokenQuantity(Double tokenQuantity) {
        this.tokenQuantity = tokenQuantity;
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