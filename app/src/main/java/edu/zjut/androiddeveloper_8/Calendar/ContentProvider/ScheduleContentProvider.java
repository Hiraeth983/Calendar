package edu.zjut.androiddeveloper_8.Calendar.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import edu.zjut.androiddeveloper_8.Calendar.DB.DBHelper;
import edu.zjut.androiddeveloper_8.Calendar.DB.Schedule;

public class ScheduleContentProvider extends ContentProvider {

    // DBHelper 注入
    public DBHelper mDBHelper;

    // id 标识
    public static final int SCHEDULES_ID = 1;
    public static final int SCHEDULE_ID = 2;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // 单条和多条数据的匹配
    static {
        uriMatcher.addURI(Schedule.CONTENT_AUTHORITY, Schedule.PATH_CONTACTS, SCHEDULES_ID);
        // # 此处代表列名
        uriMatcher.addURI(Schedule.CONTENT_AUTHORITY, Schedule.PATH_CONTACTS + "/#", SCHEDULE_ID);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        // 初始化数据库信息
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
//        db.execSQL("delete from schedule");
        db.execSQL("insert into schedule values(null,'标题','浙江省杭州市','全天','2022-05-26 14:55:12','2022-05-26 14:55:30','重复','重要提醒','我的日历','描述',null);");
        return true;
    }

    /**
     * @param uri      Table
     * @param strings  columns
     * @param s        where column = ?
     * @param strings1 value
     * @param s1       order by column
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        // 查询获取只读
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        // 初始化cursor
        Cursor cursor;
        Log.i("URI", uri + "");
        // 查询可能会查到多个结果或单个结果
        int match = uriMatcher.match(uri);
        // 匹配
        switch (match) {
            case SCHEDULES_ID:
                cursor = db.query(Schedule.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;

            case SCHEDULE_ID:
                s = Schedule._ID + " = ?";
                strings1 = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(Schedule.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;

            default:
                throw new IllegalArgumentException("Can't Query" + uri);
        }

        // 设置 URI 通知，自动更新界面
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // 验证数据的合法性（非空）
        // TODO 数据合法性数据类型未知，需验证，更新也需要
        if (contentValues.containsKey(Schedule.COLUMN_TITLE)) {
            String title = contentValues.getAsString(Schedule.COLUMN_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Title is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_LOCATE)) {
            String locate = contentValues.getAsString(Schedule.COLUMN_LOCATE);
            if (locate == null) {
                throw new IllegalArgumentException("Locate is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_TIME_SLOT)) {
            String time_slot = contentValues.getAsString(Schedule.COLUMN_TIME_SLOT);
            if (time_slot == null) {
                throw new IllegalArgumentException("Time_slot is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_BEGIN_TIME)) {
            String begin_time = contentValues.getAsString(Schedule.COLUMN_BEGIN_TIME);
            if (begin_time == null) {
                throw new IllegalArgumentException("Begin_time is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_END_TIME)) {
            String end_time = contentValues.getAsString(Schedule.COLUMN_END_TIME);
            if (end_time == null) {
                throw new IllegalArgumentException("End_time is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_REPEAT)) {
            String repeat = contentValues.getAsString(Schedule.COLUMN_REPEAT);
            if (repeat == null) {
                throw new IllegalArgumentException("Repeat is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_IMPORTANT)) {
            String important = contentValues.getAsString(Schedule.COLUMN_IMPORTANT);
            if (important == null) {
                throw new IllegalArgumentException("Important is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_ACCOUNT)) {
            String account = contentValues.getAsString(Schedule.COLUMN_ACCOUNT);
            if (account == null) {
                throw new IllegalArgumentException("Account is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_DESCRIPTION)) {
            String description = contentValues.getAsString(Schedule.COLUMN_DESCRIPTION);
            if (description == null) {
                throw new IllegalArgumentException("Description is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_TIME_ZONE)) {
            String time_zone = contentValues.getAsString(Schedule.COLUMN_TIME_ZONE);
            if (time_zone == null) {
                throw new IllegalArgumentException("Time_zone is required");
            }
        }


        // 插入获取可写的数据库
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        long id = database.insert(Schedule.TABLE_NAME, null, contentValues);

        // 插入失败报错
        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * @param uri     table
     * @param s       where column = ?
     * @param strings value
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int result;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case SCHEDULES_ID:
                result = db.delete(Schedule.TABLE_NAME, s, strings);
                break;

            case SCHEDULE_ID:
                s = Schedule._ID + " = ?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = db.delete(Schedule.TABLE_NAME, s, strings);
                break;

            default:
                throw new IllegalArgumentException("Can't execute delete" + uri);
        }
        if (result != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    /**
     * @param uri           table
     * @param contentValues values
     * @param s             where column = ?
     * @param strings       value
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        // 验证数据的合法性（非空）
        if (contentValues.containsKey(Schedule.COLUMN_TITLE)) {
            String title = contentValues.getAsString(Schedule.COLUMN_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Title is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_LOCATE)) {
            String locate = contentValues.getAsString(Schedule.COLUMN_LOCATE);
            if (locate == null) {
                throw new IllegalArgumentException("Locate is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_TIME_SLOT)) {
            String time_slot = contentValues.getAsString(Schedule.COLUMN_TIME_SLOT);
            if (time_slot == null) {
                throw new IllegalArgumentException("Time_slot is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_BEGIN_TIME)) {
            String begin_time = contentValues.getAsString(Schedule.COLUMN_BEGIN_TIME);
            if (begin_time == null) {
                throw new IllegalArgumentException("Begin_time is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_END_TIME)) {
            String end_time = contentValues.getAsString(Schedule.COLUMN_END_TIME);
            if (end_time == null) {
                throw new IllegalArgumentException("End_time is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_REPEAT)) {
            String repeat = contentValues.getAsString(Schedule.COLUMN_REPEAT);
            if (repeat == null) {
                throw new IllegalArgumentException("Repeat is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_IMPORTANT)) {
            String important = contentValues.getAsString(Schedule.COLUMN_IMPORTANT);
            if (important == null) {
                throw new IllegalArgumentException("Important is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_ACCOUNT)) {
            String account = contentValues.getAsString(Schedule.COLUMN_ACCOUNT);
            if (account == null) {
                throw new IllegalArgumentException("Account is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_DESCRIPTION)) {
            String description = contentValues.getAsString(Schedule.COLUMN_DESCRIPTION);
            if (description == null) {
                throw new IllegalArgumentException("Description is required");
            }
        }
        if (contentValues.containsKey(Schedule.COLUMN_TIME_ZONE)) {
            String time_zone = contentValues.getAsString(Schedule.COLUMN_TIME_ZONE);
            if (time_zone == null) {
                throw new IllegalArgumentException("Time_zone is required");
            }
        }

        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        int result;
        switch (match) {
            case SCHEDULES_ID:
                result = database.update(Schedule.TABLE_NAME, contentValues, s, strings);
                if (result != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return result;
            case SCHEDULE_ID:
                s = Schedule._ID + " = ?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = database.update(Schedule.TABLE_NAME, contentValues, s, strings);
                if (result != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return result;

            default:
                throw new IllegalArgumentException("Can't update the schedule");
        }
    }
}
