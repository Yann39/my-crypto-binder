package com.mycryptobinder.helpers;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

/**
 * Created by Yann
 * Created on 05/06/2017
 */

public class UtilsHelper {

    private Context context;

    public UtilsHelper(Context c) {
        context = c;
    }

    public Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

}
