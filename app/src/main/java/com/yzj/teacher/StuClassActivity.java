package com.yzj.teacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class StuClassActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lv;
    private Button btn;
    private ArrayAdapter arrayAdapter;
    private List<String> stuInfo;
    private List<StuClass> stuClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_class);
        lv = (ListView) findViewById(R.id.activity_stu_lv);
        stuInfo = StudentManager.getManager(this).getClassInfo();
        stuClasses = StudentManager.getManager(this).getStudentClasses();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stuInfo);
        LinearLayout layout_back = (LinearLayout) findViewById(R.id.activity_stu_back);
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lv.setAdapter(arrayAdapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String[] menu = getResources().getStringArray(R.array.stu_class_menu);
                new AlertDialog.Builder(StuClassActivity.this).setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                final EditText edt_edit = new EditText(StuClassActivity.this);
                                edt_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//设置字数限制
                                edt_edit.setText(stuInfo.get(position));
                                edt_edit.setHint("名称最大长度为20个字符");
                                Utils.popupKeyboard(edt_edit);
                                edt_edit.setSelection(edt_edit.getText().length());//让光标后置
                                new AlertDialog.Builder(StuClassActivity.this).setView(edt_edit).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (edt_edit.length() > 0) {
                                            StuClass stuClass = stuClasses.get(position);
                                            stuClass.setName(edt_edit.getText().toString());
                                            StudentManager.getManager(StuClassActivity.this).update(stuClass);
                                            Toast.makeText(StuClassActivity.this, "已保存", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(StuClassActivity.this, "您的输入有误！", Toast.LENGTH_SHORT).show();
                                    }
                                }).show();

                                break;
                            case 1:
                                new AlertDialog.Builder(StuClassActivity.this).setMessage("确认删除?").setNegativeButton("取消", null).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        StudentManager.getManager(StuClassActivity.this).delete(stuClasses.get(position));
                                        Toast.makeText(StuClassActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                }).show();
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
        btn = (Button) findViewById(R.id.activity_stu_btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final EditText edt = new EditText(this);
        edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        edt.setHint("名称最大长度为20个字符");
        Utils.popupKeyboard(edt);
        new AlertDialog.Builder(this).setMessage("请输入班级名称").setView(edt).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edt.length() > 0) {
                    StudentManager.getManager(StuClassActivity.this).addClass(new StuClass(edt.getText().toString()));
                    Toast.makeText(StuClassActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(StuClassActivity.this, "您的输入有误！", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }
}
