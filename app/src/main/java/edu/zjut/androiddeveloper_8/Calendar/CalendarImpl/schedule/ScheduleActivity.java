package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.base.activity.BaseActivity;
import edu.zjut.androiddeveloper_8.Calendar.R;


public class ScheduleActivity extends BaseActivity {

    // TextView
    TextView mTitle,mLocate,mBeginTime,mEndTime,mRepeatSelect,mTimeZoneSelect;

    // EditText
    EditText mScheduleTitle,mDescription;

    // Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch mTimeSlotSwitch,mImportantSwitch;

    // Other
    private Uri currentUri;
    private boolean mScheduleHasChanged = false;
    public static final int LOADER = 0;
    boolean hasAllRequiredValues = false;

    // 点击监听，点击即视为修改
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mScheduleHasChanged = true;
            return false;
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_schedule_edit;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        // 获取View
        mTitle = findViewById(R.id.title);
        mScheduleTitle = findViewById(R.id.schedule_title);
        mLocate = findViewById(R.id.locate);
        mTimeSlotSwitch = findViewById(R.id.time_slot_switch);
        mBeginTime = findViewById(R.id.begin_time);
        mEndTime = findViewById(R.id.end_time);
        mRepeatSelect = findViewById(R.id.repeat_select);
        mImportantSwitch = findViewById(R.id.important_switch);
        mDescription = findViewById(R.id.description);
        mTimeZoneSelect = findViewById(R.id.time_zone_select);

        // 添加触控监听
        mScheduleTitle.setOnTouchListener(onTouchListener);
        mTimeSlotSwitch.setOnTouchListener(onTouchListener);
        mBeginTime.setOnTouchListener(onTouchListener);
        mEndTime.setOnTouchListener(onTouchListener);
        mImportantSwitch.setOnTouchListener(onTouchListener);
        mDescription.setOnTouchListener(onTouchListener);



    }

    @Override
    protected void initData() {
        // 获取URI
        Intent intent = getIntent();
        currentUri = intent.getData();
        Log.i("currentContactUri", currentUri + "");

        // 此处判断是编辑模式还是新建模式
        if (currentUri == null) {
            mTitle.setText("新建日程");
        } else {
            mTitle.setText("编辑日程");
            
        }


    }

}
