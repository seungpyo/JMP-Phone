package com.example.jmpphone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    public static String myNumber;

    private final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
    };
    private final static int PERMISSIONS_REQUEST_CODE = 10;
    private final static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 11;

    Button serviceLaunchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Main Activity Started");

        Boolean gotAllPermissions = true;
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(), permission) != PackageManager.PERMISSION_DENIED) {
                gotAllPermissions = false;
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
        if (gotAllPermissions) {
            getMyNumber();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted");

                    getMyNumber();

                } else {
                    Log.d(TAG, "Permission denied");
                }
                return;
            }
        }
    }

    private void getMyNumber() {
        TelephonyManager tmgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            myNumber = tmgr.getLine1Number();
            Log.d(TAG, "My Number is: " + myNumber);
        } catch (SecurityException e) {
            Toast.makeText(this, "앱 권한 확인", Toast.LENGTH_LONG);
            Log.e(TAG, e.toString());
        }
    }




}
