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

package com.mycryptobinder.activities;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.InternalFilesListAdapter;
import com.mycryptobinder.viewmodels.BackupRestoreViewModel;

import java.io.File;

public class BackupRestoreActivity extends AppCompatActivity {

    private BackupRestoreViewModel backupRestoreViewModel;
    private EditText fileNameEditText;
    private String fileName;
    //private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        // set toolbar as actionbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view model
        backupRestoreViewModel = ViewModelProviders.of(this).get(BackupRestoreViewModel.class);

        // get view components
        final Button backupButton = findViewById(R.id.backup_restore_backup_button);
        final Button restoreButton = findViewById(R.id.backup_restore_restore_button);
        final Button deleteButton = findViewById(R.id.backup_restore_delete_button);
        fileNameEditText = findViewById(R.id.backup_restore_filename_editText);
        final RecyclerView recyclerView = findViewById(R.id.backup_restore_fileList_recyclerView);

        // prepare the recycler view with a linear layout
        recyclerView.setHasFixedSize(true); // to improve performance as we know any changes in content will not modify the layout size of the RecyclerView
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // initialize the adapter for the list
        final InternalFilesListAdapter internalFilesListAdapter = new InternalFilesListAdapter();
        recyclerView.setAdapter(internalFilesListAdapter);

        // load the file name list first
        backupRestoreViewModel.loadFileList();

        // observe the file name list from the view model so it is always up to date
        backupRestoreViewModel.getFileList().observe(this, internalFilesListAdapter::setFileNameList);

        // set click listener for the backup button
        backupButton.setOnClickListener(view -> {
            fileName = fileNameEditText.getText().toString();
            if (fileName.trim().length() > 0) {

                // permission has already been granted, export file
                backupRestoreViewModel.exportExchangesInternal(fileName);

                // show a notification about the backed up file
                final Toast toast = Toast.makeText(view.getContext(), getString(R.string.success_file_backed_up, fileName), Toast.LENGTH_SHORT);
                toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastGreen));
                toast.show();
                // clear text field
                fileNameEditText.setText("");
                // reload list so the new file is displayed
                backupRestoreViewModel.loadFileList();

            } else {
                // show a notification about the error
                final Toast toast = Toast.makeText(view.getContext(), getString(R.string.error_backup_file_name_not_specified), Toast.LENGTH_SHORT);
                toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastRed));
                toast.show();
            }
        });

        // used to save the file to external storage
        /*backupButton.setOnClickListener(view -> {
            fileName = fileNameEditText.getText().toString();
            if (fileName.trim().length() > 0) {
                // check user application permission setting to see if it is allowed to write to external storage
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    // if following condition is true it means permission has already been asked to user but he refused
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // show an explanation to the user to explain why this permission is required
                        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage(getString(R.string.message_external_storage_permission_needed));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    // request permission again
                                    ActivityCompat.requestPermissions(BackupRestoreActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                });
                        alertDialog.show();
                    } else {
                        // no explanation needed, request the permission
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    // permission has already been granted, export file
                    backupRestoreViewModel.exportExchangesExternal(fileName);

                    // show a notification about the backed up file
                    Toast toast = Toast.makeText(view.getContext(), getString(R.string.success_file_backed_up, fileName), Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastGreen));
                    toast.show();
                    // clear text field
                    fileNameEditText.setText("");
                    // reload list so the new file is displayed
                    backupRestoreViewModel.loadFileList();
                }
            } else {
                // show a notification about the error
                Toast toast = Toast.makeText(view.getContext(), getString(R.string.error_backup_file_name_not_specified), Toast.LENGTH_SHORT);
                toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastRed));
                toast.show();
            }
        });*/

        // set click listener for the restore button
        restoreButton.setOnClickListener(view -> {
            final String fileName = internalFilesListAdapter.getSelectedFileName();
            if (fileName != null && fileName.trim().length() > 0) {
                backupRestoreViewModel.importExchanges(fileName);
                // show a notification about the restored file
                final Toast toast = Toast.makeText(view.getContext(), getString(R.string.success_file_restored, fileName), Toast.LENGTH_SHORT);
                toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastGreen));
                toast.show();
            } else {
                // show a notification about the error
                final Toast toast = Toast.makeText(view.getContext(), getString(R.string.error_must_select_file), Toast.LENGTH_SHORT);
                toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastRed));
                toast.show();
            }
        });

        // set click listener for the delete button
        deleteButton.setOnClickListener(view -> {
            final String fileName = internalFilesListAdapter.getSelectedFileName();
            if (fileName != null && fileName.trim().length() > 0) {
                // show a confirm dialog
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(getString(R.string.title_confirmation));
                alert.setMessage(Html.fromHtml(getString(R.string.confirm_delete_file, fileName)));

                // "yes" button click
                alert.setPositiveButton(getString(R.string.label_yes), (dialog, which) -> {

                    // delete the file
                    final String dir = getFilesDir().getAbsolutePath();
                    final File file = new File(dir, fileName);
                    boolean deleted = file.delete();

                    // show a notification
                    if (deleted) {
                        final Toast toast = Toast.makeText(view.getContext(), getString(R.string.success_file_deleted, fileName), Toast.LENGTH_SHORT);
                        toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastGreen));
                        toast.show();
                    } else {
                        final Toast toast = Toast.makeText(view.getContext(), getString(R.string.error_delete_file, fileName), Toast.LENGTH_SHORT);
                        toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastRed));
                        toast.show();
                    }

                    backupRestoreViewModel.loadFileList();

                    dialog.dismiss();
                });

                // "no" button click
                alert.setNegativeButton(getString(R.string.label_no), (dialog, which) -> dialog.dismiss());

                alert.show();
            } else {
                // show a notification about the error
                Toast.makeText(view.getContext(), getString(R.string.error_must_select_file), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        // back arrow click
        if (id == android.R.id.home) {
            // close current activity and return to previous activity if there is any
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // if request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    backupRestoreViewModel.exportExchangesExternal(fileName);
                    // show a notification about the backed up file
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.success_file_backed_up, fileName), Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastGreen));
                    toast.show();
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error_write_external_storage_permission_refused), Toast.LENGTH_SHORT);
                    toast.getView().setBackgroundColor(getResources().getColor(R.color.colorToastRed));
                    toast.show();
                }
            }
        }
    }*/

}
