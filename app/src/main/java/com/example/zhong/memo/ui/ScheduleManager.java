package com.example.zhong.memo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhong.memo.R;
import com.example.zhong.memo.ShowMemoActivity;
import com.example.zhong.memo.util.MyApplication;

import java.util.Calendar;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;

/**
 * Created by DELL on 2018/5/20.
 */

public class ScheduleManager {
    private CardView scheduleCardView ;

    private Calendar calendar;

    private StringBuilder date;

    private TextView textView ;

    private StringBuilder time;

    private DatePicker datePicker;

    private TimePicker timePicker;

    private AppCompatActivity activity;
    public ScheduleManager(CardView scheduleCardView,AppCompatActivity activity){
        this.scheduleCardView = scheduleCardView;
        calendar = Calendar.getInstance();
        date = new StringBuilder();
        //textView = scheduleCardView.findViewById(R.id.show_schedule_text);
        time = new StringBuilder();
        this.activity = activity;
    }

    public CardView getScheduleCardView() {
        return scheduleCardView;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setSchedule(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyApplication.getContext());
        dialog.setTitle("设置提醒");
        dialog.create();
        dialog.setCancelable(true);
        dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scheduleCardView.setVisibility(View.VISIBLE);
                //isNeedSchedule = true;
                textView.setText(date+" "+time);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        LayoutInflater inflater = LayoutInflater.from(MyApplication.getContext());
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

    private TimePicker onTimePicker(View view, Calendar calendar) {
        TimePicker picker = new TimePicker(activity, TimePicker.HOUR_24);
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
        picker.setTextPadding(ConvertUtils.toPx(MyApplication.getContext(), 15));
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                showToast(hour + ":" + minute);
            }
        });
        picker.show();
        return picker;
    }

    public DatePicker onYearMonthDayPicker(View view, Calendar calendar) {
        final DatePicker picker = new DatePicker(activity);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(activity, 10));
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

    private void showToast(String msg) {
        Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
