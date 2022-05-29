package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.base.activity;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import edu.zjut.androiddeveloper_8.Calendar.MainActivity;
import edu.zjut.androiddeveloper_8.Calendar.SMS.SMSReceiverService;

/**
 * 基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static boolean isMiUi = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            System.out.println("Service Connect Success!");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("Service Disconnect!");

        }
    };
    protected void initWindow() {

    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(getLayoutId());
        initView();
        initData();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 设置状态栏颜色
        getReadPermissions();
        Intent i = new Intent(this, SMSReceiverService.class);
        bindService(i,serviceConnection,BIND_AUTO_CREATE);

    }

    /**
     * 设置小米黑色状态栏字体
     */
    @SuppressLint("PrivateApi")
    private void setMIUIStatusBarDarkMode() {
        if (isMiUi) {
            Class<? extends Window> clazz = getWindow().getClass();
            try {
                int darkModeFlag;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(getWindow(), darkModeFlag, darkModeFlag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 静态域，获取系统版本是否基于MIUI
     */

    static {
        try {
            @SuppressLint("PrivateApi") Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getStringMethod = sysClass.getDeclaredMethod("get", String.class);
            String version = (String) getStringMethod.invoke(sysClass, "ro.miui.ui.version.name");
            isMiUi = version.compareTo("V6") >= 0 && Build.VERSION.SDK_INT < 24;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置魅族手机状态栏图标颜色风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    public static boolean setMeiZuDarkMode(Window window, boolean dark) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 24) {
            return false;
        }
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @SuppressLint("InlinedApi")
    private int getStatusBarLightMode() {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isMiUi) {
                result = 1;
            } else if (setMeiZuDarkMode(getWindow(), true)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 3;
            }
        }
        return result;
    }


    @SuppressLint("InlinedApi")
    protected void setStatusBarDarkMode() {
        int type = getStatusBarLightMode();
        if (type == 1) {
            setMIUIStatusBarDarkMode();
        } else if (type == 2) {
            setMeiZuDarkMode(getWindow(), true);
        } else if (type == 3) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    private void getReadPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    | ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                    | ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS) | ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//是否请求过该权限
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, 10001);
                } else {//没有则请求获取权限，示例权限是：存储权限和短信权限，需要其他权限请更改或者替换
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);
                }
            } else {//如果已经获取到了权限则直接进行下一步操作
                Log.e(TAG, "onRequestPermissionsResult");
            }
        }

    }

    /**
     * 一个或多个权限请求结果回调
     * 当点击了不在询问，但是想要实现某个功能，必须要用到权限，可以提示用户，引导用户去设置
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10001:
                for (int i = 0; i < grantResults.length; i++) {
//                   如果拒绝获取权限
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (flag) {
                            getReadPermissions();
                            return;//用户权限是一个一个的请求的，只要有拒绝，剩下的请求就可以停止，再次请求打开权限了
                        } else { // 勾选不再询问，并拒绝
//                            Toast.makeText(this, "请到设置中打开权限", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
//                Toast.makeText(LoginActivity.this, "权限开启完成",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

    }
}
