package com.smonline.appbox.boot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.smonline.appbox.ABoxConstants;
import com.smonline.appbox.base.BaseActivity;
import com.smonline.appbox.ui.splashloading.SplashAdActivity;
import com.smonline.appbox.ui.userguide.UserGuideActivity;
import com.smonline.appbox.utils.ABoxUtils;
import com.smonline.virtual.helper.sp.SharedPreferencesConstants.InitInfo;
import com.smonline.virtual.helper.sp.SharedPreferencesUtil;

import wer.xds.fds.AdManager;
import wer.xds.fds.nm.cm.ErrorCode;
import wer.xds.fds.nm.sp.SpotManager;
import wer.xds.fds.nm.sp.SpotRequestListener;

/**
 * Created by yzm on 18-7-6.
 */

public class BootActivity extends BaseActivity {
    private static final String TAG = "BootActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreferencesUtil.getBooleanValue(InitInfo.name, InitInfo.userguideShowed, false)){
            startActivity(new Intent(this, SplashAdActivity.class));
        }else {
            startActivity(new Intent(this, UserGuideActivity.class));
        }

        //应用启动时设置有米广告
        AdManager.getInstance(this).init(ABoxConstants.YM_APPID, ABoxConstants.YM_APPSECRET, true);
        preloadAd();
        finish();
    }

    /**
     * 预加载广告
     * 不必每次展示插播广告前都请求，只需在应用启动时请求一次
     */
    private void preloadAd() {
        SpotManager.getInstance(BootActivity.this).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                Log.d(TAG,"广告请求成功");
                // 应用安装后首次展示开屏会因为本地没有数据而跳过
                // 如果开发者需要在首次也能展示开屏，可以在请求广告成功之前展示应用的logo，请求成功后再加载开屏
                // setupSplashAd();
            }

            @Override
            public void onRequestFailed(int errorCode) {
                StringBuilder errMsg = new StringBuilder().append("广告请求失败，errorCode: ").append(errorCode);
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        errMsg.append(", 网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        errMsg.append(", 暂无视频广告");
                        break;
                    default:
                        errMsg.append(", 请稍后再试");
                        break;
                }
                ABoxUtils.logE(TAG,errMsg.toString());
            }
        });
    }
}
