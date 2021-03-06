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

    // ???????????????
    public void init() {
        // ?????????????????????
        String[] projection = {ScheduleDB._ID,
                ScheduleDB.COLUMN_TITLE,
                ScheduleDB.COLUMN_BEGIN_TIME,
                ScheduleDB.COLUMN_END_TIME,
                ScheduleDB.COLUMN_DESCRIPTION
        };
        // ????????????????????????
        Cursor cursor = getContentResolver().query(ScheduleDB.CONTENT_URI, projection, null, null, "begin_time");
        // ?????????????????????
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
//                Toast.makeText(context, "??????" + position + "  " + s.get_id(), Toast.LENGTH_SHORT).show();
                if (!isChecked) {
                    // ????????????????????????
                    checkedScheduleList.removeIf(new Predicate<Integer>() {
                        @Override
                        public boolean test(Integer integer) {
                            return integer.equals(s.get_id());
                        }
                    });
                    compoundButton.setChecked(false);
                } else {
                    // ????????????????????????
//                    Toast.makeText(context, "id:" + s.get_id(), Toast.LENGTH_SHORT).show();
                    checkedScheduleList.add((Integer) s.get_id());
                    compoundButton.setChecked(true);
                }
                mTitle.setText("????????? " + checkedScheduleList.size() + " ???");
//                Toast.makeText(context, "list??????" + checkedScheduleList.size(), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(scheduleDeleteAdapter);
    }

    // ??????????????????
    public void deleteCheckedSchedules() {
        for (int i = 0; i < checkedScheduleList.size(); i++) {
            int id = (int) checkedScheduleList.get(i);
//            Toast.makeText(context, id + "", Toast.LENGTH_SHORT).show();
            Uri newUri = ContentUris.withAppendedId(ScheduleDB.CONTENT_URI, id);
//            Toast.makeText(context, newUri + "", Toast.LENGTH_SHORT).show();
            int num = getContentResolver().delete(newUri, null, null);
            if (num != 1) {
                Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
        finish();
    }

    // ???????????????????????????
    private void setupSearchView() {
        //??????????????????????????????????????????????????????(???????????????) ??????????????? ?????????????????????
        //searchScheduleSearchView.setIconified(false);
        //??????????????????????????????????????????????????????(???????????????) ??????????????? ??????????????????????????? ?????????????????????
        //searchScheduleSearchView.setIconifiedByDefault(false);
        //?????????????????????????????????????????????????????????(???????????????) ??????????????? ??????????????????????????? ?????????????????????
        searchScheduleSearchView.onActionViewExpanded();
        searchScheduleSearchView.clearFocus();

        // ?????????????????????
        String[] projection = {ScheduleDB._ID,
                ScheduleDB.COLUMN_TITLE,
                ScheduleDB.COLUMN_BEGIN_TIME,
                ScheduleDB.COLUMN_END_TIME,
                ScheduleDB.COLUMN_DESCRIPTION
        };
        Context thisContext = this;

        //??? SearchView ????????????????????????????????????
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
                // ????????????????????????????????????
                // ????????????????????????
                Cursor cursor = getContentResolver().query(ScheduleDB.CONTENT_URI, projection, selection, args, "begin_time");
                // ?????????????????????
                scheduleList = toScheduleList(cursor);
                LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext);
                mRecyclerView.setLayoutManager(layoutManager);
                ScheduleDeleteAdapter scheduleAdapter = new ScheduleDeleteAdapter(scheduleList);
                scheduleAdapter.setOnItemClickListener(new OnItemClickListener() {
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
                        if (!isChecked) {
                            // ????????????????????????
                            checkedScheduleList.removeIf(new Predicate<Integer>() {
                                @Override
                                public boolean test(Integer integer) {
                                    return integer.equals(s.get_id());
                                }
                            });
                            compoundButton.setChecked(false);
                        } else {
                            // ????????????????????????
                            checkedScheduleList.add((Integer) s.get_id());
                            compoundButton.setChecked(true);
                        }
                        mTitle.setText("????????? " + checkedScheduleList.size() + " ???");
                    }
                });
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
                // ????????????????????????????????????
                // ????????????????????????
                Cursor cursor = getContentResolver().query(ScheduleDB.CONTENT_URI, projection, selection, args, "begin_time");
                // ?????????????????????
                scheduleList = toScheduleList(cursor);
                LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext);
                mRecyclerView.setLayoutManager(layoutManager);
                ScheduleDeleteAdapter scheduleAdapter = new ScheduleDeleteAdapter(scheduleList);
                scheduleAdapter.setOnItemClickListener(new OnItemClickListener() {
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
                        if (!isChecked) {
                            // ????????????????????????
                            checkedScheduleList.removeIf(new Predicate<Integer>() {
                                @Override
                                public boolean test(Integer integer) {
                                    return integer.equals(s.get_id());
                                }
                            });
                            compoundButton.setChecked(false);
                        } else {
                            // ????????????????????????
                            checkedScheduleList.add((Integer) s.get_id());
                            compoundButton.setChecked(true);
                        }
                        mTitle.setText("????????? " + checkedScheduleList.size() + " ???");
                    }
                });
                mRecyclerView.setAdapter(scheduleAdapter);
                return false;
            }
        });
        //??????????????????????????????????????????
        searchScheduleSearchView.setSubmitButtonEnabled(false);
        //??????????????????
        searchScheduleSearchView.setQueryHint("???????????????????????????");
    }

    public List<Object> toScheduleList(Cursor cursor) {
        List<Object> temp = new ArrayList<>();
        String flag = "";
        String currentDate = MyDateFormatter.getDateFormatter(new Date(), "yyyy-MM-dd");
        String currentDateOfChina = MyDateFormatter.getDateFormatter(new Date(), "yyyy???MM???dd");

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

                // ???????????????????????????????????????
                if (!beginTime.substring(0, 10).equals(flag)) {
                    flag = beginTime.substring(0, 10);
                    // ?????????????????????
                    String date = "";
                    // ???????????????
                    // String ????????? Date
                    Date myDate = parseDateFormatter(beginTime, "yyyy-MM-dd");
                    LunarCalendarFestivalUtils festival = new LunarCalendarFestivalUtils();
                    festival.initLunarCalendarInfo(getDateFormatter(myDate, "yyyy-MM-dd"));

                    date += getDateFormatter(myDate, "MM???dd") + festival.getWeekOfDate(myDate) + " ";
                    date += "??????" + festival.getLunarMonth() + "???" + festival.getLunarDay() + " ";
                    date += festival.getLunarTerm() + " ";
                    date += festival.getSolarFestival() + " ";
                    date += festival.getLunarFestival() + " ";
                    // ?????? list ?????????????????????????????????
                    temp.add(date);
                }

                // ????????????????????????list
                temp.add(new Schedule(Integer.parseInt(_id), title, beginTime.substring(11, 16), endTime.substring(11, 16),description));
            }
        }
        return temp;
    }

    @Override
    protected void onResume() {  // ????????????
        super.onResume();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkedScheduleList.clear();
    }
}
