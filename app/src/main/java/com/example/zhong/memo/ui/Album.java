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

    private static String getImagePath(Uri uri,String selection){
        String path = null;
        //Get path through Uri and selection
        Cursor cursor = MyApplication.getContext().getContentResolver().query(uri,null,
                selection, null,null);
        if(cursor != null){
            if(cursor.moveToNext()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /*public static Uri getPhotoUri( final int requestCode){//添加图片
        //textView = scheduleCardView.findViewById(R.id.show_schedule_text);
        final Context context = MyApplication.getContext();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("设置提醒");
        dialog.create();
        dialog.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(MyApplication.getContext());
        View view = inflater.inflate(R.layout.alertdialog_add_photo, null);
        dialog.setView(view);
        dialog.show();
        TextView takePhoto = view.findViewById(R.id.take_photo);
        TextView choosePhoto = view.findViewById(R.id.from_album);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //File obj to store picture
                File outputImage = new File(context.getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >=24){//Android 7.0 and above
                    imageUri = FileProvider.getUriForFile(context,
                            "com.example.zhong.memo.fileprovider",outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                ((Activity)context).startActivityForResult(intent,requestCode);
            }
        });
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(context, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) context, new
                            String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},CHOOSE_PHOTO);
                }else {
                    Album.openAlbum((Activity) context);
                }
            }
        });
        return imageUri;
    }*/
}
