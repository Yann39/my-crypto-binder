/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.AppSetting;

import java.util.List;

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
