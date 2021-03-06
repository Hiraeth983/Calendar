package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import android.app.AlertDialog;
import android.content.ContentUris;
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
        mEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleShowActivity.this, ScheduleActivity.class);

                intent.setData(mCurrentScheduleUri);
                startActivity(intent);
            }
        });

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
        // ?????????????????????
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
        // ??????????????????????????????
        Cursor cursor = getContentResolver().query(mCurrentScheduleUri, projection, null, null, null);
        Schedule schedule = toSchedule(cursor);

        mTitleShow.setText(schedule.getTitle());
        mBeginTime.setText(schedule.getBegin_time());
        mEndTime.setText(schedule.getEnd_time());
        mRepeatShow.setText(schedule.getRepeat());
        mImportantShow.setChecked(schedule.getImportant().equals("????????????"));
        mAccountShow.setText(schedule.getAccount());
        mDescriptionShow.setText(schedule.getDescription());
        mLocateShow.setText(schedule.getLocate());
        mTimeZoneShow.setText(schedule.getTime_zone());
    }

    // ?????????????????????????????????
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("?????????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
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

    // ?????????????????????
    private void deleteProduct() {
        if (mCurrentScheduleUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentScheduleUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "????????????",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "????????????",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public Schedule toSchedule(Cursor cursor) {
        Schedule schedule = new Schedule();
        if (cursor != null && cursor.moveToFirst()) {
            // ?????????????????????
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

            // ???????????????
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

            // ??????????????????
            String begin_time = "???????????? ";
            begin_time += beginTime.substring(11, 16) + " - " + endTime.substring(11, 16);
            String end_time = "???    ?????? ";
            String date = beginTime.substring(0, 10);
            // ???????????????
            LunarCalendarFestivalUtils festival = new LunarCalendarFestivalUtils();
            festival.initLunarCalendarInfo(date);
            date.replaceFirst("-", "???");
            date.replaceFirst("-", "???");
            date += "???";
            end_time += date + festival.getWeekOfDate(new Date());

            // ????????????
            schedule = new Schedule(Integer.parseInt(_id), title, locate, timeSlot, begin_time, end_time, repeat, important, account, description, timeZone);
        }
        return schedule;
    }

    @Override
    protected void onResume() {  // ????????????
        super.onResume();
        init();
    }
}
