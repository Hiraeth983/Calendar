package edu.zjut.androiddeveloper_8.Calendar.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.zjut.androiddeveloper_8.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_8.Calendar.R;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<Schedule> mScheduleList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView begin_time;
        TextView end_time;
        TextView title;

        public ViewHolder(@NonNull View view) {
            super(view);
            begin_time = (TextView) view.findViewById(R.id.item_begin_time);
            end_time = (TextView) view.findViewById(R.id.item_end_time);
            title = (TextView) view.findViewById(R.id.item_title);
        }
    }

    public ScheduleAdapter(List<Schedule> mScheduleList) {
        this.mScheduleList = mScheduleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_schedule, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = mScheduleList.get(position);
        holder.begin_time.setText(schedule.getBegin_time().toString());
        holder.end_time.setText(schedule.getEnd_time().toString());
        holder.title.setText(schedule.getTitle());
    }

    @Override
    public int getItemCount() {
        return mScheduleList.size();
    }
}


