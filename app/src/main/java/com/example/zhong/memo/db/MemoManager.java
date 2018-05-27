package com.example.zhong.memo.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhong.memo.R;
import com.example.zhong.memo.ShowMemoActivity;
import com.example.zhong.memo.util.MyApplication;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MemoManager {
    private Memo memo;

    private String inputText;

    private String refreshDate;

    private String refreshTime;

    private String scheduleDate;

    private String scheduleTime;

    private String type;

    private String fileName;

    private int count;

    private List<Memo> memoList = new ArrayList<>();

    public MemoManager(Memo memo, String inputText) {
        this.memo = memo;
        this.inputText = inputText;
        this.memoList = DataSupport.findAll(Memo.class);
    }

    public MemoManager(List<Memo> memoList) {
        this.memoList = memoList;
    }

    public MemoManager() {
        this.memoList = DataSupport.findAll(Memo.class);
    }

    public MemoManager(LinearLayout linearLayout) {
        Calendar calendar = Calendar.getInstance();
        TextView textView = linearLayout.findViewById(R.id.input_text);
        inputText = textView.getText().toString();
        textView = linearLayout.findViewById(R.id.refresh_date);
        refreshDate = textView.getText().toString();
        textView = linearLayout.findViewById(R.id.refresh_time);
        refreshTime = textView.getText().toString();
        Spinner spinner = linearLayout.findViewById(R.id.type_choose_spinner);
        CardView scheduleCardView = linearLayout.findViewById(R.id.show_schedule);
        textView = scheduleCardView.findViewById(R.id.show_schedule_date);
        scheduleDate = textView.getText().toString();
        textView = scheduleCardView.findViewById(R.id.show_schedule_time);
        scheduleTime = textView.getText().toString();
        MemoGroup memoGroup = (MemoGroup) spinner.getSelectedItem();
        type = memoGroup.getGroupName();
        memo = new Memo(type, R.drawable.ink);
        memo.setFileName((new Date()).getTime() + "");
        memo.setRefreshDate(calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
                + calendar.get(Calendar.DAY_OF_MONTH));
        memo.setRefreshTime(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        memo.setScheduleDate(scheduleDate + "");
        memo.setScheduleTime(scheduleTime + "");
        if (scheduleCardView.getVisibility() == View.GONE) {
            memo.setScheduleDate("");
            memo.setScheduleTime("");
        }
        memoList = DataSupport.findAll(Memo.class);
        count = memoList.size();
    }

    public MemoManager(LinearLayout linearLayout, String fileName) {
        Calendar calendar = Calendar.getInstance();
        TextView textView = linearLayout.findViewById(R.id.input_text);
        inputText = textView.getText().toString();
        textView = linearLayout.findViewById(R.id.refresh_date);
        refreshDate = textView.getText().toString();
        textView = linearLayout.findViewById(R.id.refresh_time);
        refreshTime = textView.getText().toString();
        Spinner spinner = linearLayout.findViewById(R.id.type_choose_spinner);
        CardView scheduleCardView = linearLayout.findViewById(R.id.show_schedule);
        textView = scheduleCardView.findViewById(R.id.show_schedule_date);
        scheduleDate = textView.getText().toString();
        textView = scheduleCardView.findViewById(R.id.show_schedule_time);
        scheduleTime = textView.getText().toString();
        MemoGroup memoGroup = (MemoGroup) spinner.getSelectedItem();
        type = memoGroup.getGroupName();
        memo = new Memo(type, R.drawable.ink);
        memo.setFileName(fileName);
        memo.setRefreshDate(calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
                + calendar.get(Calendar.DAY_OF_MONTH));
        memo.setRefreshTime(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        memo.setScheduleDate(scheduleDate + "");
        memo.setScheduleTime(scheduleTime + "");
        if (scheduleCardView.getVisibility() == View.GONE) {
            memo.setScheduleDate("");
            memo.setScheduleTime("");
        }
        memoList = DataSupport.findAll(Memo.class);
        count = memoList.size();
    }

    public void saveTxt(String fileName) {
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = MyApplication.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String saveMemo(Context context) {
        memo.save();
        Log.d("saveMemo", "onCreate: memo.getScheduleDate() " + memo.getScheduleDate());
        Log.d("saveMemo", "onCreate: memo.getScheduleTime() " + memo.getScheduleTime());
        saveTxt(memo.getFileName());
        if (!TextUtils.isEmpty(memo.getScheduleDate()) && !TextUtils.isEmpty(memo.getScheduleTime())) {
            addCalendarEvent(context, "便签提醒" + memo.getFileName(), inputText, memo.getScheduleDate(), memo.getScheduleTime());
        }
        Toast.makeText(MyApplication.getContext(), "Saved succeed -" + memo.getFileName(),
                Toast.LENGTH_SHORT).show();
        return memo.getFileName();
    }

    public List<Memo> getMemoList() {
        return memoList;
    }

    public List<MemoGroup> getGroupList() {
        List<MemoGroup> memoGroupList = new ArrayList<>();
        memoList = getMemoList();
        memoGroupList.add(0, new MemoGroup("All", 0));
        for (int i = 0; i < memoList.size(); i++) {
            boolean isRepeated = false;
            for (int j = 0; j < memoGroupList.size(); j++) {//第一个为all
                if (memoList.get(i).getType().equals(memoGroupList.get(j).getGroupName())) {
                    isRepeated = true;
                    break;
                }
            }
            if (!isRepeated && i != 0) {//之前没有这一类，添加
                MemoGroup memoGroup = new MemoGroup(memoList.get(i).getType(), memoList.get(i).getImageId());
                memoGroupList.add(memoGroup);
            }
        }
        return memoGroupList;
    }

    public int getCount() {
        return count;
    }

    public String loadTxt(String fileName) {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = MyApplication.getContext().openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    public void updateMemo(Context context) {
        Log.d("updateMemo", "onCreate: memo.getScheduleDate() " + this.memo
                .getScheduleDate());
        Log.d("updateMemo", "onCreate: memo.getScheduleTime() " + this.memo
                .getScheduleTime());
        this.memo.updateAll("filename = ?", this.memo.getFileName());
        saveTxt(this.memo.getFileName());
        changeCalendarEvent(context);
    }

    public void deleteMemo(Context context) {
        deleteCalendarEvent(context,"便签提醒" + this.memo.getFileName());
        DataSupport.deleteAll(Memo.class, "filename = ?", this.memo.getFileName());
        deleteText(this.memo.getFileName());
    }

    public void deleteMemoList(Context context) {
        for (int i = 0; i < memoList.size(); i++) {
            Memo memo = memoList.get(i);
            deleteCalendarEvent(context,"便签提醒" + memo.getFileName());
            DataSupport.deleteAll(Memo.class, "filename = ?", memo.getFileName());
            deleteText(memo.getFileName());
        }
    }

    private void deleteText(String fileName) {
        try {
            // 找到文件所在的路径并删除该文件
            File file = new File(fileName);
            file.delete();
        } catch (Exception e) {
            Log.d("deleteText", "deleteText failed ");
            e.printStackTrace();
        }
    }

    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";

    //检查是否有现有存在的账户。存在则返回账户id，否则返回-1
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver()
                .query(Uri.parse(CALANDER_URL), null, null,
                        null, null);
        try {
            if (userCursor == null)//查询返回空值
                return -1;
            int count = userCursor.getCount();
            if (count > 0) {//存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    //添加账户。账户创建成功则返回账户id，否则返回-1
    private static String CALENDARS_NAME = "test";
    private static String CALENDARS_ACCOUNT_NAME = "test@gmail.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.exchange";
    private static String CALENDARS_DISPLAY_NAME = "测试账户";

    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars
                .CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALANDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME,
                        CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    //检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    public static void addCalendarEvent(Context context, String title, String description,
                                        String endDate, String endTime) {
        // 获取日历账户的id
        int calId = checkAndAddCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            return;
        }
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        // 插入账户的id
        event.put("calendar_id", calId);
        StringBuilder Year = new StringBuilder();
        StringBuilder Month = new StringBuilder();
        StringBuilder Day = new StringBuilder();
        StringBuilder Hour = new StringBuilder();
        StringBuilder Minute = new StringBuilder();
        int count = 0;
        for (int i = 0; i < endDate.length(); i++) {
            if (count == 0 && endDate.charAt(i) != '/') {
                Year.append(endDate.charAt(i));
            } else if (count == 1 && endDate.charAt(i) != '/') {
                Month.append(endDate.charAt(i));
            } else if (count == 2 && endDate.charAt(i) != '/') {
                Day.append(endDate.charAt(i));
            } else {
                count++;
            }
        }
        count = 0;
        for (int i = 0; i < endTime.length(); i++) {
            if (count == 0 && endTime.charAt(i) != ':') {
                Hour.append(endTime.charAt(i));
            } else if (count == 1 && endTime.charAt(i) != ':') {
                Minute.append(endTime.charAt(i));
            } else {
                count++;
            }
        }
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Integer.parseInt(Year + ""), Integer.parseInt(Month + "") - 1, Integer
                .parseInt(Day + ""), Integer.parseInt(Hour + ""), Integer.parseInt(Minute + ""));
        //设置终止时间
        long end = mCalendar.getTime().getTime();
        //设置开始时间
        long start = Calendar.getInstance().getTime().getTime() - 60 * 60 * 100;
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Beijing");  //这个是时区，必须有，
        //添加事件
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), event);
        if (newEvent == null) {
            // 添加日历事件失败直接返回
            return;
        }
        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        // 提前10分钟有提醒
        values.put(CalendarContract.Reminders.MINUTES, 10);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
        if (uri == null) {
            // 添加闹钟提醒失败直接返回
            return;
        }
    }

    public static void deleteCalendarEvent(Context context, String title) {
        Log.d("changeCalendarEvent", "changeCalendarEvent: 开始删除日程 "+title);
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALANDER_EVENT_URL),
                null, null, null, null);
        try {
            if (eventCursor == null){
                //查询返回空值
                Log.d("changeCalendarEvent", "changeCalendarEvent: 没有找到 "+title);
                return;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor
                        .moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor
                            .getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract
                                .Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALANDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri,
                                null, null);
                        if (rows == -1) {
                            //事件删除失败
                            Log.d("changeCalendarEvent", "changeCalendarEvent: 事件删除失败 "+title);
                            return;
                        }
                        Log.d("changeCalendarEvent", "changeCalendarEvent: 事件删除成功 "+title);
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    private void changeCalendarEvent(Context context) {
        if (TextUtils.isEmpty(memo.getScheduleDate()) || TextUtils.isEmpty(memo.getScheduleTime())) { //取消了日程
            Log.d("changeCalendarEvent", "changeCalendarEvent: 取消了日程");
            deleteCalendarEvent(context, "便签提醒" + memo.getFileName());
        } else if (!TextUtils.isEmpty(memo.getScheduleDate()) && !TextUtils.isEmpty(memo
                .getScheduleTime())) {
            Memo memoSame = new Memo();
            for (int i = 0; i < memoList.size(); i++) {
                if (memoList.get(i).getFileName().equals(memo.getFileName())) {
                    memoSame = memoList.get(i);
                }
            }
            if (memoSame.getScheduleDate().equals(memo.getScheduleDate()) && memoSame.getScheduleTime()
                    .equals(memo.getRefreshTime())) {//日程不变
                Log.d("changeCalendarEvent", "changeCalendarEvent: 日程不变");
            } else {//日程发生了改变，先删除，后添加
                Log.d("changeCalendarEvent", "changeCalendarEvent: 日程发生了改变，先删除，后添加");
                deleteCalendarEvent(context, "便签提醒" + memo.getFileName());
                addCalendarEvent(context,"便签提醒" + memo.getFileName(),inputText,
                        memo.getScheduleDate(),memo.getScheduleTime());
            }
        }
    }

}
