package com.mycryptobinder.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Yann
 * Created on 02/11/2017
 */

@Entity(tableName = "kraken_ledger")
public class KrakenLedger {

    @PrimaryKey(autoGenerate = true)
    private String id;

    @ColumnInfo(name = "ref_id")
    private String refId;

    @ColumnInfo(name = "time")
    private Long time;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "aclass")
    private String aClass;

    @ColumnInfo(name = "asset")
    private String asset;

    @ColumnInfo(name = "amount")
    private Double amount;

    @ColumnInfo(name = "fee")
    private Double fee;

    @ColumnInfo(name = "balance")
    private Double balance;

    public KrakenLedger(String refId, Long time, String type, String aClass, String asset, Double amount, Double fee, Double balance) {
        this.refId = refId;
        this.time = time;
        this.type = type;
        this.aClass = aClass;
        this.asset = asset;
        this.amount = amount;
        this.fee = fee;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getaClass() {
        return aClass;
    }

    public void setaClass(String aClass) {
        this.aClass = aClass;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}