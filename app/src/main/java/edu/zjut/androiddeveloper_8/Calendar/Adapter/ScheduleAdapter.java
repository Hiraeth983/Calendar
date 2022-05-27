package edu.zjut.androiddeveloper_8.Calendar.Adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.zjut.androiddeveloper_8.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_8.Calendar.R;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> mScheduleList;

    public ScheduleAdapter(List<Object> mScheduleList) {
        this.mScheduleList = mScheduleList;
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

            return new ScheduleHolder(view);
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

    // 自定义监听事件
    private View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO 完成跳转到日程查看界面

        }
    };

    class DateHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public DateHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_date);
        }
    }

    class ScheduleHolder extends RecyclerView.ViewHolder {
        TextView begin_time;
        TextView end_time;
        TextView title;

        public ScheduleHolder(@NonNull View itemView) {
            super(itemView);
            begin_time = (TextView) itemView.findViewById(R.id.item_begin_time);
            end_time = (TextView) itemView.findViewById(R.id.item_end_time);
            title = (TextView) itemView.findViewById(R.id.item_title);

            // 添加点击监听，跳转到日程查看界面
            begin_time.setOnClickListener(OnClickListener);
            end_time.setOnClickListener(OnClickListener);
            title.setOnClickListener(OnClickListener);
        }
    }
}


