package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mycryptobinder.entities.AppSetting;

import java.util.List;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

@Dao
public interface AppSettingDao {

    @Query("select * from app_settings")
    LiveData<List<AppSetting>> getAll();

    @Query("select * from app_settings where name = :name")
    AppSetting getByName(String name);

    @Insert
    void insert(AppSetting... appSettings);

    @Update
    void update(AppSetting... appSettings);

    @Delete
    void delete(AppSetting appSetting);

}