package com.example.zhong.memo.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by DELL on 2018/5/12.
 */

public class MyApplication extends Application {


    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }

}
