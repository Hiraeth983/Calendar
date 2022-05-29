package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import android.view.View;
import android.widget.CompoundButton;

/**
 * 定义RecyclerView选项单击事件的回调接口
 */

public interface OnItemClickListener {
    //参数（父组件，当前单击的View,单击的View的位置，数据）
    public void onItemClick(View view, int position);

    public void onItemLongClick(View view);

    public void onItemChecked(CompoundButton compoundButton, boolean isChecked, int position);
}