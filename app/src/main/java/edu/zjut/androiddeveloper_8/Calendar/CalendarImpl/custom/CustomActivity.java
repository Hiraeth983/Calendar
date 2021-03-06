package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.custom;

import static edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter.getDateFormatter;
import static edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter.parseDateFormatter;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule.OnItemClickListener;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule.ScheduleShowActivity;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule.SchedulesShowActivity;
import edu.zjut.androiddeveloper_8.Calendar.Contact.ContactActivity;
import edu.zjut.androiddeveloper_8.Calendar.Utils.LunarCalendarFestivalUtils;
import edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter;

import edu.zjut.androiddeveloper_8.Calendar.Adapter.ScheduleAdapter;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.base.activity.BaseActivity;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule.ScheduleActivity;
import edu.zjut.androiddeveloper_8.Calendar.DB.ScheduleDB;
import edu.zjut.androiddeveloper_8.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_8.calendarview.Calendar;
import edu.zjut.androiddeveloper_8.calendarview.CalendarLayout;
import edu.zjut.androiddeveloper_8.calendarview.CalendarView;
import edu.zjut.androiddeveloper_8.Calendar.R;

public class CustomActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener {

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;

    private int mYear;

    CalendarLayout mCalendarLayout;

    RecyclerView mRecyclerView;

    private List<Object> scheduleList = new ArrayList<>();

    private FloatingActionButton mCurrentDay;

    private FloatingActionButton mAddScheduleFloatButton;

    private ImageView allScheduleImageView;

