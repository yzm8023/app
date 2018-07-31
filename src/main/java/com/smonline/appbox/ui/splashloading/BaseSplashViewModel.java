package com.smonline.appbox.ui.splashloading;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by yzm on 18-7-31.
 */

public abstract class BaseSplashViewModel {
    protected Activity mActivity;
    protected ViewDataBinding mBinding;
    protected boolean isLaunchingApp;
    protected Handler mHandler = new Handler(Looper.getMainLooper());

    public BaseSplashViewModel(Activity activity, ViewDataBinding binding, boolean isLaunchingApp){
        this.mActivity = activity;
        this.mBinding = binding;
        this.isLaunchingApp = isLaunchingApp;
    }
    protected abstract void onActivityCreate(Intent intent);
    protected abstract void onActivityResume();
    protected abstract void onActivityPause();
    protected abstract void onNewIntent(Intent intent);
    protected abstract void onActivityStop();
    protected abstract void onActivityDestroy();
}
