package com.yzj.teacher;

import android.content.Context;

import com.yzj.teacher.constance.DbConstance;

import net.lzzy.sql.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据的一些操作，包括数据库中的操作，还有学生list中的操作
 */

class StudentManager {
    private Context context;
    private Repository<Student> studentRepository;
    private Repository<StuClass> classRepository;
    private static StudentManager manager;
    private List<Student> students;
    private List<StuClass> studentClasses;
    private List<String> classInfo;

    public static StudentManager getManager(Context context) {
        if (manager == null)
            manager = new StudentManager(context);
        return manager;
    }

    public void addAttendStu(Student student) {
        students.add(student);
    }

    public void saveAllStu() {
        for (Student student : students) {
            studentRepository.insert(student);
        }
    }

    public List<String> getClassInfo() {
        return classInfo;
    }

    public List<StuClass> getStudentClasses() {
        return studentClasses;
    }

    public void addClass(StuClass stuClass) {
        classRepository.insert(stuClass);
        studentClasses.add(stuClass);
        upStudentClass();
    }

    public void delete(StuClass stuClass) {
        classRepository.delete(stuClass);
        studentClasses.remove(stuClass);
        upStudentClass();
    }

    public List<Student> getStudents() {
        return students;
    }

    public void update(StuClass stuClass) {
        classRepository.update(stuClass);
        upStudentClass();
    }

    public boolean isExist(String id) {
        for (Student student : students)
            if (student.getId().equals(id))
                return true;
        return false;

    }

    private StudentManager(Context context) {
        this.context = context;

        try {
            studentRepository = new Repository<>(context, DbConstance.student_sql, DbConstance.DB_NAME, 1, Student.class);
            classRepository = new Repository<>(context, DbConstance.student_sql, DbConstance.DB_NAME, 1, StuClass.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        students = new ArrayList<>();

        try {
            studentClasses = classRepository.getByKeyWordFilter(null, null, true);
        } catch (Exception e) {
            e.printStackTrace();
            studentClasses = new ArrayList<>();
        }
        classInfo = new ArrayList<>();
        upStudentClass();

    }

    public void upStudentClass() {
        classInfo.clear();
        for (StuClass stuClass : studentClasses)
            classInfo.add(stuClass.getName());
    }
}
