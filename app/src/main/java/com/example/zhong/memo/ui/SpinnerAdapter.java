package com.example.zhong.memo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zhong.memo.R;
import com.example.zhong.memo.db.MemoGroup;

import java.util.List;

/**
 * Created by DELL on 2018/5/13.
 */

public class SpinnerAdapter extends BaseAdapter{

    private List<MemoGroup> mMemoGroupList;

    private Context mContext ;

    public SpinnerAdapter(Context context ,List<MemoGroup> memoGroupList){
        mMemoGroupList = memoGroupList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mMemoGroupList.size();
    }

    @Override
    public MemoGroup getItem(int position) {
        return mMemoGroupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
        convertView=_LayoutInflater.inflate(R.layout.group_item, null);
        if(convertView!=null){
            TextView groupName = convertView.findViewById(R.id.group_name);
            MemoGroup memoGroup = mMemoGroupList.get(position);
            groupName.setText(memoGroup.getGroupName());
        }
        return convertView;
    }
}
