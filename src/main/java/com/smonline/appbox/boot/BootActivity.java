package com.smonline.appbox.boot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.smonline.appbox.base.BaseActivity;
import com.smonline.appbox.ui.splashloading.SplashLoadingActivity;

/**
 * Created by yzm on 18-7-6.
 */

public class BootActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, SplashLoadingActivity.class));
        finish();
    }
}
