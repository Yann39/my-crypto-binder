package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.AppSetting;

/**
 * Created by Yann
 * Created on 09/11/2017
 */

public class AddAppSettingsViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public AddAppSettingsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    /**
     * Insert the specified application setting into the database
     *
     * @param appSetting The new application setting to insert
     */
    public void addAppSetting(AppSetting appSetting) {
        new addAsyncTask(appDatabase).execute(appSetting);
    }

    private static class addAsyncTask extends AsyncTask<AppSetting, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final AppSetting... appSettings) {
            db.settingsDao().insert(appSettings[0]);
            return null;
        }

    }

}
