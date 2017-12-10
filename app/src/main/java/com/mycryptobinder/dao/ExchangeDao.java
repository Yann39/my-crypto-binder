package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mycryptobinder.entities.Exchange;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface ExchangeDao {

    @Query("select * from exchanges")
    LiveData<List<Exchange>> getAll();

    @Query("select * from exchanges where name = :name")
    Exchange getByName(String name);

    @Insert
    void insert(Exchange... exchanges);

    @Update
    void update(Exchange... exchanges);

    @Delete
    void delete(Exchange exchange);

    @Query("delete from exchanges;")
    void deleteAll();

}