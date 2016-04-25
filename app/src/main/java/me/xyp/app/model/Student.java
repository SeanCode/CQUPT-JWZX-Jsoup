package me.xyp.app.model;

import java.util.List;

/**
 * Created by cc on 16/4/24.
 */
public class Student {

    public String stuNum;
    public String name;
    public String classNum;
    public String gender;
    public String oldName;
    public String birthday;
    public String avatar;
    public String nation;
    public String college;
    public String major;
    public String majorPeriod;
    public String grade;
    public String majorKind;

    @Override
    public String toString() {
        return "Student{" +
                "avatar='" + avatar + '\'' +
                ", stuNum='" + stuNum + '\'' +
                ", name='" + name + '\'' +
                ", classNum='" + classNum + '\'' +
                ", gender='" + gender + '\'' +
                ", oldName='" + oldName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", nation='" + nation + '\'' +
                ", college='" + college + '\'' +
                ", major='" + major + '\'' +
                ", majorPeriod='" + majorPeriod + '\'' +
                ", grade='" + grade + '\'' +
                ", majorKind='" + majorKind + '\'' +
                '}';
    }

    public static Student getStudentFromElements(List<String> elements) {
        Student student = new Student();

        int size = elements.size();

        student.stuNum = "学号：" + (size > 0 ? elements.get(0).substring(2) : "");//【 2012121212 -> 2012121212
        student.name = "姓名：" + (size > 1 ? elements.get(1) : "");
        student.classNum = "班级：" + (size > 2 ? elements.get(2) : "");
        student.gender = "性别：" + (size > 3 ? elements.get(3).substring(0, 1) : "");// 男 】 -> 男
        student.oldName = size > 4 ? elements.get(4) : "";//曾用名：xx -> xx
        student.birthday = size > 5 ? elements.get(5) : "";//生日：19930101 -> 19930101
        student.avatar = size > 6 ? elements.get(6) : "";
        student.nation = size > 7 ? elements.get(7) : "";//民族：汉族 -> 汉族
        student.college = size > 8 ? elements.get(8) : "";
        student.major = size > 9 ? elements.get(9) : "";
        student.majorPeriod = size > 10 ? elements.get(10) : "";
        student.grade = size > 11 ? elements.get(11) : "";
        student.majorKind = size > 12 ? elements.get(12) : "";

        return student;
    }
}
