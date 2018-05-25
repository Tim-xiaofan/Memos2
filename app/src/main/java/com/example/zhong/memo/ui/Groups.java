package com.example.zhong.memo.ui;

import com.example.zhong.memo.R;
import com.example.zhong.memo.db.MemoGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2018/5/12.
 */

public class Groups {

    private MemoGroup[] groupsArray ={
            new MemoGroup("Apple", R.drawable.apple_pic),
            new MemoGroup("Banana", R.drawable.banana_pic),
            new MemoGroup("Orange",R.drawable.orange_pic),
            new MemoGroup("Pear",R.drawable.pear_pic),
            new MemoGroup("Grape",R.drawable.grape_pic),
            new MemoGroup("Watermelon",R.drawable.watermelon_pic),
            new MemoGroup("Pineapple",R.drawable.pineapple_pic),
            new MemoGroup("Strawberry",R.drawable.strawberry_pic),
            new MemoGroup("Cherry",R.drawable.cherry_pic),
            new MemoGroup("Mango",R.drawable.mango_pic),
    };

    public Groups(){

    }

    private List<MemoGroup> memoGroupList = new ArrayList<>();

    public List<MemoGroup> getMemoGroupList(){
        return memoGroupList;
    }

    public void initGroups(){
        memoGroupList.clear();
        for(int i = 0; i<10; i++){
            memoGroupList.add(groupsArray[i]);
        }
    }
}
