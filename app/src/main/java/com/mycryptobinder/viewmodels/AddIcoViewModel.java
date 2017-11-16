package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Ico;

/**
 * Created by Yann
 * Created on 09/11/2017
 */

public class AddIcoViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public AddIcoViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    public void addIco(Ico ico) {
        new addAsyncTask(appDatabase).execute(ico);
    }

    private static class addAsyncTask extends AsyncTask<Ico, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Ico... icos) {
            db.icoDao().insert(icos[0]);
            return null;
        }

    }

}
