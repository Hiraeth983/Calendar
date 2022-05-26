package edu.zjut.androiddeveloper_8.Calendar.Schedule;

import android.net.Uri;
import android.provider.BaseColumns;

import java.io.Serializable;

public class Schedule implements Serializable, BaseColumns {

    public static final String CONTENT_AUTHORITY = "edu.zjut.androiddeveloper_8.Calendar";

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CONTACTS = "schedule";

    // content://edu.zjut.androiddeveloper_8.Calendar/schedule
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_CONTACTS);

    public static final String TABLE_NAME = "schedule";

    // 日程字段
    // id
    public static final String _ID = BaseColumns._ID;
    // 标题
    public static final String COLUMN_TITLE = "title";
    // 地点
    public static final String COLUMN_LOCATE = "locate";
    // 时间段（是否全天）
    public static final String COLUMN_TIME_SLOT = "time_slot";
    // 开始时间
    public static final String COLUMN_BEGIN_TIME = "begin_time";
    // 结束时间
    public static final String COLUMN_END_TIME = "end_time";
    // 重复
    public static final String COLUMN_REPEAT = "repeat";
    // 重要提醒
    public static final String COLUMN_IMPORTANT = "important";
    // 账户
    public static final String COLUMN_ACCOUNT = "account";
    // 描述说明
    public static final String COLUMN_DESCRIPTION = "description";
    // 时区
    public static final String COLUMN_TIME_ZONE = "time_zone";

}
