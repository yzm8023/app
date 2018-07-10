package com.smonline.appbox.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by yzm on 18-7-6.
 *
 * 希望能是一个通用的ListAdapter
 * @param <M> 代表数据模型Model
 */
public class ListAdapter<M> extends BaseAdapter {

    private Context mContext;
    private List<M> mList;
    private int mLayoutId;
    private int mVariableId;

    public void setList(List<M> list){
        mList = list;
    }

    public ListAdapter(Context context, List<M> list, int layoutId, int variableId){
        mContext = context;
        mList = list;
        mLayoutId = layoutId;
        mVariableId = variableId;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewDataBinding binding = null;
         if (convertView == null){
             binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),mLayoutId, parent,false);
         } else {
             binding = DataBindingUtil.getBinding(convertView);
         }

         binding.setVariable(mVariableId, (mList != null ? mList.get(position) : null));
         return binding.getRoot();
    }
}
