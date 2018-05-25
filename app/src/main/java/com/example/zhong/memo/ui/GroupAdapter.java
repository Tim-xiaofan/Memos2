package com.example.zhong.memo.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zhong.memo.R;
import com.example.zhong.memo.db.MemoGroup;

import java.util.List;

/**
 * Created by DELL on 2018/5/12.
 */

public class GroupAdapter extends RecyclerView.Adapter <GroupAdapter.ViewHolder>{

    private Context mContext;

    private List<MemoGroup> mMemoGroupList;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.group_item,
                parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemoGroup memoGroup = mMemoGroupList.get(position);
        holder.groupName.setText(memoGroup.getGroupName());
        Glide.with(mContext).load(memoGroup.getImageId()).into(holder.groupImage);
    }

    @Override
    public int getItemCount() {
        return mMemoGroupList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView groupImage;
        TextView groupName;

        public ViewHolder(View view) {
            super(view);
            groupName = view.findViewById(R.id.group_name);
            groupImage = view.findViewById(R.id.group_image);
        }
    }

    public GroupAdapter(Groups groups){
        mMemoGroupList = groups.getMemoGroupList();
    }
}
