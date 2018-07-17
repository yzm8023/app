package com.smonline.appbox.ui.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.ObservableArrayList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.smonline.appbox.ABoxConstants;
import com.smonline.appbox.R;
import com.smonline.appbox.databinding.ActivityHomeBinding;
import com.smonline.appbox.model.AppInfo;
import com.smonline.appbox.ui.appimport.AppImportActivity;
import com.smonline.appbox.ui.splashloading.SplashLoadingActivity;
import com.smonline.appbox.utils.ABoxUtils;
import com.smonline.appbox.utils.HintViewUtil;
import com.smonline.virtual.client.core.InstallStrategy;
import com.smonline.virtual.client.core.VirtualCore;
import com.smonline.virtual.helper.sp.SharedPreferencesConstants.InitInfo;
import com.smonline.virtual.helper.sp.SharedPreferencesUtil;
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
    private Handler mHandler = new Handler(Looper.getMainLooper());

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
        mAdapter.setDataModels(mInstalledAppInfos);
        boolean hasApp = mInstalledAppInfos.size() > 0;
        mBinding.recyclerView.setVisibility(hasApp ? View.VISIBLE : View.GONE);
        mBinding.wechatLayoutContainer.setVisibility(hasApp ? View.GONE : View.VISIBLE);
        mBinding.appEmptyTip.setVisibility(View.GONE);
        mBinding.defWechatLayout.setOnClickListener(this);
    }

    public void onActivityResume(){
        if(mInstalledAppInfos.size() == 0 && !SharedPreferencesUtil.getBooleanValue(InitInfo.name, InitInfo.hintViewImportWechat, false)){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**如果是首次启动则显示导入微信的小提示**/
                    View.OnClickListener importWechatClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /**点击导入微信的小提示之后再显示导入更多应用的提示**/
                            ((ViewGroup)view.getParent()).removeView(view);
                            if(!SharedPreferencesUtil.getBooleanValue(InitInfo.name, InitInfo.hintViewImportMore, false)){
                                HintViewUtil.showImportMoreHint(mActivity, mBinding.importAppImg, null);
                            }
                        }
                    };
                    HintViewUtil.showImportWechatHint(mActivity, mBinding.wechatIcon, importWechatClickListener);
                }
            }, 1000);
        }
    }

    class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(2, 2, 2, 2);
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
        }else if(view.getId() == R.id.def_wechat_layout){
            try {
                ApplicationInfo appInfo = mPackageManager.getApplicationInfo(ABoxConstants.PKG_NAME_WECHAT,PackageManager.GET_META_DATA);
                if(appInfo != null){
                    CharSequence appName = appInfo.loadLabel(mPackageManager);
                    Intent intent = new Intent(mActivity, HomeActivity.class);
                    intent.putExtra("app_name", appName);
                    intent.putExtra("apk_path", appInfo.sourceDir);
                    mActivity.startActivity(intent);
                }else{
                    Toast.makeText(mActivity, R.string.home_toast_no_wechat, Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Toast.makeText(mActivity, R.string.home_toast_no_wechat, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onItemClick(View v, int posoition, final AppInfo appInfo, boolean isMenu) {
        if(isMenu){
            View contentLayout = LayoutInflater.from(mActivity).inflate(R.layout.popup_menu_layout,null);
            final PopupWindow popupWindow = new PopupWindow(contentLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView uninstallText = (TextView)contentLayout.findViewById(R.id.menu_uninstall);
            TextView fixText = (TextView)contentLayout.findViewById(R.id.menu_fix);
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
                            mBinding.recyclerView.setVisibility(View.GONE);
                            mBinding.wechatLayoutContainer.setVisibility(View.GONE);
                        }
                    }else {
                        Toast.makeText(mActivity, mActivity.getText(R.string.toast_tip_uninstall_failed), Toast.LENGTH_SHORT).show();
                    }
                    popupWindow.dismiss();
                }
            });
            fixText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                    try {
                        ABoxUtils.logD(TAG, "appInfo.pkg = " + appInfo.getPackageName() + ", apkPath = " + appInfo.getApkPath());
                        ApplicationInfo info = mPackageManager.getApplicationInfo(appInfo.getPackageName(),PackageManager.GET_META_DATA);
                        if(info != null){
                            ABoxUtils.logD(TAG, "info.sourceDir = " + info.sourceDir);
                            CharSequence appName = info.loadLabel(mPackageManager);
                            if(appName != null && !info.sourceDir.equals(appInfo.getApkPath())){
                                installNewApp(info.sourceDir, appName.toString(), true);
                            }else {
                                Toast.makeText(mActivity, R.string.home_fix_noneed, Toast.LENGTH_LONG).show();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1f);
                }
            });
            popupWindow.setContentView(contentLayout);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown(v);
            backgroundAlpha(0.5f);
        }else {
            SplashLoadingActivity.launchApp(mActivity, appInfo, 0);
        }
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        mActivity.getWindow().setAttributes(lp);
    }

    public void installNewApp(final String apkPath, final String appName, final boolean isUpdate){
        new AsyncTask<Void, Void, InstallResult>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                String dialogMsg = isUpdate ? String.format(mActivity.getResources().getString(R.string.home_fixing), appName) :
                        String.format(mActivity.getResources().getString(R.string.home_importing), appName);
                mLoadingDialog = createLoadingDialog(mActivity, dialogMsg);
                mLoadingDialog.show();
            }

            @Override
            protected InstallResult doInBackground(Void... voids) {
                int flags = InstallStrategy.COMPARE_VERSION | InstallStrategy.SKIP_DEX_OPT | InstallStrategy.DEPEND_SYSTEM_IF_EXIST;
                if(isUpdate){
                    flags |= InstallStrategy.UPDATE_IF_EXIST;
                }
                InstallResult installResult = VirtualCore.get().installPackage(apkPath, flags);
                ABoxUtils.logD(TAG, "installResult.isSuccess = " + installResult.isSuccess);
                ABoxUtils.logD(TAG, "installResult.isUpdate = " + installResult.isUpdate);
                if(installResult.isSuccess || installResult.isUpdate){
                    loadInstalledApps();
                }
                return installResult;
            }

            @Override
            protected void onPostExecute(InstallResult result) {
                super.onPostExecute(result);
                if(result.isSuccess || result.isUpdate){
                    mBinding.appEmptyTip.setVisibility(View.INVISIBLE);
                    mBinding.recyclerView.setVisibility(View.VISIBLE);
                    mBinding.wechatLayoutContainer.setVisibility(View.GONE);
                    mAdapter.setDataModels(mInstalledAppInfos);
                    mAdapter.notifyDataSetChanged();
                    if(isUpdate){
                        Toast.makeText(mActivity,R.string.home_fix_done, Toast.LENGTH_LONG).show();
                    }
                }else {
                    int msgId = R.string.home_import_failed;
                    if(isUpdate){
                        msgId = R.string.home_fix_fail;
                    }
                    Toast.makeText(mActivity,msgId, Toast.LENGTH_LONG).show();
                }
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
