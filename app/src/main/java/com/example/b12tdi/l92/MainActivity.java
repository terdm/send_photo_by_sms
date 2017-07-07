package com.example.b12tdi.l92;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    final String LOG_TAG = "myLogs";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //onClickPhotoStart(findViewById(R.id.btnPhotoStart));

    }


    public void onClickStart(View v) {
        Log.d(LOG_TAG, " onClickStart  initializeSmsRadarService  ");
            //startService(new Intent(this, SendMailIntentService.class));
        initializeSmsRadarService();
        Log.d(LOG_TAG, " onClickStart  initializeSmsRadarService ends ");
    }

    public void onClickStop(View v) {
        Log.d(LOG_TAG, " onClickStop  stopSmsRadarService  ");
        //stopService(new Intent(this, SendMailIntentService.class));
        stopSmsRadarService();
        Log.d(LOG_TAG, " onClickStop  stopSmsRadarService  ends");
    }

    public void onClickPhotoStart(View v) {
        startService(new Intent(this, PhotoTakingService.class));

    }


    public void onClickPhotoStop(View v) {
        stopService(new Intent(this, PhotoTakingService.class));
    }

    private void initializeSmsRadarService() {
        SmsRadar.initializeSmsRadarService(this, new SmsListener() {
            @Override
            public void onSmsSent(Sms sms) {
                showSmsToast(sms);
                Log.d(LOG_TAG,"sent sms" +  sms.toString());
            }

            @Override
            public void onSmsReceived(Sms sms) {
                showSmsToast(sms);
                Log.d(LOG_TAG,"received sms" +  sms.toString());
                stopService(new Intent(getApplicationContext(), PhotoTakingService.class));
                Log.d(LOG_TAG,"after stopService");
                startService(new Intent(getApplicationContext(), PhotoTakingService.class));
                Log.d(LOG_TAG,"after startService");
                PhotoTakingService cPhotoTakingService;
                Log.d(LOG_TAG,"after declare cPhotoTakingService");
                cPhotoTakingService = new PhotoTakingService();
                Log.d(LOG_TAG,"after new  PhotoTakingService");
                //abortBroadcast();

               // cPhotoTakingService.takePhoto(context);
                //Log.d(LOG_TAG,"after new  cPhotoTakingService.takePhoto(this)");
            }
        });
    }

    private void stopSmsRadarService() {
        SmsRadar.stopSmsRadarService(this);
    }

    private void showSmsToast(Sms sms) {
        Toast.makeText(this, sms.toString(), Toast.LENGTH_LONG).show();

    }
}
