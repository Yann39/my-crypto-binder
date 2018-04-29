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
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Exchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.List;

import static com.mycryptobinder.helpers.UtilsHelper.escapeForCsv;

public class BackupRestoreViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    private final static String filename = "mycryptobinder_export_" + System.currentTimeMillis() + ".csv";
    private final static String separator = ",";

    public BackupRestoreViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getInstance(this.getApplication());
    }

    public void exportExchanges() {
        new exportExchangesAsyncTask(getApplication(), appDatabase).execute();
    }

    private static class exportExchangesAsyncTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase db;
        // use a weak reference to the application to get the context, to avoid memory leak by using a static variable
        private WeakReference<Application> appReference;

        exportExchangesAsyncTask(Application application, AppDatabase appDatabase) {
            db = appDatabase;
            this.appReference = new WeakReference<>(application);
        }

        @Override
        protected Void doInBackground(final Void... voids) {
            // get a reference to the application if it is still there
            Application app = appReference.get();
            if (app == null) return null;

            // generate file content
            StringBuilder fileContent = new StringBuilder();
            List<Exchange> exchangesToExport = db.exchangeDao().exportAll();
            for (Exchange exchange : exchangesToExport) {
                fileContent.append(escapeForCsv(exchange.getName()))
                        .append(separator)
                        .append(escapeForCsv(exchange.getLink()))
                        .append(separator)
                        .append(escapeForCsv(exchange.getDescription()))
                        .append(separator)
                        .append(escapeForCsv(exchange.getPublicApiKey()))
                        .append(separator)
                        .append(escapeForCsv(exchange.getPrivateApiKey()));
            }

            // write content to output stream
            /*FileOutputStream outputStream;
            try {
                outputStream = app.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContent.toString().getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            File dir = getPublicDocumentStorageDir("mycryptobinder_save");
            if (dir != null) {
                File file = new File(dir, filename);
            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.write(fileContent.toString());
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

            return null;
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    static File getPublicDocumentStorageDir(String directoryName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), directoryName);
        if (!file.mkdirs()) {
            System.out.println("Cannot create directory " + directoryName);
            return null;
        }
        return file;
    }


}
