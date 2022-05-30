package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import static edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter.getDateFormatter;
import static edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter.parseDateFormatter;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import edu.zjut.androiddeveloper_8.Calendar.Adapter.ScheduleAdapter;
import edu.zjut.androiddeveloper_8.Calendar.Adapter.ScheduleDeleteAdapter;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.base.activity.BaseActivity;
import edu.zjut.androiddeveloper_8.Calendar.DB.ScheduleDB;
import edu.zjut.androiddeveloper_8.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_8.Calendar.R;
import edu.zjut.androiddeveloper_8.Calendar.Utils.LunarCalendarFestivalUtils;
import edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter;

public class SchedulesDeleteActivity extends BaseActivity {

    ImageView backMainImageView;

    ImageView deleteCheckedImageView;

    SearchView searchScheduleSearchView;

    RecyclerView mRecyclerView;

    TextView mTitle;

    private List<Object> scheduleList = new ArrayList<>();

    private List<Integer> checkedScheduleList = new ArrayList<>();

    Context context = this;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_schedule_delete;
    }

    @Override
    protected void initView() {
        mTitle = findViewById(R.id.title_schedule_delete);
        backMainImageView = findViewById(R.id.back_main);
        backMainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteCheckedImageView = findViewById(R.id.ib_delete_schedules);
        deleteCheckedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCheckedSchedules();
            }
        });

        searchScheduleSearchView = findViewById(R.id.ib_search);
        setupSearchView();

        mRecyclerView = findViewById(R.id.schedule_recycler_view_all);
    }

    @Override
    protected void initData() {
        init();
    }

    // 数据的获取
    public void init() {
        // 自定义参数列表
        String[] projection = {ScheduleDB._ID,
                ScheduleDB.COLUMN_TITLE,
                ScheduleDB.COLUMN_BEGIN_TIME,
                ScheduleDB.COLUMN_END_TIME
        };
        // 获取所有日程信息
        Cursor cursor = getContentResolver().query(ScheduleDB.CONTENT_URI, projection, null, null, "begin_time");
        // 日程列表初始化
        scheduleList = toScheduleList(cursor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        ScheduleDeleteAdapter scheduleDeleteAdapter = new ScheduleDeleteAdapter(scheduleList);
        scheduleDeleteAdapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view) {
            }

            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemChecked(CompoundButton compoundButton, boolean isChecked, int position) {
                Schedule s = (Schedule) scheduleList.get(position);
//                Toast.makeText(context, "位置" + position + "  " + s.get_id(), Toast.LENGTH_SHORT).show();
                if (!isChecked) {
                    // 从选中列表中移除
                    checkedScheduleList.removeIf(new Predicate<Integer>() {
                        @Override
                        public boolean test(Integer integer) {
                            return integer.equals(s.get_id());
                        }
                    });
                    compoundButton.setChecked(false);
                } else {
                    // 添加到选中列表中
//                    Toast.makeText(context, "id:" + s.get_id(), Toast.LENGTH_SHORT).show();
                    checkedScheduleList.add((Integer) s.get_id());
                    compoundButton.setChecked(true);
                }
                mTitle.setText("已选择 " + checkedScheduleList.size() + " 项");
//                Toast.makeText(context, "list大小" + checkedScheduleList.size(), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(scheduleDeleteAdapter);
    }

    // 删除选中日程
    public void deleteCheckedSchedules() {
        for (int i = 0; i < checkedScheduleList.size(); i++) {
            int id = (int) checkedScheduleList.get(i);
//            Toast.makeText(context, id + "", Toast.LENGTH_SHORT).show();
            Uri newUri = ContentUris.withAppendedId(ScheduleDB.CONTENT_URI, id);
//            Toast.makeText(context, newUri + "", Toast.LENGTH_SHORT).show();
            int num = getContentResolver().delete(newUri, null, null);
            if (num != 1) {
                Toast.makeText(context, "删除失败！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(context, "删除成功！", Toast.LENGTH_SHORT).show();
        finish();


    }

    // 设置搜索框监听逻辑
    private void setupSearchView() {
        //设置搜索框直接展开显示。左侧有放大镜(在搜索框中) 右侧有叉叉 可以关闭搜索框
        //searchScheduleSearchView.setIconified(false);
        //设置搜索框直接展开显示。左侧有放大镜(在搜索框外) 右侧无叉叉 有输入内容后有叉叉 不能关闭搜索框
        //searchScheduleSearchView.setIconifiedByDefault(false);
        //设置搜索框直接展开显示。左侧有无放大镜(在搜索框中) 右侧无叉叉 有输入内容后有叉叉 不能关闭搜索框
        searchScheduleSearchView.onActionViewExpanded();

        // 自定义参数列表
        String[] projection = {ScheduleDB._ID,
                ScheduleDB.COLUMN_TITLE,
                ScheduleDB.COLUMN_BEGIN_TIME,
                ScheduleDB.COLUMN_END_TIME
        };
        Context thisContext = this;

        //为 SearchView 中的用户操作设置侦听器。
        searchScheduleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String selection;
                String[] args;
                if (TextUtils.isEmpty(s)) {
                    selection = null;
                    args = null;
                } else {
                    selection = ScheduleDB.COLUMN_TITLE + " like ? or " + ScheduleDB.COLUMN_DESCRIPTION + " like ?";
                    args = new String[]{"%" + s + "%", "%" + s + "%"};
                }
                // 读取数据库信息及更新视图
                // 获取所有日程信息
                Cursor cursor = getContentResolver().query(ScheduleDB.CONTENT_URI, projection, selection, args, "begin_time");
                // 日程列表初始化
                scheduleList = toScheduleList(cursor);
                LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext);
                mRecyclerView.setLayoutManager(layoutManager);
                ScheduleAdapter scheduleAdapter = new ScheduleAdapter(scheduleList);
                mRecyclerView.setAdapter(scheduleAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String selection;
                String[] args;
                if (TextUtils.isEmpty(s)) {
                    selection = null;
                    args = null;
                } else {
                    selection = ScheduleDB.COLUMN_TITLE + " like ? or " + ScheduleDB.COLUMN_DESCRIPTION + " like ?";
                    args = new String[]{"%" + s + "%", "%" + s + "%"};
                }
                // 读取数据库信息及更新视图
                // 获取所有日程信息
                Cursor cursor = getContentResolver().query(ScheduleDB.CONTENT_URI, projection, selection, args, "begin_time");
                // 日程列表初始化
                scheduleList = toScheduleList(cursor);
                LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext);
                mRecyclerView.setLayoutManager(layoutManager);
                ScheduleAdapter scheduleAdapter = new ScheduleAdapter(scheduleList);
                mRecyclerView.setAdapter(scheduleAdapter);
                return false;
            }
        });
        //当查询非空时启用显示提交按钮
        searchScheduleSearchView.setSubmitButtonEnabled(false);
        //查询提示语句
        searchScheduleSearchView.setQueryHint("请输入要查询的内容");
    }

    public List<Object> toScheduleList(Cursor cursor) {
        List<Object> temp = new ArrayList<>();
        String flag = "";
        String currentDate = MyDateFormatter.getDateFormatter(new Date(), "yyyy-MM-dd");
        String currentDateOfChina = MyDateFormatter.getDateFormatter(new Date(), "yyyy年MM月dd");

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

                // 开始时间变化到下一天的情况
                if (!beginTime.substring(0, 10).equals(flag)) {
                    flag = beginTime.substring(0, 10);
                    // 拼接日期字符串
                    String date = "";
                    // 使用工具类
                    // String 转化为 Date
                    Date myDate = parseDateFormatter(beginTime, "yyyy-MM-dd");
                    LunarCalendarFestivalUtils festival = new LunarCalendarFestivalUtils();
                    festival.initLunarCalendarInfo(getDateFormatter(myDate, "yyyy-MM-dd"));

                    date += getDateFormatter(myDate, "MM月dd") + festival.getWeekOfDate(myDate) + " ";
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

    @Override
    protected void onResume() {  // 自动更新
        super.onResume();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkedScheduleList.clear();
    }
}
