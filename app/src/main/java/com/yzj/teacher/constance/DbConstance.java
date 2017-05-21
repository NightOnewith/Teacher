package com.yzj.teacher.constance;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建数据库
 */
public class DbConstance {
    public static final String DB_NAME = "school.db";
    public static final String TABLE_ATT_INFO = "attend_info";
    public static final String COLUMN_STU_ID = "id";
    public static final String COLUMN_STU_NAME = "name";

    public static List<String> student_sql = new ArrayList<>();

    public static final String TABLE_STU_CLASS = "stu_class";
    public static final String COLUMN_CLASS_NAME = "name";
    public static final String COLUMN_CLASS_UUID = "uuid";

    static {
        String create = "create table " + TABLE_ATT_INFO + "(" +
                COLUMN_STU_ID + " TEXT," +
                COLUMN_STU_NAME + " TEXT)";

        String createClass = "create table " + TABLE_STU_CLASS + "(" +
                COLUMN_CLASS_UUID + " TEXT PRIMARY KEY NOT AUTO_INCREMENT" +
                COLUMN_CLASS_NAME + " TEXT)";
        student_sql.add(create);
        student_sql.add(createClass);
    }

}
