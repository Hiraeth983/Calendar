package edu.zjut.androiddeveloper_8.Calendar.SMS;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Date;
import java.util.Objects;

import edu.zjut.androiddeveloper_8.Calendar.Contact.adapter.MyCursorAdapter;
import edu.zjut.androiddeveloper_8.Calendar.Contact.db.Contact;
import edu.zjut.androiddeveloper_8.Calendar.DB.ScheduleDB;
import edu.zjut.androiddeveloper_8.Calendar.Model.Schedule;

public class SMSReceiverService extends Service {
    SMSBroadcastReceiver mSMSBroadcastReceiver;
    boolean hasAllRequiredValues = false;
    private IBinder myBinder =new Binder(){};
    public SMSReceiverService() {
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendTextMessage(String destAddress, String Message){
        SMSMethod.getInstance(this).SendMessage(destAddress,Message);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendMultipartTextMessage(String destAddress, String Message){
        SMSMethod.getInstance(this).SendMessage2(destAddress,Message);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("Recycle")
    private void init(){
        // TODO:0.判断是否为联系人 1.接收到联系人的日期，转成date  2.接收到日程
        mSMSBroadcastReceiver=new SMSBroadcastReceiver();
        mSMSBroadcastReceiver.setOnReceivedMessageListener(message -> {
            //TODO:判断短信sender是否为联系人

            String[] projection = {Contact.ContactEntry._ID,
                    Contact.ContactEntry.COLUMN_NAME,
                    Contact.ContactEntry.COLUMN_TELEPHONE,
                    Contact.ContactEntry.COLUMN_WORKPLACE,
                    Contact.ContactEntry.COLUMN_ADDRESS,
                    Contact.ContactEntry.COLUMN_TIME
            };
            String sender = message[0];
            String content = message[1].trim();
            String selection;
            String[] args;
            if (TextUtils.isEmpty(sender)) {
                return;
            } else {
                selection = Contact.ContactEntry.COLUMN_TELEPHONE + " like ?";
                args = new String[]{"%" + sender};//模糊+86或是其他地区号码前缀
            }

            if(Objects.requireNonNull(getContentResolver().query(Contact.ContactEntry.CONTENT_URI, projection, selection, args, null)).getCount()!=0){//在联系人里
                //按日期查询
                if (!"".equals(content) && content.matches("^[0-9]*$")){
                    String year = content.substring(0,4);
                    String month = content.substring(4,6);
                    String date = content.substring(6);
                    String dateForSearch = year+"-"+month+"-"+date;
                    System.out.println("dateforsearch"+dateForSearch);
                    String result = searchCalendarByDate(dateForSearch);
                    sendMultipartTextMessage(sender,result);

                //发送模版
                }else if ("添加日程".equals(content)){
                    String AddExp = "#日程标题:新建日程\n"+
                            "#开始时间:1997-01-01 09:00:00\n"+
                            "#结束时间:1997-01-01 10:00:00\n"+
                            "#日程内容:开发Android";
                    sendMultipartTextMessage(sender,"请按照以下模版编辑日程");
                    sendMultipartTextMessage(sender,AddExp);

                //TODO: 添加日程
                }else if("#".equals(content.substring(0,1))){
                    String[] AddCalendar = content.split("#");
                    for (String a:AddCalendar) {
                        System.out.println("编辑前："+a);
                    }
                    String calendarTitle = AddCalendar[1].substring(AddCalendar[1].indexOf(":")+1);
                    System.out.println("编辑后："+calendarTitle);
                    String calendarStartTime = AddCalendar[2].substring(AddCalendar[2].indexOf(":")+1);
                    System.out.println("编辑后："+calendarStartTime);
                    String calendarEndTime = AddCalendar[3].substring(AddCalendar[3].indexOf(":")+1);
                    System.out.println("编辑后："+calendarEndTime);
                    String calendarContent = AddCalendar[4].substring(AddCalendar[4].indexOf(":")+1);
                    System.out.println("编辑后："+calendarContent);
                    if (saveSchedule(calendarTitle,calendarStartTime,calendarEndTime,calendarContent)){
                        sendMultipartTextMessage(sender,"日程添加成功！");
                    }else {
                        sendMultipartTextMessage(sender,"日程添加失败！");

                    }
                }
            }

//            Toast.makeText(SMSReceiverService.this, "接收成功："+message[0], Toast.LENGTH_LONG).show();

        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCreate(){
        super.onCreate();
        Log.i(TAG,"SMSReceiver服务已启动：onCreate()!");
        init();
    }

    private String searchCalendarByDate(String date){
        StringBuilder result= new StringBuilder();
        String[] projection = {ScheduleDB._ID,
                ScheduleDB.COLUMN_TITLE,
                ScheduleDB.COLUMN_BEGIN_TIME,
                ScheduleDB.COLUMN_END_TIME,
                ScheduleDB.COLUMN_DESCRIPTION
        };
        String selection;
        String[] args;
        if (TextUtils.isEmpty(date)) {
            return "查询失败！";
        } else {
            selection = ScheduleDB.COLUMN_BEGIN_TIME + " like ?";
            args = new String[]{"%" + date + "%"};
        }
        // 读取数据库信息及更新视图
        // 获取所有日程信息
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(ScheduleDB.CONTENT_URI, projection, selection, args, "begin_time");
        Schedule[] schedules = ConvertToSchedule(cursor);
        if (schedules==null){
            result.append("今日无开始日程！");
        }else {
            for (Schedule s:schedules) {
                result.append(s.toString()).append("\n");
            }
        }
        return result.toString();
    }

    private Schedule[] ConvertToSchedule(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        Schedule[] schedules = new Schedule[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            schedules[i] = new Schedule();
            schedules[i].set_id(i+1);
            schedules[i].setBegin_time(cursor.getString(cursor.getColumnIndex(ScheduleDB.COLUMN_BEGIN_TIME)).trim());
            schedules[i].setEnd_time(cursor.getString(cursor.getColumnIndex(ScheduleDB.COLUMN_END_TIME)).trim());
            schedules[i].setTitle(cursor.getString(cursor.getColumnIndex(ScheduleDB.COLUMN_TITLE)));
            schedules[i].setDescription(cursor.getString(cursor.getColumnIndex(ScheduleDB.COLUMN_DESCRIPTION)));
            System.out.println(schedules[i]);
            cursor.moveToNext();
        }
        return schedules;
    }

    // 保存日程
    private boolean saveSchedule(String title, String beginTime, String endTime, String description) {

        // 保存结果
        String locate = "SMS";
        String time_slot = "全天";
        String repeat = "不重复";
        String important = "重要提醒";
        String account = "我的日历";
        String timeZone = "GMT+8:00 中国标准时间";
        System.out.println("save:  "+endTime);
        System.out.println("save: "+description);

        // 当用户没有输入时
        if (TextUtils.isEmpty(title)
                && TextUtils.isEmpty(locate)
                && TextUtils.isEmpty(time_slot)
                && TextUtils.isEmpty(beginTime)
                && TextUtils.isEmpty(endTime)
                && TextUtils.isEmpty(repeat)
                && TextUtils.isEmpty(important)
                && TextUtils.isEmpty(account)
                && TextUtils.isEmpty(description)
                && TextUtils.isEmpty(timeZone)) {
            return false;
        }

        ContentValues values = new ContentValues();

        // 保存标题信息

        values.put(ScheduleDB.COLUMN_TITLE, title.trim());


        // 保存位置信息

        values.put(ScheduleDB.COLUMN_LOCATE, locate);


        // 保存时间段（全天/非全天）
        values.put(ScheduleDB.COLUMN_TIME_SLOT, time_slot);

        // 先判断是否为合法时间段，在保存
        if (!isValidSchedule(beginTime, endTime)) {
            Toast.makeText(this, "时间段有误!", Toast.LENGTH_SHORT).show();
            return false;
        }
        values.put(ScheduleDB.COLUMN_BEGIN_TIME, beginTime);
        values.put(ScheduleDB.COLUMN_END_TIME, endTime);

        // 是否重复
        values.put(ScheduleDB.COLUMN_REPEAT, repeat);

        // 是否为重要提醒
        values.put(ScheduleDB.COLUMN_IMPORTANT, important);

        // 账户信息
        values.put(ScheduleDB.COLUMN_ACCOUNT, account);

        // 日程内容
        values.put(ScheduleDB.COLUMN_DESCRIPTION, description);

        // 时区信息
        values.put(ScheduleDB.COLUMN_TIME_ZONE, timeZone);


        Uri newUri = getContentResolver().insert(ScheduleDB.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    // 判断日程开始时间是否小于结束时间
    private boolean isValidSchedule(String beginTime, String endTime) {
        return beginTime.compareTo(endTime) < 0;
    }

}