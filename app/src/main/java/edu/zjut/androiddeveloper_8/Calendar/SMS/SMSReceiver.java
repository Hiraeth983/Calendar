package edu.zjut.androiddeveloper_8.Calendar.SMS;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;


public class SMSReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMSMethod.SMS_SEND_ACTIOIN)){
            try{
                /* android.content.BroadcastReceiver.getResultCode()方法 */
                //Retrieve the current result code, as set by the previous receiver.
                System.out.println("<<<<<<<<<<<<!!!!!!!!!!错误原因:  "+getResultCode());
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        System.out.println("短信发送成功");
                        Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        System.out.println("短信发送失败");
                        Toast.makeText(context, "短信发送失败", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if(intent.getAction().equals(SMSMethod.SMS_DELIVERED_ACTION)){
            /* android.content.BroadcastReceiver.getResultCode()方法 */
            System.out.println("<<<<<<<<<<<<!!!!!!!!!!错误原因:  "+getResultCode());
            switch(getResultCode()){
                case Activity.RESULT_OK:
                    System.out.println("短信已送达");
                    Toast.makeText(context, "短信已送达", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    System.out.println("短信未送达");
                    /* 短信未送达 */
                    Toast.makeText(context, "短信未送达", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    break;
            }
        }
    }
}
