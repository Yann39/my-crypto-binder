package com.mycryptobinder.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mycryptobinder.R;

/**
 * Activity responsible for new ICO creation
 * <p>
 * Created by Yann on 07/10/2017
 */

public class AddIcoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.title_add_ico));
        setContentView(R.layout.activity_add_ico);

    }
}
