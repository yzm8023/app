package com.smonline.appbox.ui.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import com.smonline.appbox.R;
import com.smonline.appbox.base.BaseActivity;

/**
 * Created by yzm on 18-7-17.
 */

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_about);
        try {
            PackageInfo pkgInfo = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            TextView versionTxt = (TextView)findViewById(R.id.version_txt);
            versionTxt.setText("v" + pkgInfo.versionName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
