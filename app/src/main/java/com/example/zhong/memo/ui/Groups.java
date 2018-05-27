package com.example.zhong.memo.ui;

import com.example.zhong.memo.R;
import com.example.zhong.memo.db.MemoGroup;

import java.util.ArrayList;
import java.util.List;


public class Groups {

    public Groups(){

    }

    private List<MemoGroup> memoGroupList = new ArrayList<>();

    public List<MemoGroup> getMemoGroupList(){
        return memoGroupList;
    }

}
