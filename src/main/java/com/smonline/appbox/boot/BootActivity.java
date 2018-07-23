package com.smonline.appbox.boot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.smonline.appbox.base.BaseActivity;
import com.smonline.appbox.ui.splashloading.SplashLoadingActivity;
import com.smonline.appbox.ui.userguide.UserGuideActivity;
import com.smonline.virtual.helper.sp.SharedPreferencesConstants.InitInfo;
import com.smonline.virtual.helper.sp.SharedPreferencesUtil;

/**
 * Created by yzm on 18-7-6.
 */

public class BootActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreferencesUtil.getBooleanValue(InitInfo.name, InitInfo.userguideShowed, false)){
            startActivity(new Intent(this, SplashLoadingActivity.class));
        }else {
            startActivity(new Intent(this, UserGuideActivity.class));
        }
        finish();
    }
}
