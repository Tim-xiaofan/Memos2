package com.example.zhong.memo.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.zhong.memo.MemoActivity;
import com.example.zhong.memo.R;
import com.example.zhong.memo.util.MyApplication;

import java.io.File;
import java.io.IOException;

import static com.example.zhong.memo.MemoActivity.CHOOSE_PHOTO;

/**
 * Created by DELL on 2018/5/24.
 */

public class Album {


    public static void openAlbum(Activity activity){//打开相册
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        activity.startActivityForResult(intent,CHOOSE_PHOTO);
    }

    public static String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        return  imagePath;
    }

    @TargetApi(19)
    public static String handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(MyApplication.getContext(),uri)){//Document Uri
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                //media Uri Authority
                String id = docId.split(":")[1];//解析出数字格式Id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                //downloads Uri Authority
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads//public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //Content Uri, normally handle;
            imagePath =getImagePath(uri,null);
        }else{//File Uri,get path straightly
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private static String getImagePath(Uri uri,String selection) {
        String path = null;
        //Get path through Uri and selection
        Cursor cursor = MyApplication.getContext().getContentResolver().query(uri, null,
                selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
