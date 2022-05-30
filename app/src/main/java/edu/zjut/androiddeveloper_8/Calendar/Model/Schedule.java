package edu.zjut.androiddeveloper_8.Calendar.Model;

import android.provider.BaseColumns;

import androidx.annotation.NonNull;

import java.util.Date;

public class Schedule {
    
    // id
    private int _id;
    // 标题
    private String title;
    // 地点
    private String locate;
    // 时间段（是否全天）
    private String time_slot;
    // 开始时间
    private String begin_time;
    // 结束时间
    private String end_time;
    // 重复
    private String repeat;
    // 重要提醒
    private String important;
    // 账户
    private String account;
    // 描述说明
    private String description;
    // 时区
    private String time_zone;

    public Schedule() {
    }

    public Schedule(int _id, String title, String begin_time, String end_time,String description) {
        this._id = _id;
        this.title = title;
        this.begin_time = begin_time;
        this.end_time = end_time;
        this.description = description;

    }

    public Schedule(int _id, String title, String locate, String time_slot, String begin_time, String end_time, String repeat, String important, String account, String description, String time_zone) {
        this._id = _id;
        this.title = title;
        this.locate = locate;
        this.time_slot = time_slot;
        this.begin_time = begin_time;
        this.end_time = end_time;
        this.repeat = repeat;
        this.important = important;
        this.account = account;
        this.description = description;
        this.time_zone = time_zone;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocate() {
        return locate;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public String getTime_slot() {
        return time_slot;
    }

    public void setTime_slot(String time_slot) {
        this.time_slot = time_slot;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getImportant() {
        return important;
    }

    public void setImportant(String important) {
        this.important = important;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    @NonNull
    @Override
    public String toString() {
        String result = "";
        result += "ID：" + this._id + "\n";
        result += "标题：" + this.title + "\n";
        result += "开始时间：" + this.begin_time + "\n";
        result += "结束时间：" + this.end_time + "\n";
        result += "内容：" + this.description;
        return result;
    }
}
