package edu.zjut.androiddeveloper_8.Calendar.Adapter;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> mScheduleList;

    private OnItemClickListener mClickListener;

    public ScheduleAdapter(List<Object> mScheduleList) {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_schedule, parent, false);

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
        //return ??????????????????????????????????????????????????????????????????
        if (mScheduleList.get(position) instanceof String) {
            return 1;  // ???????????????
        } else {
            return 2;  // Schedule ???????????????
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

    class ScheduleHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        TextView begin_time;
        TextView end_time;
        TextView title;
        TextView description;
        private OnItemClickListener mListener;// ????????????????????????

        public ScheduleHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mListener = listener;
            begin_time = (TextView) itemView.findViewById(R.id.item_begin_time);
            end_time = (TextView) itemView.findViewById(R.id.item_end_time);
            title = (TextView) itemView.findViewById(R.id.item_title);
            description = (TextView) itemView.findViewById(R.id.item_description);

            // ????????????????????????????????????????????????
            begin_time.setOnClickListener(this);
            end_time.setOnClickListener(this);
            title.setOnClickListener(this);
            description.setOnClickListener(this);

            // ????????????
            title.setOnLongClickListener(this);
            title.setOnLongClickListener(this);
            title.setOnLongClickListener(this);
            description.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // getPosition()???ViewHolder????????????????????????????????????RecyclerView????????????????????????????????????????????????
            mListener.onItemClick(view, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onItemLongClick(view);
            return false;
        }
    }
}


