package com.smonline.appbox.ui.userguide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.smonline.appbox.R;
import com.smonline.appbox.base.BaseActivity;
import com.smonline.appbox.ui.home.HomeActivity;
import com.smonline.appbox.widget.CircleIndicator;
import com.smonline.virtual.helper.sp.SharedPreferencesConstants.InitInfo;
import com.smonline.virtual.helper.sp.SharedPreferencesUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 安装之后首次启动时显示的用户引导页面
 *
 * Created by yzm on 18-7-6.
 */

public class UserGuideActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguide);
        ViewPager mViewPager = (ViewPager)findViewById(R.id.view_pager);
        UserGuidePagerAdapter mAdapter = new UserGuidePagerAdapter(this);
        mAdapter.getGuideImgs().add(this.getResources().getDrawable(R.mipmap.ic_launcher_no_bg));
        mAdapter.getGuideImgs().add(this.getResources().getDrawable(R.drawable.userguide_2));
        mAdapter.getGuideImgs().add(this.getResources().getDrawable(R.drawable.userguide_3));
        mViewPager.setAdapter(mAdapter);
        CircleIndicator mIndicator = (CircleIndicator)findViewById(R.id.viewpager_indicator);
        mIndicator.setViewPager(mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Object userManager = this.getSystemService(Context.USER_SERVICE);
            if (userManager != null) {
                Method getUserHandle = userManager.getClass().getMethod("getUserHandle", (Class<?>[]) null);
                getUserHandle.setAccessible(true);
                int userHandle = (int) getUserHandle.invoke(userManager);
                if(userHandle != 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(android.R.string.dialog_alert_title);
                    builder.setMessage(R.string.single_user_tip);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserGuideActivity.this.finish();
                        }
                    });
                    builder.create().show();
                }
            }
        } catch (Exception e) {
        }
    }

    public class UserGuidePagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<Drawable> mGuideImgs = new ArrayList<>();

        public UserGuidePagerAdapter(Context context){
            mContext = context;
        }

        public void setGuideImgs(List<Drawable> guideImgs){
            mGuideImgs = guideImgs;
        }

        public List<Drawable> getGuideImgs(){
            return mGuideImgs;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View pager = LayoutInflater.from(mContext).inflate(R.layout.userguide_pager_item, null);
            Drawable guideImgItem = mGuideImgs.get(position);
            RelativeLayout logoLayout = (RelativeLayout)pager.findViewById(R.id.logo_rlayout);
            ImageView guideImg = pager.findViewById(R.id.guide_img);
            Button guideBtn = pager.findViewById(R.id.guide_btn);
            guideImg.setImageDrawable(guideImgItem);
            if(position == 0){
                logoLayout.setVisibility(View.VISIBLE);
                guideImg.setVisibility(View.GONE);
            }else if(position == mGuideImgs.size() - 1){
                guideBtn.setVisibility(View.VISIBLE);
                guideBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferencesUtil.setValue(InitInfo.name, InitInfo.userguideShowed, true);
                        HomeActivity.goHome(mContext);
                        UserGuideActivity.this.finish();
                    }
                });
            }
            container.addView(pager);
            return pager;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ViewGroup)object);
        }

        @Override
        public int getCount() {
            return mGuideImgs != null ? mGuideImgs.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
