package com.example.zhong.memo.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by DELL on 2018/5/12.
 */


public class Memo extends DataSupport implements Serializable{

    private MemoGroup memoGroup;

    private String refreshDate;

    private String refreshTime;

    private String scheduleDate;

    private String scheduleTime;

    private  String type;

    private String fileName = "";

    private int imageId;

    private  int isSelected = 0;

    public Memo(String type, int imageId){
        this.type = type;
        this.imageId = imageId;
        //isSelected = false;
    }

    public Memo(){
        this.type = "All";
        this.imageId = 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int isSelected() {
        return isSelected;
    }

    public void isSelected(int isSelected) {
        this.isSelected = isSelected;
    }


    public MemoGroup getMemoGroup() {
        return memoGroup;
    }

    public void setMemoGroup(MemoGroup memoGroup) {
        this.memoGroup = memoGroup;
    }

    public String getRefreshDate() {
        return refreshDate;
    }

    public void setRefreshDate(String refreshDate) {
        this.refreshDate = refreshDate;
    }

    public String getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(String refreshTime) {
        this.refreshTime = refreshTime;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }


    public boolean equals(Memo memo){
        if(!this.type.equals(memo.getType()))return false;
        if(!this.fileName.equals(memo.fileName))return false;
        if(this.imageId != memo.getImageId())return  false;
        return true;
    }
}
