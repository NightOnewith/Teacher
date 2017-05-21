package com.yzj.teacher;

import android.database.Cursor;

import com.yzj.teacher.constance.DbConstance;

import net.lzzy.sql.Sqlable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class StuClass extends Sqlable {
    String name;
    String uuid;

    StuClass(String name) {
        this.name = name;
        uuid = UUID.randomUUID().toString();
    }

    public StuClass() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getTableName() {
        return DbConstance.TABLE_STU_CLASS;
    }

    @Override
    public String getKeyVal() {
        return uuid;
    }

    @Override
    public String getKeyCol() {
        return DbConstance.COLUMN_CLASS_UUID;
    }

    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(DbConstance.COLUMN_CLASS_NAME, name);
        map.put(DbConstance.COLUMN_CLASS_UUID, uuid);
        return map;
    }

    @Override
    public void setCursor(Cursor cursor) {
        name = cursor.getString(cursor.getColumnIndex(DbConstance.COLUMN_CLASS_NAME));
        uuid = cursor.getString(cursor.getColumnIndex(DbConstance.COLUMN_CLASS_UUID));
    }
}
