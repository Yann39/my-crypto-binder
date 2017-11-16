package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.AppSetting;

import java.util.List;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class AppSettingListViewModel extends AndroidViewModel {

    private final LiveData<List<AppSetting>> settingsList;
    private AppDatabase appDatabase;

    public AppSettingListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        settingsList = appDatabase.settingsDao().getAll();
    }

    public LiveData<List<AppSetting>> getAppSettingsList() {
        return settingsList;
    }

    public void deleteItem(AppSetting appSetting) {
        new deleteAsyncTask(appDatabase).execute(appSetting);
    }

    private static class deleteAsyncTask extends AsyncTask<AppSetting, Void, Void> {
        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final AppSetting... appSettings) {
            db.settingsDao().delete(appSettings[0]);
            return null;
        }
    }

}
