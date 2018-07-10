package com.smonline.appbox.ui.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.ObservableArrayList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.smonline.appbox.R;
import com.smonline.appbox.databinding.ActivityHomeBinding;
import com.smonline.appbox.model.AppInfo;
import com.smonline.appbox.ui.appimport.AppImportActivity;
import com.smonline.appbox.ui.splashloading.SplashLoadingActivity;
import com.smonline.appbox.utils.ABoxUtils;
import com.smonline.virtual.client.core.InstallStrategy;
import com.smonline.virtual.client.core.VirtualCore;
import com.smonline.virtual.remote.InstallResult;
import com.smonline.virtual.remote.InstalledAppInfo;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by yzm on 18-7-9.
 */

public class HomeViewModel implements View.OnClickListener, HomeAppInfoAdapter.OnItemClickListener{

    private static final String TAG = "HomeViewModel";

    private Activity mActivity;
    private ActivityHomeBinding mBinding;

    private Dialog mLoadingDialog;
    private HomeAppInfoAdapter mAdapter;
    private PackageManager mPackageManager;
    private ObservableArrayList<AppInfo> mInstalledAppInfos = new ObservableArrayList<>();

    public HomeViewModel(Activity activity, ActivityHomeBinding binding){
        mActivity = activity;
        mBinding = binding;
        mPackageManager = VirtualCore.get().getUnHookPackageManager();
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mBinding.recyclerView.addItemDecoration(new MyItemDecoration());
        mAdapter = new HomeAppInfoAdapter(mActivity);
        mAdapter.setOnItemClickListener(this);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.importAppImg.setOnClickListener(this);
        loadInstalledApps();

        if(mInstalledAppInfos.size() > 0){
            mAdapter.setDataModels(mInstalledAppInfos);
            mBinding.appEmptyTip.setVisibility(View.INVISIBLE);
            mBinding.recyclerView.setVisibility(View.VISIBLE);
        }else {
            mBinding.recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(1, 1, 1, 1);
        }
    }

    private void loadInstalledApps(){
        mInstalledAppInfos.clear();
        List<InstalledAppInfo> installedApps = VirtualCore.get().getInstalledApps(0);
        for(InstalledAppInfo info : installedApps){
            AppInfo appInfo = new AppInfo();
            appInfo.setAppIcon(info.getApplicationInfo(0).loadIcon(mPackageManager));
            appInfo.setAppName(info.getApplicationInfo(0).loadLabel(mPackageManager).toString());
            appInfo.setAppVersion(info.getPackageInfo(0).versionName);
            appInfo.setPackageName(info.packageName);
            appInfo.setApkPath(info.apkPath);
            mInstalledAppInfos.add(appInfo);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.import_app_img){
            AppImportActivity.gotoImport(mActivity);
        }
    }

    @Override
    public void onItemClick(View v, int posoition, final AppInfo appInfo, boolean isMenu) {
        if(isMenu){
            View contentLayout = LayoutInflater.from(mActivity).inflate(R.layout.popup_menu_layout,null);
            final PopupWindow popupWindow = new PopupWindow(contentLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView uninstallText = (TextView) contentLayout.findViewById(R.id.menu_uninstall);
            uninstallText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(VirtualCore.get().uninstallPackage(appInfo.getPackageName())){
                        ListIterator<AppInfo> iterator = mInstalledAppInfos.listIterator();
                        while (iterator.hasNext()){
                            AppInfo info = iterator.next();
                            if(info != null && info.getPackageName().equals(appInfo.getPackageName())){
                                iterator.remove();
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if(mInstalledAppInfos.size() == 0){
                            mBinding.appEmptyTip.setVisibility(View.VISIBLE);
                            mBinding.recyclerView.setVisibility(View.INVISIBLE);
                        }
                    }else {
                        Toast.makeText(mActivity, mActivity.getText(R.string.toast_tip_uninstall_failed), Toast.LENGTH_SHORT).show();
                    }
                    popupWindow.dismiss();
                }
            });
            popupWindow.setContentView(contentLayout);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown(v);
        }else {
            SplashLoadingActivity.launchApp(mActivity, appInfo, 0);
        }
    }

    public void installNewApp(final String apkPath, final String appName){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mLoadingDialog = createLoadingDialog(mActivity, "正在安装 : " + appName);
                mLoadingDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                int flags = InstallStrategy.COMPARE_VERSION | InstallStrategy.SKIP_DEX_OPT | InstallStrategy.DEPEND_SYSTEM_IF_EXIST;
                InstallResult installResult = VirtualCore.get().installPackage(apkPath, flags);
                ABoxUtils.d(TAG, "installResult = " + installResult.isSuccess);
                if(installResult.isSuccess){
                    loadInstalledApps();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mBinding.appEmptyTip.setVisibility(View.INVISIBLE);
                mBinding.recyclerView.setVisibility(View.VISIBLE);
                mAdapter.setDataModels(mInstalledAppInfos);
                mAdapter.notifyDataSetChanged();
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
            }
        }.execute();
    }

    private Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_importing, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        return loadingDialog;
    }
}
