package com.smonline.appbox.ui.splashloading;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.smonline.appbox.databinding.ActivityLoadingBinding;
import com.smonline.appbox.databinding.ActivitySplashBinding;
import com.smonline.appbox.ui.home.HomeActivity;
import com.smonline.appbox.utils.ABoxUtils;
import com.smonline.virtual.client.core.VirtualCore;
import com.smonline.virtual.remote.InstalledAppInfo;

import wer.xds.fds.nm.cm.ErrorCode;
import wer.xds.fds.nm.sp.SplashViewSettings;
import wer.xds.fds.nm.sp.SpotListener;
import wer.xds.fds.nm.sp.SpotManager;

/**
 * 有米广告
 * Created by yzm on 18-7-31.
 */

public class YMSplashViewModel extends BaseSplashViewModel {
    private static final String TAG = "YMSplashViewModel";
    private int mLaunchAppUserId = 0;
    private String mLaunchAppPkg = "";
    private Intent mLaunchAppIntent = null;

    public YMSplashViewModel(Activity activity, ViewDataBinding binding, boolean isLaunchingApp){
        super(activity, binding, isLaunchingApp);
    }

    @Override
    protected void onActivityCreate(Intent intent) {
        RelativeLayout adContainer = null;
        if(!isLaunchingApp && mBinding instanceof ActivitySplashBinding){
            adContainer = ((ActivitySplashBinding)mBinding).adContainer;
        }else if(isLaunchingApp && mBinding instanceof ActivityLoadingBinding){
            mLaunchAppPkg = intent.getStringExtra(SplashAdActivity.KEY_PKG_NAME);
            mLaunchAppUserId = intent.getIntExtra(SplashAdActivity.KEY_USER, 0);
            mLaunchAppIntent = intent.getParcelableExtra(SplashAdActivity.KEY_INTENT);
            LoadingAppActivity.launchUserId = mLaunchAppUserId;
            LoadingAppActivity.launchAppPkg = mLaunchAppPkg;
            LoadingAppActivity.launchAppIntent = mLaunchAppIntent;
            InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(mLaunchAppPkg, 0);
            Drawable appIcon = installedAppInfo.getApplicationInfo(mLaunchAppUserId).loadIcon(VirtualCore.getPM());
            CharSequence appName = installedAppInfo.getApplicationInfo(mLaunchAppUserId).loadLabel(VirtualCore.getPM());
            ((ActivityLoadingBinding)mBinding).launchingAppIcon.setImageDrawable(appIcon);
            ((ActivityLoadingBinding)mBinding).launchingAppName.setText(appName);
            adContainer = ((ActivityLoadingBinding)mBinding).adContainer;
        }
        // 对开屏广告进行设置
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        // 设置是否展示失败自动跳转，默认自动跳转
        splashViewSettings.setAutoJumpToTargetWhenShowFailed(false);
        // 设置跳转的窗口类
        splashViewSettings.setTargetClass(isLaunchingApp ? LoadingAppActivity.class : HomeActivity.class);
        // 设置开屏的容器
        splashViewSettings.setSplashViewContainer(adContainer);
        // 展示开屏广告
        SpotManager.getInstance(mActivity).showSplash(mActivity, splashViewSettings, new SpotListener() {

            @Override
            public void onShowSuccess() {
                ABoxUtils.logD(TAG, "开屏展示成功");
            }

            @Override
            public void onShowFailed(int errorCode) {
                StringBuilder errMsg = new StringBuilder().append("开屏展示失败");
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        errMsg.append(", 网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        errMsg.append(", 暂无开屏广告");
                        break;
                    case ErrorCode.RESOURCE_NOT_READY:
                        errMsg.append(", 开屏资源还没准备好");
                        break;
                    case ErrorCode.SHOW_INTERVAL_LIMITED:
                        errMsg.append(", 开屏展示间隔限制");
                        break;
                    case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                        errMsg.append(", 开屏控件处在不可见状态");
                        break;
                    default:
                        errMsg.append(", errorCode: " + errorCode);
                        break;
                }
                ABoxUtils.logE(TAG, errMsg.toString());
                if(isLaunchingApp){
                    if(mLaunchAppIntent != null && !TextUtils.isEmpty(mLaunchAppPkg)){
                        LoadingAppActivity.launchUserId = mLaunchAppUserId;
                        LoadingAppActivity.launchAppPkg = mLaunchAppPkg;
                        LoadingAppActivity.launchAppIntent = mLaunchAppIntent;
                        mActivity.startActivity(new Intent(mActivity, LoadingAppActivity.class));
                    }else {
                        mActivity.finish();
                    }
                }else {
                    /**广告展示失败，并且不是启动插件app则跳转到home界面**/
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            HomeActivity.goHome(mActivity);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onSpotClosed() {
                ABoxUtils.logD(TAG, "开屏被关闭");
            }

            @Override
            public void onSpotClicked(boolean isWebPage) {
                ABoxUtils.logD(TAG, "开屏被点击,是否是网页广告？ %s", isWebPage ? "是" : "不是");
            }
        });
    }

    @Override
    protected void onActivityResume() {

    }

    @Override
    protected void onActivityPause() {

    }

    @Override
    protected void onNewIntent(Intent intent) {

    }

    @Override
    protected void onActivityStop() {

    }

    @Override
    protected void onActivityDestroy() {
        // 开屏展示界面的 onDestroy() 回调方法中调用
        SpotManager.getInstance(mActivity).onDestroy();
    }
}
