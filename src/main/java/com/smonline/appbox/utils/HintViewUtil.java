package com.smonline.appbox.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smonline.appbox.R;
import com.smonline.virtual.helper.sp.SharedPreferencesConstants.InitInfo;
import com.smonline.virtual.helper.sp.SharedPreferencesUtil;

/**
 * Created by yzm on 18-7-13.
 */

public class HintViewUtil {
    private static RelativeLayout mContainer;

    public static void showImportWechatHint(Activity activity, View view, View.OnClickListener listener){
        ViewGroup contentView = (ViewGroup)activity.getWindow().getDecorView().findViewById(android.R.id.content);
        int[] targetPos = getTargetPos(contentView, view);
        TextView tv = new TextView(activity);
        tv.setBackgroundResource(R.drawable.tip_bg_down_left);
        tv.setGravity(Gravity.LEFT|Gravity.CENTER);
        tv.setText(R.string.home_hint_import_wechat);
        tv.setTextColor(Color.WHITE);
        tv.setSingleLine(true);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(targetPos[0] - view.getWidth() / 2, targetPos[1] + view.getHeight(), 0, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tv.setLayoutParams(layoutParams);
        RelativeLayout container = generateContainer(activity);
        container.addView(tv);
        container.setOnClickListener(listener);
        contentView.addView(container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        SharedPreferencesUtil.setValue(InitInfo.name, InitInfo.hintViewImportWechat, true);
    }

    public static void showImportMoreHint(Activity activity, View view, View.OnClickListener listener){
        ViewGroup contentView = (ViewGroup)activity.getWindow().getDecorView().findViewById(android.R.id.content);
        int[] targetPos = getTargetPos(contentView, view);
        TextView tv = new TextView(activity);
        tv.setBackgroundResource(R.drawable.tip_bg_up_right);
        tv.setGravity(Gravity.RIGHT|Gravity.CENTER);
        tv.setText(R.string.home_hint_import_more);
        tv.setTextColor(Color.WHITE);
        tv.setSingleLine(true);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, contentView.getWidth() - targetPos[0] - view.getWidth(),
                contentView.getHeight() - targetPos[1]);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tv.setLayoutParams(layoutParams);
        RelativeLayout container = generateContainer(activity);
        container.addView(tv);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewGroup)view.getParent()).removeView(view);
            }
        });
        contentView.addView(container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        SharedPreferencesUtil.setValue(InitInfo.name, InitInfo.hintViewImportMore, true);
    }

    private static RelativeLayout generateContainer(Context context){
        if(mContainer == null){
            mContainer = new RelativeLayout(context);
            mContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.setBackgroundColor(Color.parseColor("#88000000"));
        }
        mContainer.removeAllViews();
        mContainer.setOnClickListener(null);
        return mContainer;
    }

    private static int[] getTargetPos(View content, View view){
        int[] contentPos = new int[2];
        content.getLocationInWindow(contentPos);
        int[] viewPos = new int[2];
        view.getLocationInWindow(viewPos);
        return new int[]{viewPos[0] - contentPos[0], viewPos[1] - contentPos[1]};
    }
}
