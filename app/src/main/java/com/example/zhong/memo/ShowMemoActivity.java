package com.example.zhong.memo;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhong.memo.db.GroupManager;
import com.example.zhong.memo.db.Memo;
import com.example.zhong.memo.db.MemoGroup;
import com.example.zhong.memo.db.MemoManager;
import com.example.zhong.memo.ui.Album;
import com.example.zhong.memo.ui.ImageAndText;
import com.example.zhong.memo.ui.ImageUtils;
import com.example.zhong.memo.ui.ReturnToImage;
import com.example.zhong.memo.ui.ScreenUtils;
import com.example.zhong.memo.util.MyApplication;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;


public class ShowMemoActivity extends AppCompatActivity {

    private static final String TAG = "ShowMemoActivity";

    private Spinner spinner;

    private Calendar calendar;

    private  DatePicker datePicker;

    private TimePicker timePicker;

    private CardView scheduleCardView;

    private StringBuilder date;

    private StringBuilder time;

    private Toolbar toolbar;

    private int originalSelection;

    private LinearLayout linearLayout;

    private String fileName;

    private boolean isUnderGroupView;

    private EditText editText;

    public static final int TAKE_PHOTO = 2;

    public static final int CHOOSE_PHOTO = 3;

    private Uri imageUri;

