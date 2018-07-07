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
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.Formatter;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.InternalFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BackupRestoreViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    private final static String separator = ",";
    private MutableLiveData<List<InternalFile>> fileList;
    private SimpleDateFormat sdf;

    public BackupRestoreViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getInstance(this.getApplication());
        fileList = new MutableLiveData<>();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", UtilsHelper.getCurrentLocale(application.getApplicationContext()));
    }

    public void importExchanges(String filename) {
        new importExchangesAsyncTask(getApplication(), appDatabase, filename).execute();
    }

    public void exportExchangesInternal(String filename) {
        new exportExchangesInternalAsyncTask(getApplication(), appDatabase, filename).execute();
    }

    public void exportExchangesExternal(String filename) {
        new exportExchangesExternalAsyncTask(getApplication(), appDatabase, filename).execute();
    }

    public LiveData<List<InternalFile>> getFileList() {
        return fileList;
    }

    public void loadFileList() {
        Context context = getApplication().getApplicationContext();
        // get file name list
        List<String> fileNames = Arrays.asList(context.fileList());

        // get files from file names
        List<File> files = new ArrayList<>();
        String dir = getApplication().getFilesDir().getAbsolutePath();
        for (String fileName : fileNames) {
            files.add(new File(dir, fileName));
        }

        // sort files by last modified date
        Collections.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

        // copy back files names in a list with right order
        List<InternalFile> sortedFileNames = new ArrayList<>();
        for (File f : files) {
            sortedFileNames.add(new InternalFile(f.getName(), Formatter.formatShortFileSize(context, f.length()), sdf.format(f.lastModified())));
        }

        // set the live data value
        fileList.setValue(sortedFileNames);
    }

    private static class importExchangesAsyncTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase db;
        private String filename;
        // use a weak reference to the application to get the context, to avoid memory leak by using a static variable
        private WeakReference<Application> appReference;

        importExchangesAsyncTask(Application application, AppDatabase appDatabase, String filename) {
            this.db = appDatabase;
            this.filename = filename;
            this.appReference = new WeakReference<>(application);
        }

        @Override
        protected Void doInBackground(final Void... voids) {
            // get a reference to the application if it is still there
            Application app = appReference.get();
            if (app == null) return null;

            try {
                // read file
                FileInputStream fis = app.getApplicationContext().openFileInput(filename);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                String line;
                List<Exchange> exchanges = new ArrayList<>();
                while ((line = bufferedReader.readLine()) != null) {
                    // split on comma outside quotes
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    exchanges.add(new Exchange(UtilsHelper.unescapeFromCsv(parts[0]), UtilsHelper.unescapeFromCsv(parts[1]), UtilsHelper.unescapeFromCsv(parts[2]), UtilsHelper.unescapeFromCsv(parts[3]), UtilsHelper.unescapeFromCsv(parts[4])));
                }
                // remove all existing exchanges
                db.exchangeDao().deleteAll();
                // add exchanges collected from the file
                for (Exchange exchange : exchanges) {
                    db.exchangeDao().insert(exchange);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private static class exportExchangesInternalAsyncTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase db;
        private String filename;
        // use a weak reference to the application to get the context, to avoid memory leak by using a static variable
        private WeakReference<Application> appReference;

        exportExchangesInternalAsyncTask(Application application, AppDatabase appDatabase, String filename) {
            this.db = appDatabase;
            this.filename = filename;
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
                fileContent.append(UtilsHelper.escapeForCsv(exchange.getName()))
                        .append(separator)
                        .append(UtilsHelper.escapeForCsv(exchange.getLink()))
                        .append(separator)
                        .append(UtilsHelper.escapeForCsv(exchange.getDescription()))
                        .append(separator)
                        .append(UtilsHelper.escapeForCsv(exchange.getPublicApiKey()))
                        .append(separator)
                        .append(UtilsHelper.escapeForCsv(exchange.getPrivateApiKey()))
                        .append("\n");
            }

            // write content to output stream
            FileOutputStream outputStream;
            try {
                // set a default file name in case it is not specified (should not occurs)
                if (filename == null || filename.trim().length() <= 0) {
                    filename = "export_" + new Date().getTime();
                }
                outputStream = app.getApplicationContext().openFileOutput(filename + ".csv", Context.MODE_PRIVATE);
                outputStream.write(fileContent.toString().getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private static class exportExchangesExternalAsyncTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase db;
        private String filename;
        // use a weak reference to the application to get the context, to avoid memory leak by using a static variable
        private WeakReference<Application> appReference;

        exportExchangesExternalAsyncTask(Application application, AppDatabase appDatabase, String filename) {
            this.db = appDatabase;
            this.filename = filename;
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
                fileContent.append(UtilsHelper.escapeForCsv(exchange.getName()))
                        .append(separator)
                        .append(UtilsHelper.escapeForCsv(exchange.getLink()))
                        .append(separator)
                        .append(UtilsHelper.escapeForCsv(exchange.getDescription()))
                        .append(separator)
                        .append(UtilsHelper.escapeForCsv(exchange.getPublicApiKey()))
                        .append(separator)
                        .append(UtilsHelper.escapeForCsv(exchange.getPrivateApiKey()))
                        .append("\n");
            }

            // get the directory to write to, or create it if it does not exist
            File dir = null;
            if (isExternalStorageReadable() && isExternalStorageWritable()) {
                dir = getPublicDocumentStorageDir(app.getString(R.string.app_name));
            }

            if (dir != null) {
                // write content to output stream
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
            } else {
                System.out.println("Directory cannot be created?");
            }
            return null;
        }
    }

    /**
     * Checks if external storage is available for read and write
     *
     * @return true if external storage is writable, false if not
     */
    static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read
     *
     * @return true if external storage is readable, false if not
     */
    static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Get the File object associated to the specified directory name
     * Used to save a file to a public directory
     *
     * @param directoryName The public directory name
     * @return A File object representing the specified directory name
     */
    static File getPublicDocumentStorageDir(String directoryName) {
        // get the directory for the user's public documents directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), directoryName);
        if (!file.mkdirs()) {
            System.out.println("Cannot create directory " + directoryName);
            // show a notification about the created item
            return null;
        }
        return file;
    }

    /**
     * Get the File object associated to the specified directory name
     * Used to save a file to a private directory
     * This directory will be deleted when user uninstall the app
     *
     * @param context       The current context
     * @param directoryName The private directory name
     * @return A File object representing the specified directory name
     */
    static File getPrivateDocumentStorageDir(Context context, String directoryName) {
        // get the directory for the user's public pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), directoryName);
        if (!file.mkdirs()) {
            System.out.println("Cannot create directory " + directoryName);
            // show a notification about the created item
            return null;
        }
        return file;
    }


}
