package com.example.jmpphone;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class PhonecallRecordSendTask extends AsyncTask<Void, Void, Integer> {

    private static final String TAG = "PhonecallRecordSendTask";

    private String baseUrl;
    private PhonecallRecord record;
    private HttpURLConnection conn;

    public PhonecallRecordSendTask(String url, PhonecallRecord record) {

        this.baseUrl = url;
        this.record = record;

        Log.d(TAG, this.record.toString());

    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            URL url = new URL(this.baseUrl + this.record.toString());

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            return conn.getResponseCode();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return HttpURLConnection.HTTP_BAD_REQUEST;
        } catch (IOException e) {
            e.printStackTrace();
            return HttpURLConnection.HTTP_NOT_FOUND;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);
        Log.d(TAG, "Finished sending phone call record to server.");
    }

}
