package com.smonline.appbox.utils;

import android.util.Log;

import com.smonline.appbox.BuildConfig;

/**
 * Created by yzm on 17-12-1.
 */

public class ABoxUtils {
    private static boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG_PREFIX = "ABox_";

    public static void logD(String tag, String strFormat, Object... args){
        if(DEBUG){
            Log.d(TAG_PREFIX + tag, String.format(strFormat, args));
        }
    }

    public static void logE(String tag, String strFormat, Object... args){
        if(DEBUG){
            Log.e(TAG_PREFIX + tag, String.format(strFormat, args));
        }
    }
}
