package com.yzj.teacher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class StuBaseAdapter extends BaseAdapter {
    private Context context;
    private final List<Student> students;

    public StuBaseAdapter(Context context) {
        this.context = context;
        students = StudentManager.getManager(context).getStudents();
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Student getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_stu_attend, null);
            TextView tv_id = (TextView) convertView.findViewById(R.id.item_id);
            TextView tv_name = (TextView) convertView.findViewById(R.id.item_name);
            tv_id.setText(getItem(position).getId());
            tv_name.setText(getItem(position).getName());
        }
        return convertView;
    }
}
