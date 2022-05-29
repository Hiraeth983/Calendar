package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        } else {
            mTitle.setText("编辑日程");

        }
        getLocation();
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
                String province = address.getAdminArea();
                String city = address.getLocality();
                String street = address.getSubAdminArea();
                String road = address.getThoroughfare();
                String feature = address.getFeatureName();
                String locate = province + city + street + road + feature;
                // 设置界面中位置信息
                mLocate.setText(locate);
                Log.v("TAG", "获取地址信息：" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
