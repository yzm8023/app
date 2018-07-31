package com.smonline.appbox.ui.splashloading;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.smonline.appbox.R;
import com.smonline.appbox.databinding.ActivityLoadingBinding;
import com.smonline.appbox.model.AppInfo;
import com.smonline.appbox.base.BaseActivity;
import com.smonline.appbox.databinding.ActivitySplashBinding;
import com.smonline.virtual.client.core.VirtualCore;

/**
 * 一个多功能的界面
 * 1.应用启动时显示的splash界面
 * 2.开屏广告和启动插件app的广告
 *
 * Created by yzm on 18-7-6.
 */

public class SplashAdActivity extends BaseActivity {
    private static final String TAG = "SplashAdActivity";

    public static final String KEY_PKG_NAME = "KEY_PKG_NAME";
    public static final String KEY_INTENT = "KEY_INTENT";
    public static final String KEY_USER = "KEY_USER";

    private BaseSplashViewModel mViewModel;

    public static void launchApp(Context context, AppInfo appInfo, int userId){
        Intent intent = VirtualCore.get().getLaunchIntent(appInfo.getPackageName(), userId);
        if (intent != null) {
            Intent launchAppIntent = new Intent(context, SplashAdActivity.class);
            launchAppIntent.putExtra(KEY_PKG_NAME, appInfo.getPackageName());
            launchAppIntent.putExtra(KEY_INTENT, intent);
            launchAppIntent.putExtra(KEY_USER, userId);
            launchAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchAppIntent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final Intent targetIntent = intent.getParcelableExtra(KEY_INTENT);
        /**如果这里intent为空说明是从桌面启动到home界面，否则就是启动插件app**/
        if(targetIntent == null){
            ActivitySplashBinding bingding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
            mViewModel = new YMSplashViewModel(this, bingding, false);
        }else {
            ActivityLoadingBinding bingding = DataBindingUtil.setContentView(this, R.layout.activity_loading);
            mViewModel = new YMSplashViewModel(this, bingding, true);
        }
        mViewModel.onActivityCreate(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewModel.onActivityPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mViewModel.onNewIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.onActivityStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.onActivityDestroy();
    }

    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
