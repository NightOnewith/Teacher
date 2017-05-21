package com.yzj.teacher;

import android.database.Cursor;

import com.yzj.teacher.constance.DbConstance;

import net.lzzy.sql.Sqlable;

import java.util.HashMap;
import java.util.Map;


public class Student extends Sqlable {
    private String name;
    private String id;
    private String node;

    public Student() {
    }

    Student(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getTableName() {
        return DbConstance.TABLE_ATT_INFO;
    }

    @Override
    public String getKeyVal() {
        return id;
    }

    @Override
    public String getKeyCol() {
        return DbConstance.COLUMN_STU_ID;
    }

    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(DbConstance.COLUMN_STU_ID, id);
        map.put(DbConstance.COLUMN_STU_NAME, name);
        return map;
    }

    @Override
    public void setCursor(Cursor cursor) {
        name = cursor.getString(cursor.getColumnIndex(DbConstance.COLUMN_STU_NAME));
        id = cursor.getString(cursor.getColumnIndex(DbConstance.COLUMN_STU_ID));
    }
}
