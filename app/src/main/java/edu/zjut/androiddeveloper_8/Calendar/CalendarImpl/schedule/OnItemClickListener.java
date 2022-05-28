package edu.zjut.androiddeveloper_8.Calendar.CalendarImpl.schedule;

import android.view.View;

/**
 * 定义RecyclerView选项单击事件的回调接口
 */

public interface OnItemClickListener {
    //参数（父组件，当前单击的View,单击的View的位置，数据）
    public void onItemClick(View view, int position);

    // void onItemLongClick(View view);类似，我这里没用就不写了
    //
    //这个data是List中放的数据类型，如果这里是private List<Map> mapList;这样一个
    //然后我的每个item是这样的：
    //        HashMap map =new HashMap();
    //        map.put("img",R.drawable.delete);
    //        map.put("text","x1");
    //如果是item中只有text的话比如List<String>，那么data就改成String类型
}