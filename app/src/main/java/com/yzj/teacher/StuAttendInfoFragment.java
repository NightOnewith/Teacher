package com.yzj.teacher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

public class StuAttendInfoFragment extends Fragment {

    private TextView tv;
    private GridView gv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setCount(int count) {
        if (tv != null)
            tv.setText("签到人数:" + count);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_dis, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv = (TextView) view.findViewById(R.id.fragment_dis_tv_count);
        tv.setText("签到人数:" + ((MainActivity) getActivity()).attendCount);
        gv = (GridView) view.findViewById(R.id.fragment_dis_gv);
        gv.setAdapter(((MainActivity) getActivity()).stuBaseAdapter);

    }
}
