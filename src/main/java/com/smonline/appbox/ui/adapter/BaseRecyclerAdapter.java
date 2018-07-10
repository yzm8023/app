package com.smonline.appbox.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yzm on 18-7-6.
 */

public abstract class BaseRecyclerAdapter<M, B extends ViewDataBinding> extends RecyclerView.Adapter {

    protected Context mContext;
    protected ObservableArrayList<M> mDataModels;
    protected ListChangedCallback mDataModelsChangeCallback;

    public BaseRecyclerAdapter(Context context){
        mContext = context;
        mDataModels = new ObservableArrayList<>();
        mDataModelsChangeCallback = new ListChangedCallback();
    }

    public void setDataModels(ObservableArrayList<M> dataModels){
        this.mDataModels = dataModels;
    }

    public ObservableArrayList<M> getDataModels(){
        return mDataModels;
    }

    @Override
    public int getItemCount() {
        return mDataModels.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        B binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getLayoutResId(viewType), parent, false);
        return new BaseBindingViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        B binding = DataBindingUtil.getBinding(holder.itemView);
        this.onBindItem(binding, this.mDataModels.get(position));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mDataModels.addOnListChangedCallback(mDataModelsChangeCallback);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mDataModels.removeOnListChangedCallback(mDataModelsChangeCallback);
    }

    protected void onChanged(ObservableArrayList<M> newDataModels) {
        resetItems(newDataModels);
        notifyDataSetChanged();
    }

    protected void onItemRangeChanged(ObservableArrayList<M> newDataModels, int positionStart, int itemCount) {
        resetItems(newDataModels);
        notifyItemRangeChanged(positionStart,itemCount);
    }

    protected void onItemRangeInserted(ObservableArrayList<M> newDataModels, int positionStart, int itemCount) {
        resetItems(newDataModels);
        notifyItemRangeInserted(positionStart,itemCount);
    }

    protected void onItemRangeMoved(ObservableArrayList<M> newDataModels) {
        resetItems(newDataModels);
        notifyDataSetChanged();
    }

    protected void onItemRangeRemoved(ObservableArrayList<M> newDataModels, int positionStart, int itemCount) {
        resetItems(newDataModels);
        notifyItemRangeRemoved(positionStart,itemCount);
    }

    protected void resetItems(ObservableArrayList<M> newDataModels) {
        mDataModels = newDataModels;
    }

    protected abstract int getLayoutResId(int viewType);

    protected abstract int getMenuResId();

    protected abstract void onBindItem(B binding, M item);

    protected abstract void onItemClick(View view, int position, M model, boolean isMenu);

    private class ListChangedCallback extends ObservableArrayList.OnListChangedCallback<ObservableArrayList<M>>{

        @Override
        public void onChanged(ObservableArrayList<M> sender) {
            BaseRecyclerAdapter.this.onChanged(sender);
        }

        @Override
        public void onItemRangeInserted(ObservableArrayList<M> sender, int positionStart, int itemCount) {
            BaseRecyclerAdapter.this.onItemRangeInserted(sender, positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(ObservableArrayList<M> sender, int positionStart, int itemCount) {
            BaseRecyclerAdapter.this.onItemRangeRemoved(sender, positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(ObservableArrayList<M> sender, int positionStart, int itemCount) {
            BaseRecyclerAdapter.this.onItemRangeChanged(sender, positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableArrayList<M> sender, int fromPosition, int toPosition, int itemCount) {
            BaseRecyclerAdapter.this.onItemRangeMoved(sender);
        }
    }

    public class BaseBindingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public BaseBindingViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            View menu = itemView.findViewById(getMenuResId());
            if(menu != null){
                menu.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            boolean isMenu = false;
            if(view.getId() == getMenuResId()){
                isMenu = true;
            }
            onItemClick(view, getLayoutPosition(), mDataModels.get(getLayoutPosition()), isMenu);
        }
    }
}
