package com.example.zhong.memo;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.provider.DocumentsContract;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhong.memo.db.MemoManager;
import com.example.zhong.memo.db.GroupManager;
import com.example.zhong.memo.ui.Album;
import com.example.zhong.memo.ui.ImageAndText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;

public class MemoActivity extends AppCompatActivity {

    private StringBuilder date;

    private StringBuilder time;

    private Calendar calendar ;

    private Spinner spinner;

    private LinearLayout linearLayout;

    private Toolbar toolbar;

    private int originalSelection;

    private CardView scheduleCardView;

    private DatePicker datePicker;

    private TimePicker timePicker;

    boolean isSaved;

    String fileName;

    public static final int CALENDAR_EVENT = 1;

    public static final int TAKE_PHOTO = 2;

    public static final int CHOOSE_PHOTO = 3;

    private Uri imageUri;

    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        linearLayout = findViewById(R.id.memo_view);
        toolbar = findViewById(R.id.toolbar);
        content = findViewById(R.id.input_text);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //初始化数据spinner
        spinner  = findViewById(R.id.type_choose_spinner);
        GroupManager groupManager = new GroupManager(spinner);
        groupManager.refreshSpinner();
        originalSelection = 0;
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
        calendar = Calendar.getInstance();
        TextView timeText = findViewById(R.id.refresh_time);
        String hour,minute;
        hour = calendar.get(Calendar.HOUR_OF_DAY)+"";
        if(calendar.get(Calendar.HOUR_OF_DAY)<10){
            hour = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        }
        minute = calendar.get(Calendar.MINUTE)+"";
        if(calendar.get(Calendar.MINUTE)<10){
            minute = "0"+calendar.get(Calendar.MINUTE);
        }
        timeText.setText(hour+":"+minute);
        /*TextView dateView = findViewById(R.id.refresh_date);
        dateView.setText(calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar
        .get(Calendar.DAY_OF_MONTH));*/
        EditText inputText = findViewById(R.id.input_text);
        inputText.setFocusableInTouchMode(true);
        inputText.setFocusable(true);
        inputText.requestFocus();
        scheduleCardView = findViewById(R.id.show_schedule);
        //textView = scheduleCardView.findViewById(R.id.show_schedule_text);
        scheduleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               changeSchedule();
            }
        });
        isSaved = false;
        ImageView addPhoto = findViewById(R.id.add_photo);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddPhoto();
            }
        });
        ImageView textT = findViewById(R.id.edit_text);
        textT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_memoactivity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.delete_memo:{
                AlertDialog.Builder dialog = new AlertDialog.Builder(MemoActivity.this);
                dialog.setMessage("删除便签");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MemoManager memoManager = new MemoManager(linearLayout,fileName);
                        memoManager.deleteMemo(MemoActivity.this);
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
            case android.R.id.home:
                finish();
                break;
            case R.id.set_notice:{
                requestRuntimePermissions();
                break;
            }
                case R.id.save_memo:{
                    if(!isSaved){//新建
                        MemoManager memoManager = new MemoManager(linearLayout);
                        fileName = memoManager.saveMemo(MemoActivity.this);
                        isSaved = true;
                    }else{//修改
                        MemoManager memoManager = new MemoManager(linearLayout,fileName);
                        memoManager.updateMemo(MemoActivity.this);
                    }
                }
            default:break;
        }
        return true;
    }

    public void showDialogAddGroup(){
        calendar = Calendar.getInstance();
        //textView = scheduleCardView.findViewById(R.id.show_schedule_text);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MemoActivity.this);
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
                    Toast.makeText(MemoActivity.this,"输入不能为空",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    GroupManager groupManager = new GroupManager(spinner);
                    if(!groupManager.addNewGroup(inputText)){//添加失败
                        Toast.makeText(MemoActivity.this,"添加失败"+inputText,
                                Toast.LENGTH_SHORT).show();
                        spinner.setSelection(originalSelection,true);
                        dialog.dismiss();
                    }else{//添加成功
                        groupManager = new GroupManager(spinner);
                        int countAfter = groupManager.getCount();
                        groupManager.refreshSpinner();
                        toolbar.setTitle(inputText);
                        spinner.setSelection(countAfter-2,true);
                        Toast.makeText(MemoActivity.this,"添加成功"+inputText,
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

    public void showDialogAddPhoto(){//添加图片
        //textView = scheduleCardView.findViewById(R.id.show_schedule_text);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MemoActivity.this);
        dialog.setTitle("设置提醒");
        dialog.create();
        dialog.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alertdialog_add_photo, null);
        dialog.setView(view);
        dialog.show();
        TextView takePhoto = view.findViewById(R.id.take_photo);
        TextView choosePhoto = view.findViewById(R.id.from_album);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //File obj to store picture
                File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >=24){//Android 7.0 and above
                    imageUri = FileProvider.getUriForFile(MemoActivity.this,
                            "com.example.zhong.memo.fileprovider",outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MemoActivity.this, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MemoActivity.this, new
                            String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},CHOOSE_PHOTO);
                }else {
                    Album.openAlbum(MemoActivity.this);//打开相册
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("onActivityResult", "onActivityResult: 返回结果");
        switch(requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        //take out the photo
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        //picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    //mobile version
                    if(Build.VERSION.SDK_INT >= 19){//Above android 4.4
                        Log.d("onActivityResult", "onActivityResult: 开始插入图片");
                        String imagePath = Album.handleImageOnKitKat(data);
                        ImageAndText.displayImageAtCursor(imagePath,content);
                    }else{
                        Log.d("onActivityResult", "onActivityResult: 开始插入图片");
                        String imagePath = Album.handleImageBeforeKitKat(data);
                        ImageAndText.displayImageAtCursor(imagePath,content);
                    }
                }
                break;
            default:
                break;
        }
    }


    public DatePicker onYearMonthDayPicker(View view, Calendar calendar) {
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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void changeSchedule(){
        final TextView dateView = scheduleCardView.findViewById(R.id.show_schedule_date);
        final TextView timeView = scheduleCardView.findViewById(R.id.show_schedule_time);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MemoActivity.this);
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

    private void requestRuntimePermissions(){
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MemoActivity.this, Manifest.permission
                .READ_CALENDAR)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_CALENDAR);
        }
        if(ContextCompat.checkSelfPermission(MemoActivity.this, Manifest.permission
                .WRITE_CALENDAR)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_CALENDAR);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MemoActivity.this,permissions,1);
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
                break;
            case CHOOSE_PHOTO:{
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Album.openAlbum(MemoActivity.this);
                }else{
                    Toast.makeText(this,"必须同意才能添加图片",
                            Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:break;
        }
    }

    private void setSchedule(){
        calendar = Calendar.getInstance();
        final TextView dateView = scheduleCardView.findViewById(R.id.show_schedule_date);
        final TextView timeView = scheduleCardView.findViewById(R.id.show_schedule_time);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MemoActivity.this);
        dialog.setTitle("设置提醒");
        dialog.create();
        dialog.setCancelable(true);
        dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scheduleCardView.setVisibility(View.VISIBLE);
                dateView.setText(date);
                timeView.setText(time);
                //addCalendarEvent(MemoActivity.this,"Test","Test",date+"",time+"");
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

}


