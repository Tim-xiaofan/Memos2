package com.example.zhong.memo.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.zhong.memo.R;
import com.example.zhong.memo.util.MyApplication;

import java.util.Calendar;

public class ScheduleService extends Service {
    public ScheduleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //注册广播
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        Log.d("ScheduleService", "onStartCommand: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                NotificationManager manager = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getContext());
                builder.setContentTitle("Time change");
                builder.setContentText((Calendar.getInstance()).toString());
                builder.setWhen(System.currentTimeMillis());
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher));
                Notification notification = builder.build();
                manager.notify(1,notification);
            }
        }).start();
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int author = 1000*10;
        long triggerAtTime = SystemClock.elapsedRealtime()+author;
        Intent i = new Intent(this, ScheduleService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("ScheduleService", "onDestroy: ");
    }


}
