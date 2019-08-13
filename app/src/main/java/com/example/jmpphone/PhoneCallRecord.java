package com.example.jmpphone;

import android.telephony.TelephonyManager;
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
    private String name = null;
    private long timestamp;
    private Direction direction;
    private Startend startend;


    public PhoneCallRecord(String number, long timestamp, Direction direction, Startend startend) {

        Log.d(TAG, "Constructor got number : " + number);

        this.myNumber = MainActivity.myNumber;

        this.number = number;
        this.timestamp = timestamp;
        this.direction = direction;
        this.startend = startend;
    }

    public PhoneCallRecord(String number, Date date, Direction direction, Startend startend) {
        this(number, date.getTime(), direction, startend);
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
