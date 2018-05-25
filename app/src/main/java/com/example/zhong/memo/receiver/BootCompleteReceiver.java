package com.example.zhong.memo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.zhong.memo.Services.ScheduleService;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        /*Toast.makeText(context,"Boot Complete",Toast.LENGTH_LONG).show();
        Intent startIntent = new Intent(context, ScheduleService.class);
        context.startService(startIntent);*/
    }
}
