package com.smonline.appbox.ui.splashloading;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.smonline.appbox.R;
import com.smonline.appbox.base.BaseActivity;
import com.smonline.appbox.databinding.ActivityLoadingBinding;
import com.smonline.appbox.ui.home.HomeActivity;
import com.smonline.appbox.utils.ABoxUtils;
import com.smonline.virtual.client.core.VirtualCore;
import com.smonline.virtual.client.ipc.VActivityManager;
import com.smonline.virtual.remote.InstalledAppInfo;

/**
 * 插件进程完全没有启动时显示的猴子程序猿界面
 * Created by yzm on 18-7-6.
 */

public class LoadingAppActivity extends BaseActivity {
    private static final String TAG = "LoadingAppActivity";

    public static int launchUserId = 0;
    public static String launchAppPkg = "";
    public static Intent launchAppIntent = null;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoadingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_loading);
        if(launchAppIntent == null || TextUtils.isEmpty(launchAppPkg)){
            HomeActivity.goHome(this);
            finish();
            return;
        }
        InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(launchAppPkg, 0);
        Drawable appIcon = installedAppInfo.getApplicationInfo(launchUserId).loadIcon(VirtualCore.getPM());
        CharSequence appName = installedAppInfo.getApplicationInfo(launchUserId).loadLabel(VirtualCore.getPM());
        binding.launchingAppIcon.setImageDrawable(appIcon);
        binding.launchingAppName.setText(appName);
        if(VActivityManager.get().isAppRunning(launchAppPkg, launchUserId)){
            VActivityManager.get().startActivity(launchAppIntent, launchUserId);
            finish();
        }else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    VActivityManager.get().startActivity(launchAppIntent, launchUserId);
                }
            }, 500);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(!TextUtils.isEmpty(intent.getStringExtra("crashPkg"))) {
            Toast.makeText(this, R.string.launching_app_failed_toast, Toast.LENGTH_LONG).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoadingAppActivity.this.finish();
                }
            }, 500);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
