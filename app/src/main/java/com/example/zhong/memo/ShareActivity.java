package com.example.zhong.memo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zhong.memo.db.MemoManager;
import com.example.zhong.memo.ui.ReturnToImage;
import com.example.zhong.memo.ui.ScreenUtils;

public class ShareActivity extends AppCompatActivity {

    private ScrollView scrollView;

    private String fileName;

    private TextView contentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("预览");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){//
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ImageView codeImage = findViewById(R.id.weixin_code);
        Glide.with(ShareActivity.this).load(R.drawable.ic_action_note).into(codeImage);
        scrollView = findViewById(R.id.scroll_view);
        Intent intent = getIntent();
        fileName = intent.getStringExtra("file_name");
        MemoManager memoManager = new MemoManager();
        String content = memoManager.loadTxt(fileName);
        contentView = findViewById(R.id.share_content);
        ReturnToImage.initContent(content,contentView);
    }

    public boolean onCreateOptionsMenu(Menu menu){//加载标题栏menu
        getMenuInflater().inflate(R.menu.toolbar_share,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.share:
                share();
                break;
                default:break;
        }
        return true;
    }

    //进行分享
    private void share(){
        try {
            Bitmap bitmap = ScreenUtils.getBitmapByView(scrollView);
            //String uri = ScreenShootUtils.savePic(bitmap);
            //Log.d(TAG, uri);
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"title",null);
            Uri bitmapUri = Uri.parse(bitmapPath);


            Intent imageIntent = new Intent(Intent.ACTION_SEND);
            imageIntent.setType("image/*");
            imageIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            startActivity(Intent.createChooser(imageIntent, "分享"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
