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
        } else if (!intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)){
            Log.d(TAG, "No Incoming Number!");
            return;
        } else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, intent, state, number);
        }
    }



    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        PhoneCallRecord record = new PhoneCallRecord(
                ctx, number, start, PhoneCallRecord.Direction.INCOMING, PhoneCallRecord.Startend.START);
        Log.d(TAG, record.toString());
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d(TAG, "onOutgoingCallStarted got number " + number);

        PhoneCallRecord record = new PhoneCallRecord(
                ctx, number, start, PhoneCallRecord.Direction.OUTGOING, PhoneCallRecord.Startend.START);
        Log.d(TAG, record.toString());
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        PhoneCallRecord record = new PhoneCallRecord(
                ctx, number, start, PhoneCallRecord.Direction.INCOMING, PhoneCallRecord.Startend.END);
        Log.d(TAG, record.toString());
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        PhoneCallRecord record = new PhoneCallRecord(
                ctx, number, start, PhoneCallRecord.Direction.OUTGOING, PhoneCallRecord.Startend.END);
        Log.d(TAG, record.toString());
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
        PhoneCallRecord record = new PhoneCallRecord(
                ctx, number, start, PhoneCallRecord.Direction.INCOMING, PhoneCallRecord.Startend.MISSED);
        Log.d(TAG, record.toString());
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, Intent intent, int state, String number) {


        if(lastState == state){
            //No change, debounce extras
            return;
        }

        Log.d(TAG, "onCallStateChanged got number " + number);

        // It seems like every case needs number as savedNumber.
        savedNumber = number;

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                // savedNumber = number;
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