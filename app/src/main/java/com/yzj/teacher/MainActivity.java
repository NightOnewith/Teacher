package com.yzj.teacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yzj.teacher.constance.Constance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int WHAT_STOP_SIGN_IN = 0;
    private long currentTime;
    private WifiManager wifiManager;
    private boolean apIsEnable = false;
    private Handler handler;
    private TextView tv_hint;
    private TextView tv_time;
    private SignInServer signInServer;
    private Timer timer;
    private boolean timeEnable;
    public int attendCount = 0;
    private TextView tv_attend_count;
    private Spinner spinner;
    private String selectClass;
    private Button btn_add;
    private ArrayAdapter arrayAdapter;
    private List<String> stuClass;
    private Spinner sp_start;
    private Spinner sp_end;
    private String startNode;
    private String endNode;
    public StuBaseAdapter stuBaseAdapter;
    private StuAttendInfoFragment stuAttendInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        stuBaseAdapter = new StuBaseAdapter(this);

        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case WHAT_STOP_SIGN_IN:
                        stopSignIn();
                        break;
                    case 1:
                        tv_time.setText(msg.getData().getString("TIME"));
                        break;
                    case 2:
                        break;
                    case 3:
                        Bundle bundle = msg.getData();
                        StudentManager.getManager(MainActivity.this).addAttendStu(new Student(bundle.getString("NAME"), bundle.getString("ID")));
                        stuBaseAdapter.notifyDataSetChanged(); //刷新界面
                        attendCount++;
                        tv_attend_count.setText(attendCount + "");
                        stuAttendInfoFragment.setCount(attendCount);
                        break;
                }
            }
        };
        timeEnable = false;
        signInServer = new SignInServer(handler, this);

        RelativeLayout rl_sign_in = (RelativeLayout) findViewById(R.id.activity_main_rly_sign_in);
        rl_sign_in.setOnClickListener(this);
        tv_attend_count = (TextView) findViewById(R.id.activity_main_tv_count);
        tv_attend_count.setOnClickListener(this);
        findViewById(R.id.activity_main_look_info).setOnClickListener(this);
        tv_hint = (TextView) findViewById(R.id.activity_main_tv_hint);
        tv_time = (TextView) findViewById(R.id.activity_main_tv_time);
        btn_add = (Button) findViewById(R.id.activity_main_btn_manage);
        btn_add.setOnClickListener(this);
        spinner = (Spinner) findViewById(R.id.activity_main_sp_select);
        stuClass = StudentManager.getManager(this).getClassInfo();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stuClass);
        spinner.setAdapter(arrayAdapter);
        ArrayAdapter nodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.adapter_node));
        sp_start = (Spinner) findViewById(R.id.start_node);
        sp_start.setAdapter(nodeAdapter);
        stuAttendInfoFragment = new StuAttendInfoFragment();
        sp_end = (Spinner) findViewById(R.id.end_node);
        sp_end.setAdapter(nodeAdapter);
        sp_start.setOnItemSelectedListener(this);
        sp_end.setOnItemSelectedListener(this);
        findViewById(R.id.activity_main_tv_count_hint).setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectClass = stuClass.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeWifiHotspot(); //关闭热点
        try {
            signInServer.getServerSocket().close();  //关闭socket
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建Wifi热点
     */
    private boolean createWifiHotspot() {
        if (wifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            wifiManager.setWifiEnabled(false);
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = Constance.WIFI_SSID;
        //通过反射调用设置热点
        try {
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean enable = (Boolean) method.invoke(wifiManager, config, true);
            if (enable) {
                Toast.makeText(getApplicationContext(), "热点已开启 SSID:" + Constance.WIFI_SSID, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "创建热点失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "创建热点失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    /**
     * 关闭WiFi热点
     */
    public void closeWifiHotspot() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
            Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(wifiManager, config, false);
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopSignIn() {
        apIsEnable = false;
        timeEnable = false;
        closeWifiHotspot();

        if (timer != null)
            timer.cancel();
        tv_hint.setText("点名");
        tv_time.setText("");
    }

    public void startSignIn() {

        View view = getLayoutInflater().inflate(R.layout.view_time_pick, null);
        final NumberPicker m = (NumberPicker) view.findViewById(R.id.view_m);
        final NumberPicker s = (NumberPicker) view.findViewById(R.id.view_s);

        m.setMinValue(0);
        m.setMaxValue(10);
        s.setMinValue(0);
        s.setMaxValue(59);
        m.setValue(Constance.DEFAULT_SIGN_IN_TIME);
        new AlertDialog.Builder(this).setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (StudentManager.getManager(getApplicationContext()).getStudents().size() > 0)
                    new AlertDialog.Builder(getApplicationContext()).setMessage("是否重新开始签到").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StudentManager.getManager(getApplicationContext()).getStudents().clear();
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }).setNegativeButton("取消", null).show();

                if (!createWifiHotspot())
                    return;
                timeEnable = true;
                apIsEnable = true;
                currentTime = m.getValue() * 60000 + s.getValue() * 1000;
                tv_hint.setText("点名中...");
                signInServer.startSignInServe();
                timer = new Timer();
                timer.schedule(new TimerTask() { //只执行一次
                    @Override
                    public void run() {
                        if (!timeEnable || currentTime < 0) {
                            handler.sendEmptyMessage(WHAT_STOP_SIGN_IN);
                            return;
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        //格式化时间
                        bundle.putString("TIME", com.yzj.teacher.constance.Utils.simpleDate.format(new Date(currentTime)));
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        currentTime -= 1000;
                    }
                }, 0, 1000);
            }
        }).setMessage("设置签到时间").show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        arrayAdapter.notifyDataSetChanged();
        spinner.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_tv_count_hint:
            case R.id.activity_main_tv_count:
            case R.id.activity_main_look_info:
                tv_attend_count.setText(String.valueOf(attendCount));
                //fragment的切换
                getSupportFragmentManager().beginTransaction().add(R.id.main_container, stuAttendInfoFragment).addToBackStack(null).commit();
                break;
            case R.id.activity_main_rly_sign_in:
                if (selectClass == null) {
                    Toast.makeText(MainActivity.this, "请先选择一个要点名的班级", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!apIsEnable) {
                    startSignIn();
                } else {
                    stopSignIn();
                    if (attendCount > 0)
                        new AlertDialog.Builder(this).setMessage("是否保存点名数据?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StudentManager.getManager(getApplicationContext()).saveAllStu();
                                Toast.makeText(getApplicationContext(), "已保存", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null).show();
                }
                break;
            case R.id.activity_main_btn_manage:
                startActivityForResult(new Intent(this, StuClassActivity.class), 0);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.id.start_node:
                startNode = (String) parent.getItemAtPosition(position);
                break;
            case R.id.end_node:
                endNode = (String) parent.getItemAtPosition(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
