package com.example.zhong.memo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zhong.memo.MemoActivity;
import com.example.zhong.memo.R;
import com.example.zhong.memo.ShowMemoActivity;
import com.example.zhong.memo.db.Memo;
import com.example.zhong.memo.db.MemoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2018/5/12.
 */
public class MemoAdapter extends RecyclerView.Adapter <MemoAdapter.ViewHolder>{

    private Context mContext;

    private List<Memo> mMemoList = new ArrayList<>();

    private List<Memo> selectedMemoList = new ArrayList<>();

    private boolean isUnderSelecting;

    private boolean isNeededAllSelected;

    private boolean isNeededAllUnSelected;

    private String groupShowName;

    //private final boolean isPartSelected = true;

    private int countSelectedItem;

    private MemoManager memoManager;

    private boolean isUnderGroupView;

    String inputText;

    private static final int EDIT_MEMO = 1;

    //private boolean isClassified;

    SparseBooleanArray mCheckStates=new SparseBooleanArray();
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//创建ViewHolder 实例
        //并传入构造函数
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.memo_item,parent,
                false);
        final ViewHolder holder =  new ViewHolder(view);
        if(!isUnderSelecting){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    Memo memo = mMemoList.get(position);
                    Intent intent =new Intent(mContext, ShowMemoActivity.class);
                    intent.putExtra("memo_data",memo);
                    intent.putExtra("isUnderGroupView",isUnderGroupView);
                    intent.putExtra("state",EDIT_MEMO);
                    if(groupShowName.equals("全部")){
                        ((Activity)mContext).startActivityForResult(intent,2);
                    }else{
                        ((Activity)mContext).startActivityForResult(intent,1);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Memo memo = mMemoList.get(position);
        MemoManager memoManager = new MemoManager();
        inputText = memoManager.loadTxt(memo.getFileName());
        holder.memoContent.setText(inputText);
        holder.memoType.setText(memo.getType());
        holder.memoRefreshDateTime.setText(memo.getRefreshDate()+" "+memo.getRefreshTime());
        if(!TextUtils.isEmpty(memo.getScheduleDate())&&!TextUtils.isEmpty(memo.getScheduleTime())){
            holder.memoScheduleDateTime.setText(memo.getScheduleDate()+" "+memo.getScheduleTime());
        }else {
            holder.memoScheduleMain.setVisibility(View.GONE);
        }
        Glide.with(mContext).load(memo.getImageId()).into(holder.memoImage);
        if(isUnderSelecting){
            holder.memoCheckBox.setVisibility(View.VISIBLE);
            holder.memoCheckBox.setTag(position);
            /*if(isNeededAllSelected){
                mCheckStates.put(position,true)
                if(getCountSelectedItem() >= mMemoList.size()){
                    isNeededAllSelected = false;//
                }
            }
            if(isNeededAllUnSelected){
                //所有都置为不选中
                mCheckStates.put(position,false);
                if (countSelectedItem == 0){
                    isNeededAllUnSelected = false;//
                }
            }*/
            holder.memoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos =(int)buttonView.getTag();
                    if(isChecked){
                        mCheckStates.put(pos,true);
                        selectedMemoList.add(mMemoList.get(pos));
                        //do something
                    }else{
                        mCheckStates.delete(pos);
                        selectedMemoList.remove(pos);
                        //do something else
                    }
                }
            });
            holder.memoCheckBox.setChecked(mCheckStates.get(position,false));
            Log.d("onBindViewHolder", "onBindViewHolder: countSelectedItem  "+getCountSelectedItem());
        }
    }

    @Override
    public int getItemCount() {
        return mMemoList.size();
    }

    public boolean isUnderSelecting() {
        return isUnderSelecting;
    }

    public void isUnderSelecting(boolean underSelecting) {
        isUnderSelecting = underSelecting;
    }

    public boolean isNeededAllSelected() {
        return isNeededAllSelected;
    }

    public void isNeededAllSelected(boolean neededAllSelected) {
        isNeededAllSelected = neededAllSelected;
    }

    public void isNeededAllUnSelected(boolean neededAllUnSelected) {
        isNeededAllUnSelected = neededAllUnSelected;
    }

    public String getGroupShowName() {
        return groupShowName;
    }

    public void setGroupShowName(String groupShowName) {
        this.groupShowName = groupShowName;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {//定义ViewHolder类
        CardView cardView;
        ImageView memoImage;
        TextView memoType;
        CheckBox memoCheckBox;
        TextView memoRefreshDateTime;
        TextView memoScheduleDateTime;
        LinearLayout memoScheduleMain;
        TextView memoContent;
        public ViewHolder(View view) {
            super(view);
            cardView = (CardView)view;
            memoImage = view.findViewById(R.id.memo_image);
            memoType = view.findViewById(R.id.memo_type);
            memoCheckBox = view.findViewById(R.id.check_box);
            memoRefreshDateTime = view.findViewById(R.id.memo_refreshDateTime_textView);
            memoScheduleDateTime = view.findViewById(R.id.memo_scheduleDateTime_textView);
            memoScheduleMain = view.findViewById(R.id.schedule_main);
            memoContent = view.findViewById(R.id.memo_content);
        }
    }

    public MemoAdapter(){//构造函数
        isUnderGroupView = false;
        memoManager = new MemoManager();
        mMemoList = memoManager.getMemoList();//初始化数据源list
        isUnderSelecting = false;
        isNeededAllSelected = false;
        isNeededAllUnSelected =false;
        //isClassified = true;
        countSelectedItem = 0;
        if(mMemoList.size() == 0){
            Log.d("MemoAdapter", "MemoAdapter is null");
        }
        for(int i = 0; i<mMemoList.size(); i++){
            Log.d("MemoAdapter", "MemoAdapter: "+mMemoList.get(i).getType());
        }
        groupShowName = "全部";
    }

    public MemoAdapter(String groupShowName){//构造函数
        isUnderGroupView = true;
        memoManager = new MemoManager();
        this.groupShowName = groupShowName;
        //有筛选地初始化数据源list
        if(groupShowName.equals("全部")){
            mMemoList = memoManager.getMemoList();
        }else{
            for(int i = 0; i <memoManager.getMemoList().size(); i++){
                if(memoManager.getMemoList().get(i).getType().equals( groupShowName)){
                    mMemoList.add(memoManager.getMemoList().get(i));
                }
            }
        }
        if(mMemoList.size()==0) Log.d("MemoAdapter", "MemoAdapter: no object in");
        isUnderSelecting = false;
        isNeededAllSelected = false;
        isNeededAllUnSelected =false;
        //isClassified = true;
        countSelectedItem = 0;
    }

    public int getCountSelectedItem() {
        int count = 0;
        for(int i=0;i < mCheckStates.size();i++) {
            if(mCheckStates.valueAt(i)){
                count++;
            }
        }
        if(count != countSelectedItem){
            countSelectedItem = count;//更新选中的数目
        }
        return countSelectedItem;
    }

    public List<Memo> getSelectedMemoList(){
        return selectedMemoList;
    }
}

