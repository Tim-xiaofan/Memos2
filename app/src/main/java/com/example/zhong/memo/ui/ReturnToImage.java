package com.example.zhong.memo.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zhong.memo.util.MyApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DELL on 2018/5/24.
 */

public class ReturnToImage {


    /*public static void initContent(String input, EditText editText) {
        Html.ImageGetter imageGetter = new Html.ImageGetter(){
            @Override
            public Drawable getDrawable(String s) {
                /*int width = ScreenUtils.getScreenWidth(MyApplication.getContext());
                int height = ScreenUtils.getScreenHeight(MyApplication.getContext());
                Bitmap bitmap = ImageUtils.getSmallBitmap(s,width,480);
                Drawable drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0,0,width,height);
                Log.d("initContent", "getDrawable: s "+s);
                Drawable drawable = null;
                drawable = Drawable.createFromPath(s);
                drawable.setBounds(0,0,480,480);
                return drawable;
            }
        };
        editText.setText(Html.fromHtml(input, imageGetter,null));
    }*/



    public static void initContent(String input, EditText editText) {
        //String regex = "<img src=\\".*?\\"\\/>";
        Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
        Matcher m = p.matcher(input);
        //List<String> result = new ArrayList<String>();

        SpannableString spannableString = new SpannableString(input);
        while(m.find()){
            Log.d("YYPT", m.group());
            //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
            String s = m.group();
            int start = m.start();
            int end = m.end();
            Log.d("YYPT", "start "+start +" end "+end);
            //path是去掉<img src=""/>的中间的图片路径
            String path = s.replaceAll("\\<img src=\"|\"\\/>","").trim();
            Log.d("YYPT", path);
            //利用spannableString和ImageSpan来替换掉这些图片
            int width = ScreenUtils.getScreenWidth(MyApplication.getContext());
            int height = ScreenUtils.getScreenHeight(MyApplication.getContext());

            try {
                Bitmap bitmap = ImageUtils.getSmallBitmap(path, width, 480);
                ImageSpan imageSpan = new ImageSpan(MyApplication.getContext(), bitmap);
                spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }catch (Exception e){
                Log.d("YYPT", "failed!!!");
                e.printStackTrace();
            }
        }
        editText.setText(spannableString);
        //content.append("\n");
        //Log.d("YYPT_RGX_SUCCESS",content.getText().toString());
    }

    public static void initContent(String input, TextView textView) {
        //String regex = "<img src=\\".*?\\"\\/>";
        Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
        Matcher m = p.matcher(input);
        //List<String> result = new ArrayList<String>();

        SpannableString spannableString = new SpannableString(input);
        while(m.find()){
            Log.d("YYPT", m.group());
            //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
            String s = m.group();
            int start = m.start();
            int end = m.end();
            Log.d("YYPT", "start "+start +" end "+end);
            //path是去掉<img src=""/>的中间的图片路径
            String path = s.replaceAll("\\<img src=\"|\"\\/>","").trim();
            Log.d("YYPT", path);
            //利用spannableString和ImageSpan来替换掉这些图片
            int width = ScreenUtils.getScreenWidth(MyApplication.getContext());
            int height = ScreenUtils.getScreenHeight(MyApplication.getContext());

            try {
                Bitmap bitmap = ImageUtils.getSmallBitmap(path, width, 480);
                ImageSpan imageSpan = new ImageSpan(MyApplication.getContext(), bitmap);
                spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }catch (Exception e){
                Log.d("YYPT", "failed!!!");
                e.printStackTrace();
            }
        }
        textView.setText(spannableString);
        //content.append("\n");
        //Log.d("YYPT_RGX_SUCCESS",content.getText().toString());
    }
}

