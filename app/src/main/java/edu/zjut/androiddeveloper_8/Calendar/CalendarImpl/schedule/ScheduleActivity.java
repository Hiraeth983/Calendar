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

    // ????????????????????????????????????
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
        // ??????View
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

        // ??????????????????
        mScheduleTitle.setOnTouchListener(onTouchListener);
        mTimeSlotSwitch.setOnTouchListener(onTouchListener);
        mBeginTime.setOnTouchListener(onTouchListener);
        mEndTime.setOnTouchListener(onTouchListener);
        mImportantSwitch.setOnTouchListener(onTouchListener);
        mDescription.setOnTouchListener(onTouchListener);

        // ??????????????????
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

        // ???????????? Button ?????????????????????
        mBeginTimeButton = findViewById(R.id.begin_time_spinner);
        mBeginTimeButton.setOnTouchListener(onTouchListener);
        mBeginTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CardDatePickerDialog.Builder(context)
                        .setTitle("??????????????????")
                        .showBackNow(false)
                        // ???????????????????????????????????????????????????
                        .setDefaultTime(Calendar.getInstance().getTimeInMillis())
                        .setMaxTime(0)
                        .setMinTime(0)
                        .setWrapSelectorWheel(false)
                        .setThemeColor(R.color.line_color)
                        .showDateLabel(true)
                        .showFocusDateInfo(true)
                        .setTouchHideable(true)
                        .setLabelText("???", "???", "???", "???", "???", "???")
                        .setOnChoose("??????", new Function1<Long, Unit>() {
                            @Override
                            public Unit invoke(Long aLong) {
                                String begin_time = MyDateFormatter.getDateFormatter(new Date(aLong), "yyyy-MM-dd HH:mm:ss");
//                                Toast.makeText(context, begin_time, Toast.LENGTH_SHORT).show();
                                mBeginTime.setText(begin_time);
                                return null;
                            }
                        })
                        .setOnCancel("??????", new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                return null;
                            }
                        })
                        .build().show();
            }
        });

        // ???????????? Button ?????????????????????
        mEndTimeButton = findViewById(R.id.end_time_spinner);
        mEndTimeButton.setOnTouchListener(onTouchListener);
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CardDatePickerDialog.Builder(context)
                        .setTitle("??????????????????")
                        .showBackNow(false)
                        // ???????????????????????????????????????????????????
                        .setDefaultTime(Calendar.getInstance().getTimeInMillis())
                        .setMaxTime(0)
                        .setMinTime(0)
                        .setWrapSelectorWheel(false)
                        .setThemeColor(R.color.line_color)
                        .showDateLabel(true)
                        .showFocusDateInfo(true)
                        .setTouchHideable(true)
                        .setLabelText("???", "???", "???", "???", "???", "???")
                        .setOnChoose("??????", new Function1<Long, Unit>() {
                            @Override
                            public Unit invoke(Long aLong) {
                                String end_time = MyDateFormatter.getDateFormatter(new Date(aLong), "yyyy-MM-dd HH:mm:ss");
//                                Toast.makeText(context, begin_time, Toast.LENGTH_SHORT).show();
                                mEndTime.setText(end_time);
                                return null;
                            }
                        })
                        .setOnCancel("??????", new Function0<Unit>() {
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
        // ??????URI
        Intent intent = getIntent();
        currentUri = intent.getData();
        Log.i("currentContactUri", currentUri + "");

        // ?????????????????????????????????????????????
        if (currentUri == null) {
            mTitle.setText("????????????");
            // ????????????????????????????????????????????????????????????????????????????????????????????????
            mBeginTime.setText(MyDateFormatter.getDateFormatter(new Date(new Date().getTime()), "yyyy-MM-dd HH:mm:ss"));
            mEndTime.setText(MyDateFormatter.getDateFormatter(new Date(new Date().getTime() + 60 * 60 * 1000), "yyyy-MM-dd HH:mm:ss"));
            // ????????????????????????
            getLocation();
        } else {
            mTitle.setText("????????????");
            // ????????????
            init();
        }

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
        Cursor cursor = getContentResolver().query(currentUri, projection, null, null, null);
        Schedule schedule = toSchedule(cursor);

        // ??????????????????
        mScheduleTitle.setText(schedule.getTitle());
        mLocate.setText(schedule.getLocate());
        mTimeSlotSwitch.setChecked(schedule.getTime_slot().equals("??????"));
        mBeginTime.setText(schedule.getBegin_time());
        mEndTime.setText(schedule.getEnd_time());
        mRepeatSelect.setText(schedule.getRepeat());
        mImportantSwitch.setChecked(schedule.getImportant().equals("????????????"));
        mDescription.setText(schedule.getDescription());
        mTimeZoneSelect.setText(schedule.getTime_zone());

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

            // ????????????
            schedule = new Schedule(Integer.parseInt(_id), title, locate, timeSlot, beginTime, endTime, repeat, important, account, description, timeZone);
        }
        return schedule;
    }

    // ????????????
    private boolean saveSchedule() {

        // ????????????
        String title = mScheduleTitle.getText().toString().trim();
        String locate = mLocate.getText().toString().trim();
        String time_slot = mTimeSlotSwitch.isChecked() ? "??????" : "?????????";
        String beginTime = mBeginTime.getText().toString().trim();
        String endTime = mEndTime.getText().toString().trim();
        String repeat = mRepeatSelect.getText().toString().trim();
        String important = mImportantSwitch.isChecked() ? "????????????" : "???????????????";
        String account = mAccountSelect.getText().toString().trim();
        String description = mDescription.getText().toString().trim();
        String timeZone = mTimeZoneSelect.getText().toString().trim();


        // ????????????????????????
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

        // ??????????????????
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(ScheduleDB.COLUMN_TITLE, title);
        }

        // ??????????????????
        if (TextUtils.isEmpty(locate)) {
            Toast.makeText(this, "??????????????????!", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(ScheduleDB.COLUMN_LOCATE, locate);
        }

        // ????????????????????????/????????????
        values.put(ScheduleDB.COLUMN_TIME_SLOT, time_slot);

        // ?????????????????????????????????????????????
        if (!isValidSchedule(beginTime, endTime)) {
            Toast.makeText(this, "???????????????!", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        }
        values.put(ScheduleDB.COLUMN_BEGIN_TIME, beginTime);
        values.put(ScheduleDB.COLUMN_END_TIME, endTime);

        // ????????????
        values.put(ScheduleDB.COLUMN_REPEAT, repeat);

        // ?????????????????????
        values.put(ScheduleDB.COLUMN_IMPORTANT, important);

        // ????????????
        values.put(ScheduleDB.COLUMN_ACCOUNT, account);

        // ????????????
        values.put(ScheduleDB.COLUMN_DESCRIPTION, description);

        // ????????????
        values.put(ScheduleDB.COLUMN_TIME_ZONE, timeZone);

        if (currentUri == null) {
            Uri newUri = getContentResolver().insert(ScheduleDB.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            }
        } else {
            int result = getContentResolver().update(currentUri, values, null, null);
            if (result == 0) {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            }
        }
        hasAllRequiredValues = true;
        return hasAllRequiredValues;
    }

    // ????????????????????????????????????????????????
    private boolean isValidSchedule(String beginTime, String endTime) {
        if (beginTime.compareTo(endTime) >= 0) {
            return false;
        }
        return true;
    }

    private void getLocation() {
        //1.?????????????????????
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //2.????????????????????????GPS??????NetWork
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //?????????GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.v("TAG", "????????????GPS");
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //?????????Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            Log.v("TAG", "????????????Network");
        } else {
//            Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
            // ?????????????????????????????????
            mLocate.setText("???????????????????????????????????????");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //??????????????????????????????????????????????????????????????????????????????????????????
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //????????????
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
                //3.?????????????????????????????????????????????????????????null
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
//                    Toast.makeText(this, location.getLongitude() + " " + location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                    Log.v("TAG", "?????????????????????-????????????" + location.getLongitude() + "   " + location.getLatitude());
                    getAddress(location);

                } else {
                    //????????????????????????????????????????????????????????????????????????????????????minTime???????????????minDistace
                    locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                }
            }
        } else {
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                Toast.makeText(this, location.getLongitude() + " " +
                        location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                Log.v("TAG", "?????????????????????-????????????" + location.getLongitude() + "   " + location.getLatitude());
                getAddress(location);

            } else {
                //????????????????????????????????????????????????????????????????????????????????????minTime???????????????minDistace
                locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
            }
        }
    }

    public LocationListener locationListener = new LocationListener() {
        // Provider??????????????????????????????????????????????????????????????????????????????????????????
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider???enable???????????????????????????GPS?????????
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider???disable???????????????????????????GPS?????????
        @Override
        public void onProviderDisabled(String provider) {
        }

        //??????????????????????????????????????????Provider?????????????????????????????????????????????
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                //????????????????????????????????????????????????????????????
                Toast.makeText(ScheduleActivity.this, location.getLongitude() + " " +
                        location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                Log.v("TAG", "????????????????????????-????????????" + location.getLongitude() + "   " + location.getLatitude());
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
                    Toast.makeText(this, "????????????", Toast.LENGTH_LONG).show();
                    try {
                        List<String> providers = locationManager.getProviders(true);
                        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                            //?????????Network
                            locationProvider = LocationManager.NETWORK_PROVIDER;

                        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
                            //?????????GPS
                            locationProvider = LocationManager.GPS_PROVIDER;
                        }
                        Location location = locationManager.getLastKnownLocation(locationProvider);
                        if (location != null) {
                            /*Toast.makeText(this, location.getLongitude() + " " +
                                    location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                            Log.v("TAG", "?????????????????????-????????????"+location.getLongitude()+"   "+location.getLatitude());*/
                        } else {
                            // ????????????????????????????????????????????????????????????????????????????????????minTime???????????????minDistace
                            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
                        }

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "????????????", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    //??????????????????:????????????????????????
    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
//                Toast.makeText(this, "?????????????????????" + result.toString(), Toast.LENGTH_LONG).show();
                // ?????????????????? ????????????????????????????????????????????????????????????
                Address address = result.get(0);
//                String province = address.getAdminArea();
//                String city = address.getLocality();
//                String street = address.getSubAdminArea();
//                String road = address.getThoroughfare();
//                String feature = address.getFeatureName();
//                String locate = province + city + street + road + feature;
                // ???????????????????????????
                mLocate.setText(address.getAddressLine(0));
                Toast.makeText(this, "?????????????????????" + address.getAddressLine(0), Toast.LENGTH_LONG).show();
                Log.v("TAG", "?????????????????????" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // ?????????/???????????????????????????
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

    // ??????/???????????????????????????????????????
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("?????????????????????");
        builder.setPositiveButton("??????", discardButtonClickListener);
        builder.setNegativeButton("????????????", new DialogInterface.OnClickListener() {
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
