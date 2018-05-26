package com.example.zhong.memo.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.zhong.memo.util.MyApplication;

/**
 * Created by DELL on 2018/5/24.
 */

public class ImageAndText {

    public static void displayImageAtCursor(String imagePath, EditText content) {//将图片显示在文字中
        if(imagePath != null){
            String tagPath = "<img src=\""+imagePath+"\"/>";//为图片路径加上<img>标签
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Log.d("Image", "displayImageAtCursor: "+bitmap);
            if (bitmap != null) {
                SpannableString ss = getBitmapMime(imagePath,tagPath);
                Log.d("Image", "displayImageAtCursor: ss "+ss);
                content.append("                           ");
                content.append("\n");
                insertPhotoToEditText(ss,content);
            }
        }else{
            Toast.makeText(MyApplication.getContext(),"Failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    public static void displayImageAtCursor(Uri imageUri, EditText content) {//将图片显示在文字中
        String s = imageUri.toString();
        String imagePath = s.replace("file://","");
        if(imageUri != null){
            String tagPath = "<img src=\""+imageUri+"\"/>";//为图片路径加上<img>标签
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Log.d("Image", "displayImageAtCursor: "+bitmap);
            if (bitmap != null) {
                SpannableString ss = getBitmapMime(imagePath,tagPath);
                Log.d("Image", "displayImageAtCursor: ss "+ss);
                content.append("                           ");
                content.append("\n");
                insertPhotoToEditText(ss,content);
            }
        }else{
            Toast.makeText(MyApplication.getContext(),"Failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    //region 将图片插入到EditText光标处
    private static void insertPhotoToEditText(SpannableString ss,EditText content){
        Log.d("ImageInsert", "displayImageAtCursor: before "+content.getText());
        Editable et = content.getText();
        int start = content.getSelectionStart();
        Log.d("Image", "displayImageAtCursor: start "+start);
        et.insert(start,ss);
        content.setText(et);
        Log.d("Image", "displayImageAtCursor: et "+et);
        content.setSelection(start+ss.length());//光标移到图片后面
        content.setFocusableInTouchMode(true);
        content.setFocusable(true);
        Log.d("ImageInsert", "displayImageAtCursor: after "+content.getText());
    }


    private static SpannableString getBitmapMime(String path,String tagPath) {
        SpannableString ss = new SpannableString(tagPath);//这里使用加了<img>标签的图片路径
        int width = ScreenUtils.getScreenWidth(MyApplication.getContext());
        int height = ScreenUtils.getScreenHeight(MyApplication.getContext());
        Bitmap bitmap = ImageUtils.getSmallBitmap(path,width,480);
        ImageSpan imageSpan = new ImageSpan(MyApplication.getContext(), bitmap);
        ss.setSpan(imageSpan, 0, tagPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

}
