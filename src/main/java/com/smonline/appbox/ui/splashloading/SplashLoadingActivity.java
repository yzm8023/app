package com.smonline.appbox.ui.splashloading;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smonline.appbox.R;
import com.smonline.appbox.model.AppInfo;
import com.smonline.appbox.ui.home.HomeActivity;
import com.smonline.appbox.base.BaseActivity;
import com.smonline.virtual.client.core.VirtualCore;
import com.smonline.virtual.client.ipc.VActivityManager;
import com.smonline.virtual.remote.InstalledAppInfo;

/**
 * 一个多功能的界面
 * 1.应用启动时显示的splash界面
 * 2.启动插件app的loading界面
 * 3.将来还要用来显示开屏广告
 *
 * Created by yzm on 18-7-6.
 */

public class SplashLoadingActivity extends BaseActivity {
    private static final String TAG = "SplashLoadingActivity";

    private static final String KEY_PKG_NAME = "KEY_PKG_NAME";
    private static final String KEY_INTENT = "KEY_INTENT";
    private static final String KEY_USER = "KEY_USER";

    private boolean isLaunchingApp = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static void launchApp(Context context, AppInfo appInfo, int userId){
        Intent intent = VirtualCore.get().getLaunchIntent(appInfo.getPackageName(), userId);
        if (intent != null) {
            if(VActivityManager.get().isAppRunning(appInfo.getPackageName(), userId)){
                VActivityManager.get().startActivity(intent, userId);
            }else {
                Intent launchAppIntent = new Intent(context, SplashLoadingActivity.class);
                launchAppIntent.putExtra(KEY_PKG_NAME, appInfo.getPackageName());
                launchAppIntent.putExtra(KEY_INTENT, intent);
                launchAppIntent.putExtra(KEY_USER, userId);
                launchAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchAppIntent);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String pkg = intent.getStringExtra(KEY_PKG_NAME);
        final int userId = intent.getIntExtra(KEY_USER, 0);
        final Intent targetIntent = intent.getParcelableExtra(KEY_INTENT);
        /**如果这里intent为空说明是从桌面启动到home界面，否则就是启动插件app**/
        if(targetIntent == null){
            setContentView(R.layout.activity_splash);
        }else {
            setContentView(R.layout.activity_loading);
            isLaunchingApp = true;
            InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(pkg, 0);
            Drawable appIcon = installedAppInfo.getApplicationInfo(0).loadIcon(VirtualCore.getPM());
            CharSequence appName = installedAppInfo.getApplicationInfo(0).loadLabel(VirtualCore.getPM());
            ImageView appIconImg = (ImageView) findViewById(R.id.launching_app_icon);
            TextView appNameTxt = (TextView) findViewById(R.id.launching_app_name);
            appIconImg.setImageDrawable(appIcon);
            appNameTxt.setText(appName);
            appIconImg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    VActivityManager.get().startActivity(targetIntent, userId);
                }
            }, 500);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**不是启动插件app则跳转到home界面**/
        if(!isLaunchingApp){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    HomeActivity.goHome(SplashLoadingActivity.this);
                }
            }, 1000);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(isLaunchingApp && !TextUtils.isEmpty(intent.getStringExtra("crashPkg"))){
            Toast.makeText(this, R.string.launching_app_failed_toast, Toast.LENGTH_LONG).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SplashLoadingActivity.this.finish();
                }
            }, 500);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
