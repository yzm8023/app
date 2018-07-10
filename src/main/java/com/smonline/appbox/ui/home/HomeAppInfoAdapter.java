package com.smonline.appbox.ui.home;

import android.content.Context;
import android.view.View;

import com.smonline.appbox.R;
import com.smonline.appbox.databinding.HomeRecyclerItemBinding;
import com.smonline.appbox.model.AppInfo;
import com.smonline.appbox.ui.adapter.BaseRecyclerAdapter;

/**
 * Created by yzm on 18-7-9.
 */

public class HomeAppInfoAdapter extends BaseRecyclerAdapter<AppInfo, HomeRecyclerItemBinding> {

    public HomeAppInfoAdapter(Context context){
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.home_recycler_item;
    }

    @Override
    protected int getMenuResId() {
        return R.id.menu_icon;
    }

    @Override
    protected void onBindItem(HomeRecyclerItemBinding binding, AppInfo item) {
        binding.setModel(item);
        binding.executePendingBindings();
    }

    @Override
    protected void onItemClick(View view, int position, AppInfo model, boolean isMenu) {
        if(mListener != null){
            mListener.onItemClick(view, position, model, isMenu);
        }
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        public void onItemClick(View v, int posoition, AppInfo appInfo, boolean isMenu);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
