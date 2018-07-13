package com.smonline.appbox.ui.appimport;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.smonline.appbox.BR;
import com.smonline.appbox.BuildConfig;
import com.smonline.appbox.R;
import com.smonline.appbox.databinding.ActivityAppimportBinding;
import com.smonline.appbox.model.AppInfo;
import com.smonline.appbox.ui.adapter.ListAdapter;
import com.smonline.appbox.ui.home.HomeActivity;
import com.smonline.appbox.utils.ABoxUtils;
import com.smonline.virtual.client.core.VirtualCore;
import com.smonline.virtual.remote.InstalledAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzm on 18-7-6.
 */

public class AppImportViewModel {
    private static final String TAG = "AppImportViewModel";

    private Activity mActivity;
    private ActivityAppimportBinding mBinding;
    private ListAdapter<AppInfo> mListAdapter;
    private PackageManager mPackageManager;
    private List<AppInfo> mAllAppInfos = new ArrayList<AppInfo>();

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public AppImportViewModel(Activity activity, ActivityAppimportBinding binding){
        mActivity = activity;
        mBinding = binding;
        mPackageManager = VirtualCore.get().getUnHookPackageManager();
        mBinding.listView.setEmptyView(mBinding.loadAppsLayout);
        mBinding.listView.setOnItemClickListener(mItemClickListener);

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                List<InstalledAppInfo> vApps = VirtualCore.get().getInstalledApps(0);
                List<ApplicationInfo> apps = mPackageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
                for(ApplicationInfo info : apps){
                    boolean canSkip = false;
                    for(InstalledAppInfo vAppInfo : vApps){
                        if(vAppInfo.packageName.equals(info.packageName)){
                            canSkip = true;
                            break;
                        }
                    }
                    if(canSkip || isSystemApp(info) || mActivity.getPackageName().equals(info.packageName)){
                        continue;
                    }
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppIcon(info.loadIcon(mPackageManager));
                    appInfo.setAppName(info.loadLabel(mPackageManager).toString());
                    try{
                        PackageInfo pkgInfo = mPackageManager.getPackageInfo(info.packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
                        appInfo.setAppVersion(pkgInfo.versionName);
                    }catch (PackageManager.NameNotFoundException e){
                        e.printStackTrace();
                    }
                    appInfo.setPackageName(info.packageName);
                    appInfo.setApkPath(info.sourceDir);
                    mAllAppInfos.add(appInfo);
                    if(BuildConfig.DEBUG){
                        ABoxUtils.logD(TAG, "appInfo = " + appInfo.toString());
                    }
                }
                if(BuildConfig.DEBUG){
                    ABoxUtils.logD(TAG, "mAllAppInfos size = " + mAllAppInfos.size());
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter = new ListAdapter<>(mActivity, mAllAppInfos, R.layout.appimport_list_item, BR.appInfo);
                        mBinding.listView.setAdapter(mListAdapter);
                    }
                }, 1000);
                return null;
            }
        }.execute();
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            AppInfo appInfo = (AppInfo)adapterView.getAdapter().getItem(position);
            if(BuildConfig.DEBUG){
                ABoxUtils.logD(TAG, "@onItemClick, app = " + appInfo.getPackageName());
            }
            Intent retIntent = new Intent(mActivity, HomeActivity.class);
            retIntent.putExtra("app_name", appInfo.getAppName());
            retIntent.putExtra("apk_path", appInfo.getApkPath());
            mActivity.startActivity(retIntent);
            mActivity.finish();
        }
    };

    public boolean isSystemApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            return true;
        }
        return false;
    }
}