    private EditText content;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        toolbar = findViewById(R.id.toolbar);
        linearLayout = findViewById(R.id.memo_view);
        //toolbar.setLogo(R.drawable.apple_pic);
        final Intent intent = getIntent();
        Memo memo = (Memo)intent.getSerializableExtra("memo_data");
        isUnderGroupView = intent.getBooleanExtra("isUnderGroupView",false);
        String title = memo.getType();
        String refreshDate = memo.getRefreshDate();
        String refreshTime = memo.getRefreshTime();
        fileName = memo.getFileName();
        MemoManager memoManager = new MemoManager();
        String content = new String(memoManager.loadTxt(fileName));
        TextView dateView = findViewById(R.id.refresh_date);
        dateView.setText(refreshDate);
        Log.d(TAG, "onCreate: refreshDate"+refreshDate+dateView);
        dateView.setText(refreshDate);
        TextView timeView = findViewById(R.id.refresh_time);
        Calendar calendar = Calendar.getInstance();
        String today = calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"
                +calendar.get(Calendar.DAY_OF_MONTH);
        if(today.equals(refreshDate)){//如果是当天，就只显示时间
            timeView .setText(refreshTime);
        }else {
            timeView .setText(refreshDate);
        }
        editText =findViewById(R.id.input_text);
        ReturnToImage.initContent(content,editText);//还原为图文混排
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){//
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //设置标题栏
        String Date = intent.getStringExtra("Date");
        TextView dateText = findViewById(R.id.refresh_date);
        dateText.setText(Date);
        //Spinner init
        spinner  = findViewById(R.id.type_choose_spinner);
        GroupManager groupManager = new GroupManager(spinner);
        groupManager.refreshSpinner();
        List<MemoGroup> memoGroupList = groupManager.getGroupList();
        int k = groupManager.getCount();
        originalSelection  = 0;
        for(int i=0;i<k;i++){
            if(title.equals(memoGroupList.get(i).getGroupName())){
                //Log.d("ShowMemoActivity", "onCreate: "+spinnerAdapter.getItem(i).getGroupName());
                spinner.setSelection(i,true);
                originalSelection = i;
                break;
            }
        }
        //新建分组
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GroupManager groupManager = new GroupManager(spinner);
                int countBefore = groupManager.getGroupList().size();//执行添加之前item数目
                if(position == countBefore  -1){
                    showDialogAddGroup();//执行添加操作
                }else{
                    toolbar.setTitle(groupManager.getGroupList().get(position).getGroupName());
                    originalSelection = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ScrollView scrollView = findViewById(R.id.scroll_view);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText inputText = findViewById(R.id.input_text);
                inputText.setFocusableInTouchMode(true);
                inputText.setFocusable(true);
                inputText.requestFocus();
                Log.d("onClick", "onClick: in scrollView");
                return false;
            }
        });
        scheduleCardView = findViewById(R.id.show_schedule);
        if(!TextUtils.isEmpty(memo.getScheduleDate())&&!TextUtils.isEmpty(memo.getScheduleTime())){
            scheduleCardView.setVisibility(View.VISIBLE);
            Log.d(TAG, "onCreate: memo.getScheduleDate() "+ memo.getScheduleDate());
            TextView scheduledDateText = scheduleCardView.findViewById(R.id.show_schedule_date);
            scheduledDateText.setText(memo.getScheduleDate());
            Log.d(TAG, "onCreate: memo.getScheduleTime() "+memo.getScheduleTime());
            TextView scheduledTimeText = scheduleCardView.findViewById(R.id.show_schedule_time);
            scheduledTimeText.setText(memo.getScheduleTime());
        }
        scheduleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               changeSchedule();
            }
        });
        ImageView addPhoto = findViewById(R.id.add_photo);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddPhoto();
            }
        });
        ImageView shareView = findViewById(R.id.add_share);
        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(ShowMemoActivity.this,ShareActivity.class);
                intent.putExtra("file_name",fileName);
                startActivity(intent);
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu){//加载标题栏menu
        getMenuInflater().inflate(R.menu.toolbar_showmemoactivity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("date_return_type", toolbar.getTitle()+"");
                intent.putExtra("isUnderGroupView",isUnderGroupView);
                setResult(RESULT_OK,intent);
                Log.d(TAG,"onDestroy");
                finish();
                break;
            case R.id.save:{
                Toast.makeText(ShowMemoActivity.this,"You clicked update_memo",
                        Toast.LENGTH_SHORT).show();
                MemoManager memoManager = new MemoManager(linearLayout,fileName);
                memoManager.updateMemo(ShowMemoActivity.this);
            }
            break;
            case R.id.delete_memo:{
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShowMemoActivity.this);
                dialog.setMessage("删除便签");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MemoManager memoManager = new MemoManager(linearLayout,fileName);
                        memoManager.deleteMemo(ShowMemoActivity.this);
                        finish();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }
            break;
            case R.id.set_notice:{//日程提醒
                requestRuntimePermissions();
            }
        }
        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public DatePicker onYearMonthDayPicker(View view, Calendar calendar) {
        calendar = Calendar.getInstance();
        final DatePicker picker = new DatePicker(this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        picker.setRangeEnd(2111, 1, 11);
        picker.setRangeStart(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.setSelectedItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                showToast(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
        return  picker;
    }


    public TimePicker onTimePicker(View view,Calendar calendar) {
        calendar = Calendar.getInstance();
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setUseWeight(false);
        picker.setCycleDisable(false);
        picker.setRangeStart(0, 0);//00:00
        picker.setRangeEnd(23, 59);//23:59
        picker.setSelectedItem(calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        picker.setSelectedItem(currentHour, currentMinute);
        picker.setTopLineVisible(false);
        picker.setTextPadding(ConvertUtils.toPx(this, 15));
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                showToast(hour + ":" + minute);
            }
        });
        picker.show();
        return picker;
    }

    public void showDialog(){
        calendar = Calendar.getInstance();
        AlertDialog.Builder dialog = new AlertDialog.Builder(ShowMemoActivity.this);
        dialog.setTitle("设置提醒");
        dialog.create();
        dialog.setCancelable(true);
        dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setNegativeButton("取消提醒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alertdialog_diy, null);
        dialog.setView(view);
        dialog.show();
    }

    public void showDialogAddGroup(){
        calendar = Calendar.getInstance();
        //textView = scheduleCardView.findViewById(R.id.show_schedule_text);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowMemoActivity.this);
        builder.setTitle("分组");
        builder.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.alertdialog_diy_add_group, null);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", null );
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.group_new_name);
                String inputText = editText.getText().toString();
                if (TextUtils.isEmpty(inputText)){//组名不能为空
                    Toast.makeText(ShowMemoActivity.this,"输入不能为空",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    GroupManager groupManager = new GroupManager(spinner);
                    if(!groupManager.addNewGroup(inputText)){//添加失败
                        Toast.makeText(ShowMemoActivity.this,"添加失败"+inputText,
                                Toast.LENGTH_SHORT).show();
                        spinner.setSelection(originalSelection,true);
                        dialog.dismiss();
                    }else{//添加成功
                        groupManager = new GroupManager(spinner);
                        int countAfter = groupManager.getCount();
                        groupManager.refreshSpinner();
                        toolbar.setTitle(inputText);
                        spinner.setSelection(countAfter-2,true);
                        Toast.makeText(ShowMemoActivity.this,"添加成功"+inputText,
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setSelection(originalSelection,true);
                dialog.dismiss();
            }
        });
    }

    public void setSchedule(){
        calendar = Calendar.getInstance();
        final TextView dateView = scheduleCardView.findViewById(R.id.show_schedule_date);
        final TextView timeView = scheduleCardView.findViewById(R.id.show_schedule_time);
        AlertDialog.Builder dialog = new AlertDialog.Builder(ShowMemoActivity.this);
        dialog.setTitle("设置提醒");
        dialog.create();
        dialog.setCancelable(true);
        dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scheduleCardView.setVisibility(View.VISIBLE);
                dateView.setText(date);
                timeView.setText(time);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alertdialog_diy, null);
        dialog.setView(view);
        dialog.show();
        final TextView showDateText = view.findViewById(R.id.show_date);
        showDateText.setText(calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+
                "/"+calendar.get(Calendar.DAY_OF_MONTH));
        date = new StringBuilder(calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+
                "/"+calendar.get(Calendar.DAY_OF_MONTH));
        RelativeLayout dialogDateChoose = view.findViewById(R.id.dialog_date_choose);
        dialogDateChoose.setOnClickListener(new View.OnClickListener() {//设置日期
            @Override
            public void onClick(View v) {
                datePicker = onYearMonthDayPicker(v,calendar);
                datePicker.getSubmitButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateText.setText(datePicker.getSelectedYear()+"/"+datePicker
                                .getSelectedMonth()+"/"+datePicker.getSelectedDay());
                        date = new StringBuilder(datePicker.getSelectedYear()+"/"+datePicker
                                .getSelectedMonth()+"/"+datePicker.getSelectedDay());
                        datePicker.dismiss();
                    }
                });
            }
        });
        final TextView showTimeText = view.findViewById(R.id.show_time);
        showTimeText.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar
                .get(Calendar.MINUTE));
        time = new StringBuilder(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar
                .get(Calendar.MINUTE));
        RelativeLayout dialogTimeChoose = view.findViewById(R.id.dialog_time_choose);
        dialogTimeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//设置时间
                timePicker=onTimePicker(v,calendar);
                timePicker.getSubmitButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimeText.setText(timePicker.getSelectedHour()+":"+timePicker
                                .getSelectedMinute());
                        time = new StringBuilder(timePicker.getSelectedHour()+":"+timePicker
                                .getSelectedMinute());
                        timePicker.dismiss();
                    }
                });
            }
        });
    }

    public void changeSchedule(){
        calendar = Calendar.getInstance();
        final TextView dateView = scheduleCardView.findViewById(R.id.show_schedule_date);
        final TextView timeView = scheduleCardView.findViewById(R.id.show_schedule_time);
        AlertDialog.Builder dialog = new AlertDialog.Builder(ShowMemoActivity.this);
        dialog.setTitle("设置提醒");
        dialog.create();
        dialog.setCancelable(true);
        dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scheduleCardView.setVisibility(View.VISIBLE);
                dateView.setText(date);
                timeView.setText(time);
            }
        });
        dialog.setNegativeButton("取消提醒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scheduleCardView.setVisibility(View.GONE);
            }
        });
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alertdialog_diy, null);
        dialog.setView(view);
        dialog.show();
        final TextView showDateText = view.findViewById(R.id.show_date);
        showDateText.setText(dateView.getText().toString());
        date = new StringBuilder(dateView.getText().toString());
        RelativeLayout dialogDateChoose = view.findViewById(R.id.dialog_date_choose);
        dialogDateChoose.setOnClickListener(new View.OnClickListener() {//设置日期
            @Override
            public void onClick(View v) {
                datePicker = onYearMonthDayPicker(v,calendar);
                datePicker.getSubmitButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateText.setText(datePicker.getSelectedYear()+"/"+datePicker
                                .getSelectedMonth()+"/"+datePicker.getSelectedDay());
                        date = new StringBuilder(datePicker.getSelectedYear()+"/"+datePicker
                                .getSelectedMonth()+"/"+datePicker.getSelectedDay());
                        datePicker.dismiss();
                    }
                });
            }
        });
        final TextView showTimeText = view.findViewById(R.id.show_time);
        showTimeText.setText(timeView.getText().toString());
        time = new StringBuilder(timeView.getText().toString());
        RelativeLayout dialogTimeChoose = view.findViewById(R.id.dialog_time_choose);
        dialogTimeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//设置时间
                timePicker=onTimePicker(v,calendar);
                timePicker.getSubmitButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimeText.setText(timePicker.getSelectedHour()+":"+timePicker
                                .getSelectedMinute());
                        time = new StringBuilder(timePicker.getSelectedHour()+":"+timePicker
                                .getSelectedMinute());
                        timePicker.dismiss();
                    }
                });
            }
        });
    }

    protected void onStart(){
        super.onStart();
        LitePal.getDatabase();
        Log.d(TAG,"onStart");
        MemoManager memoManager = new MemoManager();
        List<MemoGroup> memoGroupList = memoManager.getGroupList();
        for (MemoGroup memoGroup : memoGroupList){
            Log.d(TAG, "onStart: memoGroup name is "+ memoGroup.getGroupName());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG,"onRestart");
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("date_return_type", toolbar.getTitle()+"");
        intent.putExtra("isUnderGroupView",isUnderGroupView);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void requestRuntimePermissions(){
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(ShowMemoActivity.this, Manifest.permission
                .READ_CALENDAR)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_CALENDAR);
        }
        if(ContextCompat.checkSelfPermission(ShowMemoActivity.this, Manifest.permission
                .WRITE_CALENDAR)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_CALENDAR);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(ShowMemoActivity.this,permissions,1);
        }else{
            setSchedule();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permission,
                                           int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    setSchedule();
                }else{
                    Toast.makeText(this,"必须同意才能使用日程提醒功能",
                            Toast.LENGTH_SHORT).show();
                }
            case CHOOSE_PHOTO:{
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Album.openAlbum(ShowMemoActivity.this);
                }else{
                    Toast.makeText(this,"必须同意才能添加图片",
                            Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:break;
        }
    }

    public void showDialogAddPhoto(){//添加图片,拍照或选则
        //textView = scheduleCardView.findViewById(R.id.show_schedule_text);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowMemoActivity.this);
        builder.setTitle("设置提醒");
        builder.create();
        builder.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alertdialog_add_photo, null);
        builder.setView(view);
        final AlertDialog dialog = builder.show();
        TextView takePhoto = view.findViewById(R.id.take_photo);
        TextView choosePhoto = view.findViewById(R.id.from_album);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //File obj to store picture
                File outputImage = new File(getExternalCacheDir(),(new Date()).getTime()+".jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >=24){//Android 7.0 and above
                    imageUri = FileProvider.getUriForFile(ShowMemoActivity.this,
                            "com.example.zhong.memo.fileprovider",outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
                dialog.dismiss();
            }
        });
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ShowMemoActivity.this, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ShowMemoActivity.this, new
                            String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},CHOOSE_PHOTO);
                }else {
                    Album.openAlbum(ShowMemoActivity.this);//打开相册
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("onActivityResult", "onActivityResult: 返回结果");
        switch(requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    //take out the photo
                    String s = imageUri+"";
                    String path = s.replace("file://","");
                    ImageAndText.displayImageAtCursor(path,editText);
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    //mobile version
                    if(Build.VERSION.SDK_INT >= 19){//Above android 4.4
                        String imagePath = Album.handleImageOnKitKat(data);
                        Log.d("onActivityResult", "onActivityResult: 开始插入图片"+imagePath);
                        ImageAndText.displayImageAtCursor(imagePath,editText);//插入图片
                    }else{
                        String imagePath = Album.handleImageBeforeKitKat(data);
                        Log.d("onActivityResult", "onActivityResult: 开始插入图片"+imagePath);
                        ImageAndText.displayImageAtCursor(imagePath,editText);
                    }
                }
                break;
            default:
                break;
        }
    }

}
