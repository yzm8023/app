package com.smonline.appbox.ui.appimport;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.smonline.appbox.R;
import com.smonline.appbox.base.BaseActivity;
import com.smonline.appbox.databinding.ActivityAppimportBinding;

/**
 * Created by yzm on 18-7-6.
 */

public class AppImportActivity extends BaseActivity{

    public static void gotoImport(Context context){
        Intent intent = new Intent(context, AppImportActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ActivityAppimportBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_appimport);
        AppImportViewModel viewModel = new AppImportViewModel(this, binding);
        binding.setViewModel(viewModel);
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
