package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.base.activity.BaseActivity;
import edu.zjut.androiddeveloper_8.Calendar.DB.ScheduleDB;
import edu.zjut.androiddeveloper_8.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_8.Calendar.R;
import edu.zjut.androiddeveloper_8.Calendar.Utils.LunarCalendarFestivalUtils;
import edu.zjut.androiddeveloper_8.Calendar.Utils.MyDateFormatter;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;


public class ScheduleActivity extends BaseActivity {

    // TextView
    TextView mTitle, mLocate, mBeginTime, mEndTime, mRepeatSelect, mTimeZoneSelect, mAccountSelect;

    // EditText
    EditText mScheduleTitle, mDescription;

    // ImageButton
    ImageButton mBeginTimeButton;
    ImageButton mEndTimeButton;

    // ImageView
    ImageView mCancelImageView;
    ImageView mSaveImageView;

    // Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch mTimeSlotSwitch, mImportantSwitch;

    // Other
    private Uri currentUri;
    private boolean mScheduleHasChanged = false;
    private boolean hasAllRequiredValues = false;
    private final Context context = this;
    public static final int LOCATION_CODE = 301;
    private LocationManager locationManager;
    private String locationProvider = null;

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
        mAccountSelect = findViewById(R.id.account_select);
        mImportantSwitch = findViewById(R.id.important_switch);
        mDescription = findViewById(R.id.description);
        mTimeZoneSelect = findViewById(R.id.time_zone_select);
        mCancelImageView = findViewById(R.id.cancel);
        mSaveImageView = findViewById(R.id.agree);

        // 添加触控监听
        mScheduleTitle.setOnTouchListener(onTouchListener);
        mTimeSlotSwitch.setOnTouchListener(onTouchListener);
        mBeginTime.setOnTouchListener(onTouchListener);
        mEndTime.setOnTouchListener(onTouchListener);
        mImportantSwitch.setOnTouchListener(onTouchListener);
        mDescription.setOnTouchListener(onTouchListener);

