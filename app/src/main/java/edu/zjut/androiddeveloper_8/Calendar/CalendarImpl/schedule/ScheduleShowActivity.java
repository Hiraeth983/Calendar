package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.base.activity.BaseActivity;
import edu.zjut.androiddeveloper_8.Calendar.DB.ScheduleDB;
import edu.zjut.androiddeveloper_8.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_8.Calendar.R;
import edu.zjut.androiddeveloper_8.Calendar.Utils.LunarCalendarFestivalUtils;

public class ScheduleShowActivity extends BaseActivity {

    TextView mTitleShow;

    TextView mBeginTime;

    TextView mEndTime;

    TextView mRepeatShow;

    Switch mImportantShow;

    TextView mAccountShow;

    TextView mDescriptionShow;

    TextView mLocateShow;

    TextView mTimeZoneShow;

    Uri mCurrentScheduleUri;

    ImageView mBackImageView;

    ImageView mEditSchedule;

    ImageView mDeleteSchedule;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_schedule;
    }

    @Override
    protected void initView() {
        mTitleShow = findViewById(R.id.title_show);

        mBeginTime = findViewById(R.id.begin_show);

        mEndTime = findViewById(R.id.end_show);

        mRepeatShow = findViewById(R.id.repeat_select_show);

        mImportantShow = findViewById(R.id.important_switch_show);

        mAccountShow = findViewById(R.id.account_select_show);

        mDescriptionShow = findViewById(R.id.description_show);

        mLocateShow = findViewById(R.id.locate_show);

        mTimeZoneShow = findViewById(R.id.time_zone_select_show);

        mBackImageView = findViewById(R.id.back);
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mEditSchedule = findViewById(R.id.ib_edit_schedule);

        mDeleteSchedule = findViewById(R.id.ib_delete_schedule);
        mDeleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mCurrentScheduleUri = intent.getData();
        Log.i("currentContactUri", mCurrentScheduleUri + "");
        init();
    }

    public void init() {
        // 自定义参数列表
        String[] projection = {ScheduleDB._ID,
                ScheduleDB.COLUMN_TITLE,
                ScheduleDB.COLUMN_LOCATE,
                ScheduleDB.COLUMN_TIME_SLOT,
                ScheduleDB.COLUMN_BEGIN_TIME,
                ScheduleDB.COLUMN_END_TIME,
                ScheduleDB.COLUMN_REPEAT,
                ScheduleDB.COLUMN_IMPORTANT,
                ScheduleDB.COLUMN_ACCOUNT,
                ScheduleDB.COLUMN_DESCRIPTION,
                ScheduleDB.COLUMN_TIME_ZONE
        };
        // 获取当前点击日程信息
        Cursor cursor = getContentResolver().query(mCurrentScheduleUri, projection, null, null, null);
        Schedule schedule = toSchedule(cursor);

        mTitleShow.setText(schedule.getTitle());
        mBeginTime.setText(schedule.getBegin_time());
        mEndTime.setText(schedule.getEnd_time());
        mRepeatShow.setText(schedule.getRepeat());
        mImportantShow.setChecked(schedule.getImportant().equals("重要提醒"));
        mAccountShow.setText(schedule.getAccount());
        mDescriptionShow.setText(schedule.getDescription());
        mLocateShow.setText(schedule.getLocate());
        mTimeZoneShow.setText(schedule.getTime_zone());
    }

    // 编辑界面的确认删除功能
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除当前日程？");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16);
    }

    // 删除选中的日程
    private void deleteProduct() {
        if (mCurrentScheduleUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentScheduleUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "删除失败",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "删除成功",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public Schedule toSchedule(Cursor cursor) {
        Schedule schedule = new Schedule();
        if (cursor != null && cursor.moveToFirst()) {
            // 获取对印的索引
            int _idIndex = cursor.getColumnIndex(ScheduleDB._ID);
            int titleIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_TITLE);
            int locateIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_LOCATE);
            int timeSlotIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_TIME_SLOT);
            int beginTimeIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_BEGIN_TIME);
            int endTimeIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_END_TIME);
            int repeatIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_REPEAT);
            int importantIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_IMPORTANT);
            int accountIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_ACCOUNT);
            int descriptionIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_DESCRIPTION);
            int timeZoneIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_TIME_ZONE);

            // 获取对应值
            String _id = cursor.getString(_idIndex);
            String title = cursor.getString(titleIndex);
            String locate = cursor.getString(locateIndex);
            String timeSlot = cursor.getString(timeSlotIndex);
            String beginTime = cursor.getString(beginTimeIndex);
            String endTime = cursor.getString(endTimeIndex);
            String repeat = cursor.getString(repeatIndex);
            String important = cursor.getString(importantIndex);
            String account = cursor.getString(accountIndex);
            String description = cursor.getString(descriptionIndex);
            String timeZone = cursor.getString(timeZoneIndex);

            // 调整数据格式
            String begin_time = "时间段： ";
            begin_time += beginTime.substring(11, 16) + " - " + endTime.substring(11, 16);
            String end_time = "日    期： ";
            String date = beginTime.substring(0, 10);
            // 使用工具类
            LunarCalendarFestivalUtils festival = new LunarCalendarFestivalUtils();
            festival.initLunarCalendarInfo(date);
            date.replaceFirst("-", "年");
            date.replaceFirst("-", "月");
            date += "日";
            end_time += date + festival.getWeekOfDate(new Date());

            // 构造函数
            schedule = new Schedule(Integer.parseInt(_id), title, locate, timeSlot, begin_time, end_time, repeat, important, account, description, timeZone);
        }
        return schedule;
    }

    @Override
    protected void onResume() {  // 自动更新
        super.onResume();
        init();
    }
}
