package com.example.zhong.memo.db;

import android.support.design.widget.NavigationView;
import android.text.TextUtils;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.zhong.memo.util.MyApplication;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


public class GroupManager {

    private NavigationView navigationView;

    private List<MemoGroup> memoGroupList = new ArrayList<>();

    private Spinner spinner;

    private int count;

    private int countAllMemo;

    public GroupManager(NavigationView navigationView){
        countAllMemo = 0;
        this.navigationView = navigationView;
        memoGroupList = DataSupport.findAll(MemoGroup.class);
        initGroup();
        count = memoGroupList.size();
    }

    public GroupManager(Spinner spinner){
        countAllMemo = 0;
        this.spinner = spinner;
        List<MemoGroup> temp = DataSupport.findAll(MemoGroup.class);
        initGroup();
        memoGroupList.add(new MemoGroup("全部",0));
        for(int i = 0;i<temp.size();i++){
            memoGroupList.add(temp.get(i));
        }
        memoGroupList.add(new MemoGroup("新建分组",0));
        count = memoGroupList.size();
    }

    public GroupManager(){
        countAllMemo = 0;
        memoGroupList = DataSupport.findAll(MemoGroup.class);
        initGroup();
        count = memoGroupList.size();
    }
//管理导航栏菜单
    public void refreshMenu(){
        navigationView.getMenu().clear();
        navigationView.getMenu().add(0,0,0,"全部");
        for(int i = 0; i< memoGroupList.size(); i++){
            MemoGroup memoGroup = memoGroupList.get(i);
            navigationView.getMenu().add(i+1,i+1, i+1,
                    memoGroup.getGroupName());//动态添加menu
        }
    }

//管理spinner

    public void refreshSpinner(){
        spinner.setDropDownWidth(450);
        com.example.zhong.memo.ui.SpinnerAdapter spinnerAdapter = new
                com.example.zhong.memo.ui.SpinnerAdapter(MyApplication
                .getContext(),memoGroupList);
        spinner.setAdapter(spinnerAdapter);
    }

    public void deleteMenu(int index){
        //进行UI操作
        //UI操作成功后，反馈给数据库管理类
    }

    public List<MemoGroup> getGroupList() {
        return memoGroupList;
    }

    public boolean addNewGroup(String newGroupName){
        if(!TextUtils.isEmpty(newGroupName)){
            for(int i = 0;i<count;i++){//查重复
                if(newGroupName.equals(memoGroupList.get(i).getGroupName())){
                    Toast.makeText(MyApplication.getContext(),"该分组已存在",Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            }
            (new MemoGroup(newGroupName,0)).save();
            return  true;
        }else{
            Toast.makeText(MyApplication.getContext(),"输入为空",Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
    }

    public List<MemoGroup> getMemoGroupList() {
        return memoGroupList;
    }

    public int getCount() {
        return count;
    }

    private void initGroup(){
        for(int i = 0;i<memoGroupList.size();i++){
            int result = DataSupport.where("type = ?", memoGroupList.get(i)
                    .getGroupName()).count(Memo.class);
            countAllMemo+=result;
            if(result == 0){
                DataSupport.deleteAll(MemoGroup.class,"groupName = ?",
                        memoGroupList.get(i).getGroupName());
                //memoGroupList.remove(i);
            }else{
                memoGroupList.get(i).setAmountOfMemos(result);
            }
        }
    }
}
