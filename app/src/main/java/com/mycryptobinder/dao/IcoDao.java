package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mycryptobinder.entities.Ico;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface IcoDao {

    @Query("select * from icos")
    LiveData<List<Ico>> getAll();

    @Insert
    void insert(Ico... icos);

    @Update
    void update(Ico... icos);

    @Delete
    void delete(Ico ico);

}