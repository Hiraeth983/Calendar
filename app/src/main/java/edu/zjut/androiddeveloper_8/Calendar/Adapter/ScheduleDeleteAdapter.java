package edu.zjut.androiddeveloper_8.Calendar.Adapter;


import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.custom.CustomActivity;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule.OnItemClickListener;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule.ScheduleActivity;
import edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule.ScheduleShowActivity;
import edu.zjut.androiddeveloper_8.Calendar.DB.ScheduleDB;
import edu.zjut.androiddeveloper_8.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_8.Calendar.R;

public class ScheduleDeleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> mScheduleList;

    private OnItemClickListener mClickListener;

    public ScheduleDeleteAdapter(List<Object> mScheduleList) {
        this.mScheduleList = mScheduleList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
            return new DateHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_delete, parent, false);

            return new ScheduleHolder(view, mClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateHolder) {
            ((DateHolder) holder).textView.setText((String) mScheduleList.get(position));
        } else if (holder instanceof ScheduleHolder) {
            Schedule schedule = (Schedule) mScheduleList.get(position);
            ((ScheduleHolder) holder).begin_time.setText(schedule.getBegin_time());
            ((ScheduleHolder) holder).end_time.setText(schedule.getEnd_time());
            ((ScheduleHolder) holder).title.setText(schedule.getTitle());
            ((ScheduleHolder) holder).description.setText(schedule.getDescription());
        }
    }

    @Override
    public int getItemViewType(int position) {
        //return 的是一个标识，由自己定义一个不重复的数字即可
        if (mScheduleList.get(position) instanceof String) {
            return 1;  // 日期字符串
        } else {
            return 2;  // Schedule 实例化对象
        }
    }

    @Override
    public int getItemCount() {
        return mScheduleList.size();
    }

    class DateHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public DateHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_date);
        }
    }

    class ScheduleHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        TextView begin_time;
        TextView end_time;
        TextView title;
        TextView description;
        CheckBox checked;
        private OnItemClickListener mListener;// 声明自定义的接口

        public ScheduleHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mListener = listener;
            begin_time = (TextView) itemView.findViewById(R.id.item_begin_time);
            end_time = (TextView) itemView.findViewById(R.id.item_end_time);
            title = (TextView) itemView.findViewById(R.id.item_title);
            description = (TextView) itemView.findViewById(R.id.item_description);
            checked = (CheckBox) itemView.findViewById(R.id.multi);

            checked.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View view) {
            // getPosition()为ViewHolder自带的一个方法，用来获取RecyclerView当前的位置，将此作为参数，传出去
            mListener.onItemClick(view, getLayoutPosition());

        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mListener.onItemChecked(compoundButton, b, getLayoutPosition());
        }
    }
}



