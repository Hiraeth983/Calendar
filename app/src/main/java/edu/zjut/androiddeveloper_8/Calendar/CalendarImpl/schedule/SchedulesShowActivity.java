package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import static edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter.getDateFormatter;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.zjut.androiddeveloper_8.Calendar.Adapter.ScheduleAdapter;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.base.activity.BaseActivity;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.custom.CustomActivity;
import edu.zjut.androiddeveloper_8.Calendar.DB.ScheduleDB;
import edu.zjut.androiddeveloper_8.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_8.Calendar.R;
import edu.zjut.androiddeveloper_8.Calendar.Utils.LunarCalendarFestivalUtils;
import edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter;

public class SchedulesShowActivity extends BaseActivity {

    ImageView backMainImageView;

    ImageView searchScheduleImageView;

    RecyclerView mRecyclerView;

    FloatingActionButton addSchedule;

    private List<Object> scheduleList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_schedule_all;
    }

    @Override
    protected void initView() {
        backMainImageView = findViewById(R.id.back_main);

        searchScheduleImageView = findViewById(R.id.ib_search);

        mRecyclerView = findViewById(R.id.schedule_recycler_view_all);

        addSchedule = findViewById(R.id.addSchedule);
        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchedulesShowActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void initData() {
        // 自定义参数列表
        String[] projection = {ScheduleDB._ID,
                ScheduleDB.COLUMN_TITLE,
                ScheduleDB.COLUMN_BEGIN_TIME,
                ScheduleDB.COLUMN_END_TIME
        };
        // 获取所有日程信息
        Cursor cursor = getContentResolver().query(ScheduleDB.CONTENT_URI, projection, null, null, null);
        // 日程列表初始化
        scheduleList = toScheduleList(cursor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(scheduleList);
        mRecyclerView.setAdapter(scheduleAdapter);
    }

    public List<Object> toScheduleList(Cursor cursor) {
        List<Object> temp = new ArrayList<>();
        String flag = "";

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取对印的索引
                int _idIndex = cursor.getColumnIndex(ScheduleDB._ID);
                int titleIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_TITLE);
                int beginTimeIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_BEGIN_TIME);
                int endTimeIndex = cursor.getColumnIndex(ScheduleDB.COLUMN_END_TIME);

                // 获取对应值
                String _id = cursor.getString(_idIndex);
                String title = cursor.getString(titleIndex);
                String beginTime = cursor.getString(beginTimeIndex);
                String endTime = cursor.getString(endTimeIndex);

                if (!beginTime.substring(0, 10).equals(flag)) {
                    flag = beginTime.substring(0, 10);
                    // 拼接日期字符串
                    String date = "";
                    // 使用工具类
                    LunarCalendarFestivalUtils festival = new LunarCalendarFestivalUtils();
                    festival.initLunarCalendarInfo(getDateFormatter(new Date(), "yyyy-MM-dd"));
                    System.out.println();
                    date += getDateFormatter(new Date(), "MM月dd") + festival.getWeekOfDate(new Date()) + " ";
                    date += "农历" + festival.getLunarMonth() + "月" + festival.getLunarDay() + " ";
                    date += festival.getLunarTerm() + " ";
                    date += festival.getSolarFestival() + " ";
                    date += festival.getLunarFestival() + " ";
                    // 插入 list 开头字符串（日期信息）
                    temp.add(date);
                }

                // 规范化数据并插入list
                temp.add(new Schedule(Integer.parseInt(_id), title, beginTime.substring(11, 16), endTime.substring(11, 16)));
            }
        }
        return temp;
    }
}
