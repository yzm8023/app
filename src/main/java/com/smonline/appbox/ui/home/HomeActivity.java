package com.smonline.appbox.ui.home;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.smonline.appbox.R;
import com.smonline.appbox.base.BaseActivity;
import com.smonline.appbox.databinding.ActivityHomeBinding;
import com.smonline.appbox.ui.appimport.AppImportActivity;

public class HomeActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "HomeActivity";

    private HomeViewModel mHomeViewModel;

    public static void goHome(Context context){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mHomeViewModel = new HomeViewModel(this, binding);
        binding.setViewModel(mHomeViewModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.import_app_img:
                AppImportActivity.gotoImport(this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final String appName = intent.getStringExtra("app_name");
        final String apkPath = intent.getStringExtra("apk_path");
        if(TextUtils.isEmpty(apkPath)) return;
        mHomeViewModel.installNewApp(apkPath, appName);
    }
}