    public static void show(Context context) {
        context.startActivity(new Intent(context, CustomActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        setStatusBarDarkMode();

        mTextMonthDay = findViewById(R.id.tv_month_day);

        mTextYear = findViewById(R.id.tv_year);

        mTextLunar = findViewById(R.id.tv_lunar);

        mRelativeTool = findViewById(R.id.rl_tool);

        mCalendarView = findViewById(R.id.calendarView);

        mRecyclerView = findViewById(R.id.schedule_recycler_view);

        allScheduleImageView = findViewById(R.id.ib_schedule);
        allScheduleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomActivity.this, SchedulesShowActivity.class);
                startActivity(intent);
            }
        });

        // ????????????????????????????????????????????????
        mCurrentDay = findViewById(R.id.currentDay);
        mCurrentDay.hide();
        mCurrentDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendarView.scrollToCurrent();
                init(new Date());
                mCurrentDay.hide();
            }
        });

        // ??????????????????????????????????????????
        mAddScheduleFloatButton = findViewById(R.id.addSchedule);
        mAddScheduleFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton floatButton1 = findViewById(R.id.contactManage);
        floatButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });


        // ????????????????????????????????? formatter.format(date)
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date date = new Date(System.currentTimeMillis());
        int day = Integer.parseInt(formatter.format(date));
        switch (day) {
            case 1:
                mCurrentDay.setImageResource(R.drawable.ic_1);
                break;
            case 2:
                mCurrentDay.setImageResource(R.drawable.ic_2);
                break;
            case 3:
                mCurrentDay.setImageResource(R.drawable.ic_3);
                break;
            case 4:
                mCurrentDay.setImageResource(R.drawable.ic_4);
                break;
            case 5:
                mCurrentDay.setImageResource(R.drawable.ic_5);
                break;
            case 6:
                mCurrentDay.setImageResource(R.drawable.ic_6);
                break;
            case 7:
                mCurrentDay.setImageResource(R.drawable.ic_7);
                break;
            case 8:
                mCurrentDay.setImageResource(R.drawable.ic_8);
                break;
            case 9:
                mCurrentDay.setImageResource(R.drawable.ic_9);
                break;
            case 10:
                mCurrentDay.setImageResource(R.drawable.ic_10);
                break;
            case 11:
                mCurrentDay.setImageResource(R.drawable.ic_11);
                break;
            case 12:
                mCurrentDay.setImageResource(R.drawable.ic_12);
                break;
            case 13:
                mCurrentDay.setImageResource(R.drawable.ic_13);
                break;
            case 14:
                mCurrentDay.setImageResource(R.drawable.ic_14);
                break;
            case 15:
                mCurrentDay.setImageResource(R.drawable.ic_15);
                break;
            case 16:
                mCurrentDay.setImageResource(R.drawable.ic_16);
                break;
            case 17:
                mCurrentDay.setImageResource(R.drawable.ic_17);
                break;
            case 18:
                mCurrentDay.setImageResource(R.drawable.ic_18);
                break;
            case 19:
                mCurrentDay.setImageResource(R.drawable.ic_19);
                break;
            case 20:
                mCurrentDay.setImageResource(R.drawable.ic_20);
                break;
            case 21:
                mCurrentDay.setImageResource(R.drawable.ic_21);
                break;
            case 22:
                mCurrentDay.setImageResource(R.drawable.ic_22);
                break;
            case 23:
                mCurrentDay.setImageResource(R.drawable.ic_23);
                break;
            case 24:
                mCurrentDay.setImageResource(R.drawable.ic_24);
                break;
            case 25:
                mCurrentDay.setImageResource(R.drawable.ic_25);
                break;
            case 26:
                mCurrentDay.setImageResource(R.drawable.ic_26);
                break;
            case 27:
                mCurrentDay.setImageResource(R.drawable.ic_27);
                break;
            case 28:
                mCurrentDay.setImageResource(R.drawable.ic_28);
                break;
            case 29:
                mCurrentDay.setImageResource(R.drawable.ic_29);
                break;
            case 30:
                mCurrentDay.setImageResource(R.drawable.ic_30);
                break;
            case 31:
                mCurrentDay.setImageResource(R.drawable.ic_31);
                break;
            default:
                break;
        }

        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "???" + mCalendarView.getCurDay() + "???");
        mTextLunar.setText("??????");
    }

    public List<Object> toScheduleList(Cursor cursor, Date _date) {
        List<Object> temp = new ArrayList<>();
        // ?????????????????????
        String date = "";
        // ???????????????
        LunarCalendarFestivalUtils festival = new LunarCalendarFestivalUtils();
        festival.initLunarCalendarInfo(getDateFormatter(_date, "yyyy-MM-dd"));

        date += getDateFormatter(_date, "MM???dd") + festival.getWeekOfDate(_date) + " ";
        date += "??????" + festival.getLunarMonth() + "???" + festival.getLunarDay() + " ";
        date += festival.getLunarTerm() + " ";
        date += festival.getSolarFestival() + " ";
        date += festival.getLunarFestival() + " ";
        // ?????? list ?????????????????????????????????
        temp.add(date);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // ?????????????????????
                int _idIndex = cursor.getColumnIndex(ScheduleDB._ID);
                int titleIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_TITLE);
                int beginTimeIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_BEGIN_TIME);
                int endTimeIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_END_TIME);
                int descriptionIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_DESCRIPTION);

                // ???????????????
                String _id = cursor.getString(_idIndex);
                String title = cursor.getString(titleIndex);
                String beginTime = cursor.getString(beginTimeIndex);
                String endTime = cursor.getString(endTimeIndex);
                String description = cursor.getString(descriptionIndex).equals("") ? "(??????????????????)" : cursor.getString(descriptionIndex);

                // ????????????????????????list
                temp.add(new Schedule(Integer.parseInt(_id), title, beginTime.substring(11, 16), endTime.substring(11, 16), description));
            }
        }
        return temp;
    }

    @Override
    protected void initData() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();

        Map<String, Calendar> map = new HashMap<>();
        map.put(getSchemeCalendar(year, month, 3, 0xFF40db25, "???").toString(),
                getSchemeCalendar(year, month, 3, 0xFF40db25, "???"));
        map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "???").toString(),
                getSchemeCalendar(year, month, 6, 0xFFe69138, "???"));
        map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "???").toString(),
                getSchemeCalendar(year, month, 9, 0xFFdf1356, "???"));
        map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "???").toString(),
                getSchemeCalendar(year, month, 13, 0xFFedc56d, "???"));
        map.put(getSchemeCalendar(year, month, 14, 0xFFedc56d, "???").toString(),
                getSchemeCalendar(year, month, 14, 0xFFedc56d, "???"));
        map.put(getSchemeCalendar(year, month, 15, 0xFFaacc44, "???").toString(),
                getSchemeCalendar(year, month, 15, 0xFFaacc44, "???"));
        map.put(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "???").toString(),
                getSchemeCalendar(year, month, 18, 0xFFbc13f0, "???"));
        map.put(getSchemeCalendar(year, month, 25, 0xFF13acf0, "???").toString(),
                getSchemeCalendar(year, month, 25, 0xFF13acf0, "???"));
        map.put(getSchemeCalendar(year, month, 27, 0xFF13acf0, "???").toString(),
                getSchemeCalendar(year, month, 27, 0xFF13acf0, "???"));
        //?????????????????????????????????????????????????????????????????????
        mCalendarView.setSchemeDate(map);

        init(new Date());
    }

    public void init(Date date) {
        // ?????????????????????
        String[] projection = {ScheduleDB._ID,
                ScheduleDB.COLUMN_TITLE,
                ScheduleDB.COLUMN_BEGIN_TIME,
                ScheduleDB.COLUMN_END_TIME,
                ScheduleDB.COLUMN_DESCRIPTION
        };
        String selection = ScheduleDB.COLUMN_BEGIN_TIME + " between ? and ?";
        Log.i("time", date.toString() + "==========");
        String[] args = {MyDateFormatter.getStartTime(date), MyDateFormatter.getEndTime(date)};
        // ??????????????????????????????
        Cursor cursor = getContentResolver().query(ScheduleDB.CONTENT_URI, projection, selection, args, null);
        // ?????????????????????
        scheduleList = toScheduleList(cursor, date);
        Log.i("time", scheduleList.size() + "==========");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(scheduleList);
        scheduleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(CustomActivity.this, ScheduleShowActivity.class);
                Schedule s = (Schedule) scheduleList.get(position);
                Uri newUri = ContentUris.withAppendedId(ScheduleDB.CONTENT_URI, s.get_id());
                Log.i("newUri", newUri + "");

                intent.setData(newUri);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view) {

            }

            @Override
            public void onItemChecked(CompoundButton compoundButton, boolean isChecked, int position) {

            }
        });
        mRecyclerView.setAdapter(scheduleAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//???????????????????????????????????????????????????
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "???");
        calendar.addScheme(0xFF008800, "???");
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "???" + calendar.getDay() + "???");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        if (isClick) {
            mCurrentDay.show();
            init(parseDateFormatter(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay(), "yyyy-MM-dd"));
        }
        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

    @Override
    protected void onResume() {  // ????????????
        super.onResume();
        init(new Date());
    }
}
