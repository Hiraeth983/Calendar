package edu.zjut.androiddeveloper_8.Calendar.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.zjut.androiddeveloper_8.Calendar.Contact.db.Contact;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "calendar.db";

    public static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 初始化数据库的表结构，执行一条建表的SQL语句
        String SQL_TABLE = "CREATE TABLE " + ScheduleDB.TABLE_NAME + " ("
                + ScheduleDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 主键自增
                + ScheduleDB.COLUMN_TITLE + " TEXT NOT NULL, "
                + ScheduleDB.COLUMN_LOCATE + " TEXT NOT NULL, "
                + ScheduleDB.COLUMN_TIME_SLOT + " TEXT NOT NULL, "
                + ScheduleDB.COLUMN_BEGIN_TIME + " TEXT NOT NULL, "
                + ScheduleDB.COLUMN_END_TIME + " TEXT NOT NULL, "
                + ScheduleDB.COLUMN_REPEAT + " TEXT NOT NULL, "
                + ScheduleDB.COLUMN_IMPORTANT + " TEXT NOT NULL, "
                + ScheduleDB.COLUMN_ACCOUNT + " TEXT NOT NULL, "
                + ScheduleDB.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + ScheduleDB.COLUMN_TIME_ZONE + " TEXT DEFAULT \"GMT+8:00 中国标准时间\");";

        String SQL_TABLE1 = "CREATE TABLE " + Contact.ContactEntry.TABLE_NAME + " ("
                + Contact.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 主键自增
                + Contact.ContactEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + Contact.ContactEntry.COLUMN_TELEPHONE + " TEXT NOT NULL, "
                + Contact.ContactEntry.COLUMN_WORKPLACE + " TEXT NOT NULL, "
                + Contact.ContactEntry.COLUMN_ADDRESS + " TEXT NOT NULL, "
                + Contact.ContactEntry.COLUMN_TIME + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_TABLE);
        sqLiteDatabase.execSQL(SQL_TABLE1);
//        sqLiteDatabase.execSQL("insert into schedule values(1,'标题','浙江省杭州市','全天','2022-05-26 14:55:12','2022-05-26 14:55:30','重复','重要提醒','我的日历','描述',null);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
