package com.squeeze.squeezeadmin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.squeeze.squeezeadmin.R;

public class NetworkUtils {

    public static String getJwtFromSharedPrefs(Context context, SharedPreferences sharedPrefs) {
        return sharedPrefs.getString(context.getString(R.string.shared_prefs_jwt_key),"");
    }
 }
