package com.example.jmpphone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LoggerService extends Service {

    private final String TAG = "LoggerService";

    @Override
    public IBinder onBind(Intent intent) {

        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        String startend = intent.getStringExtra(PhonecallReceiver.STARTEND);
        String direction = intent.getStringExtra(PhonecallReceiver.DIRECTION);
        String date = intent.getStringExtra(PhonecallReceiver.DATE);
        String number = intent.getStringExtra(PhonecallReceiver.NUMBER);

        Log.d(TAG, startend);
        Log.d(TAG, direction);
        Log.d(TAG, date);
        Log.d(TAG, number);

        return START_REDELIVER_INTENT;
    }
}
