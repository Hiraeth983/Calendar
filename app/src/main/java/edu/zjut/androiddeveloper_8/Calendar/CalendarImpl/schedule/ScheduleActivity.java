package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import java.util.Calendar;
import java.util.Date;

import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.base.activity.BaseActivity;
import edu.zjut.androiddeveloper_8.Calendar.R;
import edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;


public class ScheduleActivity extends BaseActivity {

    // TextView
    TextView mTitle, mLocate, mBeginTime, mEndTime, mRepeatSelect, mTimeZoneSelect;

    // EditText
    EditText mScheduleTitle, mDescription;

    // ImageButton
    ImageButton mBeginTimeButton;
    ImageButton mEndTimeButton;

    // Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch mTimeSlotSwitch, mImportantSwitch;

    // Other
    private Uri currentUri;
    private boolean mScheduleHasChanged = false;
    public static final int LOADER = 0;
    private boolean hasAllRequiredValues = false;
    private final Context context = this;

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

    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor"})
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

        // 开始时间 Button 获取及添加监听
        mBeginTimeButton = findViewById(R.id.begin_time_spinner);
        mBeginTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CardDatePickerDialog.Builder(context)
                        .setTitle("选择开始时间")
                        .showBackNow(false)
                        // 神奇的时间戳少八个小时，故自行加之
                        .setDefaultTime(Calendar.getInstance().getTimeInMillis() + 60 * 60 * 8 * 1000)
                        .setMaxTime(0)
                        .setMinTime(0)
                        .setWrapSelectorWheel(false)
                        .setThemeColor(R.color.line_color)
                        .showDateLabel(true)
                        .showFocusDateInfo(true)
                        .setTouchHideable(true)
                        .setLabelText("年", "月", "日", "时", "分", "秒")
                        .setOnChoose("选择", new Function1<Long, Unit>() {
                            @Override
                            public Unit invoke(Long aLong) {
                                String begin_time = MyDateFormatter.getDateFormatter(new Date(aLong), "yyyy年MM月dd日 HH:mm");
//                                Toast.makeText(context, begin_time, Toast.LENGTH_SHORT).show();
                                mBeginTime.setText(begin_time);
                                return null;
                            }
                        })
                        .setOnCancel("关闭", new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                return null;
                            }
                        })
                        .build().show();
            }
        });

        // 结束时间 Button 获取及添加监听
        mEndTimeButton = findViewById(R.id.end_time_spinner);
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CardDatePickerDialog.Builder(context)
                        .setTitle("选择结束时间")
                        .showBackNow(false)
                        // 神奇的时间戳少八个小时，故自行加之
                        .setDefaultTime(Calendar.getInstance().getTimeInMillis() + 60 * 60 * 8 * 1000)
                        .setMaxTime(0)
                        .setMinTime(0)
                        .setWrapSelectorWheel(false)
                        .setThemeColor(R.color.line_color)
                        .showDateLabel(true)
                        .showFocusDateInfo(true)
                        .setTouchHideable(true)
                        .setLabelText("年", "月", "日", "时", "分", "秒")
                        .setOnChoose("选择", new Function1<Long, Unit>() {
                            @Override
                            public Unit invoke(Long aLong) {
                                String end_time = MyDateFormatter.getDateFormatter(new Date(aLong), "yyyy年MM月dd日 HH:mm");
//                                Toast.makeText(context, begin_time, Toast.LENGTH_SHORT).show();
                                mEndTime.setText(end_time);
                                return null;
                            }
                        })
                        .setOnCancel("关闭", new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                return null;
                            }
                        })
                        .build().show();
            }
        });


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
