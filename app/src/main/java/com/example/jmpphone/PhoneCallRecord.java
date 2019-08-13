package com.example.jmpphone;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;

public class PhoneCallRecord implements Serializable {

    private final String TAG = "PhoneCallRecord";

    public static enum Direction {
        OUTGOING, INCOMING
    };

    public static enum Startend {
        START, END, MISSED
    };

    private String myNumber;
    private String number;
    private String name;
    private long timestamp;
    private Direction direction;
    private Startend startend;
    private Context ctx;


    public PhoneCallRecord(Context ctx, String number, long timestamp, Direction direction, Startend startend) {

        Log.d(TAG, "Constructor got number : " + number);

        this.ctx = ctx;

        this.myNumber = MainActivity.myNumber;

        this.number = number;
        this.timestamp = timestamp;
        this.direction = direction;
        this.startend = startend;

        // 어차피 전화 자체는 한 번에 한 통만 받을 수 있으므로, 굳이 AsyncTask 로 만들 이유가 없음.
        this.name = getContactDisplayNameByNumber(number);

    }

    public PhoneCallRecord(Context ctx, String number, Date date, Direction direction, Startend startend) {
        this(ctx, number, date.getTime(), direction, startend);
    }

    private static final String[] CONTACT_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

    private String getContactDisplayNameByNumber(String number) {
        String name = "UNKNOWN";

        Uri uri = Uri.withAppendedPath(
                ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(number));

        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(uri,
                CONTACT_PROJECTION, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            }
            cursor.close();
        }
        return name;
    }


    @Override
    public String toString() {
        Date date = new Date();
        date.setTime(this.timestamp);

        String repr = "";

        repr += "PHONE NO. " + myNumber + "\n";

        if (startend == Startend.START) {
            repr += "Start ";
        } else {
            repr += "End ";
        }

        if (direction == Direction.INCOMING) {
            repr += "incoming call from\n";
        } else {
            repr += "outgoing call to\n";
        }

        repr += "number: " + number + ", ";

        if (name != null) {
            repr += "(name: " + name + "), ";
        } else {
            repr += "(name: " + "Unknown" + "), ";
        }

        repr += "at date:" + date.toString();


        return repr;
    }

}
