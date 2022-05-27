package edu.zjut.androiddeveloper_8.Calendar.SMS;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiverService extends Service {
    SMSBroadcastReceiver mSMSBroadcastReceiver;
    private IBinder myBinder =new Binder(){};
    public SMSReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myBinder;
    }


    private void init(){
        mSMSBroadcastReceiver=new SMSBroadcastReceiver();
        mSMSBroadcastReceiver.setOnReceivedMessageListener(message -> {
            Toast.makeText(SMSReceiverService.this, "接受成功："+message, Toast.LENGTH_LONG).show();
            SmsManager smsManager= SmsManager.getDefault();
            smsManager.sendTextMessage("+8617757194086","5554","hello",null,null);
            Toast.makeText(SMSReceiverService.this, "发送成功", Toast.LENGTH_LONG).show();
        });
    }
    public void onCreate(){
        super.onCreate();
        Log.i(TAG,"服务已启动：onCreate()!");
        init();
    }

}