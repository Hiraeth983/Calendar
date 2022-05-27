package edu.zjut.androiddeveloper_8.Calendar.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 配置广播接收者:
 *  <receiver android:name=".SMSBroadcastReceiver">
 *     <intent-filter android:priority="1000">
 *         <action android:name="android.provider.Telephony.SMS_RECEIVED"/>
 *     </intent-filter>
 *  </receiver>
 *
 *  注意:
 *  <intent-filter android:priority="1000">表示:
 *  设置此广播接收者的级别为最高
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static MessageListener mMessageListener;

    public SMSBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Object [] pdus= (Object[]) intent.getExtras().get("pdus");
        for(Object pdu:pdus){
            SmsMessage smsMessage=SmsMessage.createFromPdu((byte [])pdu);
            String sender=smsMessage.getDisplayOriginatingAddress();
            String content=smsMessage.getMessageBody();
            long date=smsMessage.getTimestampMillis();
            Date timeDate=new Date(date);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time=simpleDateFormat.format(timeDate);

            System.out.println("短信来自:"+sender);
            System.out.println("短信内容:"+content);
            System.out.println("短信时间:"+time);

            mMessageListener.OnReceived(content);


            //如果短信来自5556,不再往下传递
            if("5556".equals(sender)){
                System.out.println(" abort ");
                abortBroadcast();
            }

        }
    }

    // 回调接口
    public interface MessageListener {
        public void OnReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        mMessageListener=messageListener;
    }
}