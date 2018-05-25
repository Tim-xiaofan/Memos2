package com.example.zhong.memo.db;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2018/5/12.
 */

public class MemoGroup extends DataSupport{

    private String groupName;

    private int imageId;

    private int amountOfMemos;

    private List<Memo> memoList = new ArrayList<>();

    public MemoGroup(String typeName, int imageId){
        this.groupName = typeName;
        this.imageId = imageId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String typeName) {
        this.groupName = typeName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getAmountOfMemos() {
        return amountOfMemos;
    }

    public void setAmountOfMemos(int amountOfMemos) {
        this.amountOfMemos = amountOfMemos;
    }
}
