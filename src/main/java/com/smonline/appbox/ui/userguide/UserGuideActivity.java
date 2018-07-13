package com.smonline.appbox.ui.userguide;

import android.content.Context;
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

import com.smonline.appbox.R;
import com.smonline.appbox.base.BaseActivity;
import com.smonline.appbox.ui.home.HomeActivity;
import com.smonline.appbox.widget.CircleIndicator;
import com.smonline.virtual.helper.sp.SharedPreferencesConstants.InitInfo;
import com.smonline.virtual.helper.sp.SharedPreferencesUtil;

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
        mAdapter.getGuideImgs().add(this.getResources().getDrawable(R.drawable.lufei));
        mAdapter.getGuideImgs().add(this.getResources().getDrawable(R.drawable.suolong));
        mAdapter.getGuideImgs().add(this.getResources().getDrawable(R.drawable.shanzhi));
        mAdapter.getGuideImgs().add(this.getResources().getDrawable(R.drawable.nami));
        mViewPager.setAdapter(mAdapter);
        CircleIndicator mIndicator = (CircleIndicator)findViewById(R.id.viewpager_indicator);
        mIndicator.setViewPager(mViewPager);
        SharedPreferencesUtil.setValue(InitInfo.name, InitInfo.userguideShowed, true);
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
            ImageView guideImg = pager.findViewById(R.id.guide_img);
            Button guideBtn = pager.findViewById(R.id.guide_btn);
            guideImg.setImageDrawable(guideImgItem);
            if(position == mGuideImgs.size() - 1){
                guideBtn.setVisibility(View.VISIBLE);
                guideBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
