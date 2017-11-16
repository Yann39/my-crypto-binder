package com.mycryptobinder.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by Yann
 * Created on 26/05/2017
 */

@Entity(tableName = "transactions", foreignKeys = {
        @ForeignKey(entity = Exchange.class,
                parentColumns = "name",
                childColumns = "exchange_name"),
        @ForeignKey(entity = Currency.class,
                parentColumns = "iso_code",
                childColumns = "currency1_iso_code"),
        @ForeignKey(entity = Currency.class,
                parentColumns = "iso_code",
                childColumns = "currency2_iso_code")
})
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "exchange_name")
    private String exchangeName;

    @ColumnInfo(name = "transaction_id")
    private String transactionId;

    @ColumnInfo(name = "currency1_iso_code")
    private String currency1IsoCode;

    @ColumnInfo(name = "currency2_iso_code")
    private String currency2IsoCode;

    @ColumnInfo(name = "fee")
    private Double fee;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "quantity")
    private Double quantity;

    @ColumnInfo(name = "price")
    private Double price;

    @ColumnInfo(name = "total")
    private Double total;

    @ColumnInfo(name = "sum_currency1")
    private Double sumCurrency1;

    @ColumnInfo(name = "sum_currency2")
    private Double sumCurrency2;

    @ColumnInfo(name = "comment")
    private String comment;

    public Transaction(String exchangeName, String transactionId, String currency1IsoCode, String currency2IsoCode, Double fee, Date date, String type, Double quantity, Double price, Double total, Double sumCurrency1, Double sumCurrency2, String comment) {
        this.exchangeName = exchangeName;
        this.transactionId = transactionId;
        this.currency1IsoCode = currency1IsoCode;
        this.currency2IsoCode = currency2IsoCode;
        this.fee = fee;
        this.date = date;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.sumCurrency1 = sumCurrency1;
        this.sumCurrency2 = sumCurrency2;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCurrency1IsoCode() {
        return currency1IsoCode;
    }

    public void setCurrency1IsoCode(String currency1IsoCode) {
        this.currency1IsoCode = currency1IsoCode;
    }

    public String getCurrency2IsoCode() {
        return currency2IsoCode;
    }

    public void setCurrency2IsoCode(String currency2IsoCode) {
        this.currency2IsoCode = currency2IsoCode;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getSumCurrency1() {
        return sumCurrency1;
    }

    public void setSumCurrency1(Double sumCurrency1) {
        this.sumCurrency1 = sumCurrency1;
    }

    public Double getSumCurrency2() {
        return sumCurrency2;
    }

    public void setSumCurrency2(Double sumCurrency2) {
        this.sumCurrency2 = sumCurrency2;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}