        // 添加点击监听
        mCancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mScheduleHasChanged) {
                    finish();
                    return;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
            }
        });
        mSaveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSchedule();
                if (hasAllRequiredValues) {
                    finish();
                }
            }
        });

        // 开始时间 Button 获取及添加监听
        mBeginTimeButton = findViewById(R.id.begin_time_spinner);
        mBeginTimeButton.setOnTouchListener(onTouchListener);
        mBeginTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CardDatePickerDialog.Builder(context)
                        .setTitle("选择开始时间")
                        .showBackNow(false)
                        // 神奇的时间戳少八个小时，故自行加之
                        .setDefaultTime(Calendar.getInstance().getTimeInMillis())
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
                                String begin_time = MyDateFormatter.getDateFormatter(new Date(aLong), "yyyy-MM-dd HH:mm:ss");
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
        mEndTimeButton.setOnTouchListener(onTouchListener);
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CardDatePickerDialog.Builder(context)
                        .setTitle("选择结束时间")
                        .showBackNow(false)
                        // 神奇的时间戳少八个小时，故自行加之
                        .setDefaultTime(Calendar.getInstance().getTimeInMillis())
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
                                String end_time = MyDateFormatter.getDateFormatter(new Date(aLong), "yyyy-MM-dd HH:mm:ss");
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
            // 设置新建日程默认开始和结束时间，可能是时区问题导致时间慢八个小时
            mBeginTime.setText(MyDateFormatter.getDateFormatter(new Date(new Date().getTime()), "yyyy-MM-dd HH:mm:ss"));
            mEndTime.setText(MyDateFormatter.getDateFormatter(new Date(new Date().getTime() + 60 * 60 * 1000), "yyyy-MM-dd HH:mm:ss"));
            // 获取当前地理位置
            getLocation();
        } else {
            mTitle.setText("编辑日程");
            // 加载数据
            init();
        }

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
        Cursor cursor = getContentResolver().query(currentUri, projection, null, null, null);
        Schedule schedule = toSchedule(cursor);

        // 对应视图赋值
        mScheduleTitle.setText(schedule.getTitle());
        mLocate.setText(schedule.getLocate());
        mTimeSlotSwitch.setChecked(schedule.getTime_slot().equals("全天"));
        mBeginTime.setText(schedule.getBegin_time());
        mEndTime.setText(schedule.getEnd_time());
        mRepeatSelect.setText(schedule.getRepeat());
        mImportantSwitch.setChecked(schedule.getImportant().equals("重要提醒"));
        mDescription.setText(schedule.getDescription());
        mTimeZoneSelect.setText(schedule.getTime_zone());

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

            // 构造函数
            schedule = new Schedule(Integer.parseInt(_id), title, locate, timeSlot, beginTime, endTime, repeat, important, account, description, timeZone);
        }
        return schedule;
    }

    // 保存日程
    private boolean saveSchedule() {

        // 保存结果
        String title = mScheduleTitle.getText().toString().trim();
        String locate = mLocate.getText().toString().trim();
        String time_slot = mTimeSlotSwitch.isChecked() ? "全天" : "非全天";
        String beginTime = mBeginTime.getText().toString().trim();
        String endTime = mEndTime.getText().toString().trim();
        String repeat = mRepeatSelect.getText().toString().trim();
        String important = mImportantSwitch.isChecked() ? "重要提醒" : "非重要提醒";
        String account = mAccountSelect.getText().toString().trim();
        String description = mDescription.getText().toString().trim();
        String timeZone = mTimeZoneSelect.getText().toString().trim();


        // 当用户没有输入时
        if (currentUri == null
                && TextUtils.isEmpty(title)
                && TextUtils.isEmpty(locate)
                && TextUtils.isEmpty(time_slot)
                && TextUtils.isEmpty(beginTime)
                && TextUtils.isEmpty(endTime)
                && TextUtils.isEmpty(repeat)
                && TextUtils.isEmpty(important)
                && TextUtils.isEmpty(account)
                && TextUtils.isEmpty(description)
                && TextUtils.isEmpty(timeZone)) {
            hasAllRequiredValues = true;
            return true;
        }

        ContentValues values = new ContentValues();

        // 保存标题信息
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "请输入标题！", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(ScheduleDB.COLUMN_TITLE, title);
        }

        // 保存位置信息
        if (TextUtils.isEmpty(locate)) {
            Toast.makeText(this, "获取位置失败!", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(ScheduleDB.COLUMN_LOCATE, locate);
        }

        // 保存时间段（全天/非全天）
        values.put(ScheduleDB.COLUMN_TIME_SLOT, time_slot);

        // 先判断是否为合法时间段，在保存
        if (!isValidSchedule(beginTime, endTime)) {
            Toast.makeText(this, "时间段有误!", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
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

        if (currentUri == null) {
            Uri newUri = getContentResolver().insert(ScheduleDB.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        } else {
            int result = getContentResolver().update(currentUri, values, null, null);
            if (result == 0) {
                Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            }
        }
        hasAllRequiredValues = true;
        return hasAllRequiredValues;
    }

    // 判断日程开始时间是否小于结束时间
    private boolean isValidSchedule(String beginTime, String endTime) {
        if (beginTime.compareTo(endTime) >= 0) {
            return false;
        }
        return true;
    }

    private void getLocation() {
        //1.获取位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.v("TAG", "定位方式GPS");
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            Log.v("TAG", "定位方式Network");
        } else {
//            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            // 无位置权限则设置默认值
            mLocate.setText("浙江省杭州市西湖区留下街道");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
                //3.获取上次的位置，一般第一次运行，此值为null
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
//                    Toast.makeText(this, location.getLongitude() + " " + location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                    Log.v("TAG", "获取上次的位置-经纬度：" + location.getLongitude() + "   " + location.getLatitude());
                    getAddress(location);

                } else {
                    //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                    locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                }
            }
        } else {
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                Toast.makeText(this, location.getLongitude() + " " +
                        location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                Log.v("TAG", "获取上次的位置-经纬度：" + location.getLongitude() + "   " + location.getLatitude());
                getAddress(location);

            } else {
                //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
            }
        }
    }

    public LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                //如果位置发生变化，重新显示地理位置经纬度
                Toast.makeText(ScheduleActivity.this, location.getLongitude() + " " +
                        location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                Log.v("TAG", "监视地理位置变化-经纬度：" + location.getLongitude() + "   " + location.getLatitude());
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "申请权限", Toast.LENGTH_LONG).show();
                    try {
                        List<String> providers = locationManager.getProviders(true);
                        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                            //如果是Network
                            locationProvider = LocationManager.NETWORK_PROVIDER;

                        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
                            //如果是GPS
                            locationProvider = LocationManager.GPS_PROVIDER;
                        }
                        Location location = locationManager.getLastKnownLocation(locationProvider);
                        if (location != null) {
                            /*Toast.makeText(this, location.getLongitude() + " " +
                                    location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                            Log.v("TAG", "获取上次的位置-经纬度："+location.getLongitude()+"   "+location.getLatitude());*/
                        } else {
                            // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
                        }

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "缺少权限", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    //获取地址信息:城市、街道等信息
    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
//                Toast.makeText(this, "获取地址信息：" + result.toString(), Toast.LENGTH_LONG).show();
                // 处理位置信息 例如：浙江省杭州市留下街道留和路家和西苑
                Address address = result.get(0);
//                String province = address.getAdminArea();
//                String city = address.getLocality();
//                String street = address.getSubAdminArea();
//                String road = address.getThoroughfare();
//                String feature = address.getFeatureName();
//                String locate = province + city + street + road + feature;
                // 设置界面中位置信息
                mLocate.setText(address.getAddressLine(0));
                Toast.makeText(this, "获取地址信息：" + address.getAddressLine(0), Toast.LENGTH_LONG).show();
                Log.v("TAG", "获取地址信息：" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 在新建/编辑页面按下返回键
    @Override
    public void onBackPressed() {
        if (!mScheduleHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // 新建/编辑界面的用户返回确认功能
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否放弃修改？");
        builder.setPositiveButton("放弃", discardButtonClickListener);
        builder.setNegativeButton("继续编辑", new DialogInterface.OnClickListener() {
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

}
