/**
 * https://stackoverflow.com/questions/33889436/how-to-detect-a-out-going-phone-call-is-received-in-android
 */

package com.example.jmpphone;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhonecallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static String TAG = "PhonecallReceiver";

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive");

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, intent, state, number);
        }
    }

    //Derived classes should override these to respond to specific events of interest
    public static final String DIRECTION = "DIRECTION";
    public static final String OUTGOING = "OUTGOING";
    public static final String INCOMING = "INCOMING";

    public static final String STARTEND = "STARTEND";
    public static final String START = "START";
    public static final String END = "END";
    public static final String MISSED = "MISSED";


    public static final String NUMBER = "NUMBER";
    public static final String DATE = "DATE";


    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "Call from:" + number, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Call from: " + number + ", starting at " + start.toString());
        Intent intent = new Intent(ctx, LoggerService.class);
        intent.putExtra(DIRECTION, INCOMING);
        intent.putExtra(STARTEND, START);
        intent.putExtra(NUMBER, number);
        intent.putExtra(DATE, start.getTime());
        ctx.startService(intent);
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d(TAG, "Call to: " + number + ", starting at " + start.toString());
        Intent intent = new Intent(ctx, LoggerService.class);
        intent.putExtra(DIRECTION, OUTGOING);
        intent.putExtra(STARTEND, START);
        intent.putExtra(NUMBER, number);
        intent.putExtra(DATE, start.getTime());
        ctx.startService(intent);
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(TAG, "Call from: " + number + ", ending at " + start.toString());
        Intent intent = new Intent(ctx, LoggerService.class);
        intent.putExtra(DIRECTION, INCOMING);
        intent.putExtra(STARTEND, END);
        intent.putExtra(NUMBER, number);
        intent.putExtra(DATE, end.getTime());
        ctx.startService(intent);
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(TAG, "Call to: " + number + ", ending at " + start.toString());
        Intent intent = new Intent(ctx, LoggerService.class);
        intent.putExtra(DIRECTION, OUTGOING);
        intent.putExtra(STARTEND, END);
        intent.putExtra(NUMBER, number);
        intent.putExtra(DATE, end.getTime());
        ctx.startService(intent);
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d(TAG, "Call from: " + number + ", missed at " + start.toString());
        Intent intent = new Intent(ctx, LoggerService.class);
        intent.putExtra(DIRECTION, INCOMING);
        intent.putExtra(STARTEND, MISSED);
        intent.putExtra(NUMBER, number);
        intent.putExtra(DATE, start.getTime());
        ctx.startService(intent);
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, Intent intent, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                }
                else if(isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                else{
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }
}