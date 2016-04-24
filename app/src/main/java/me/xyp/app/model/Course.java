package me.xyp.app.model;

import java.util.List;

/**
 * Created by cc on 16/4/24.
 */
public class Course {
    public int hash_day;
    public int hash_lesson;
    public int begin_lesson;
    public String day;
    public String lesson;
    public String course;
    public String teacher;
    public String classroom;
    public String rawWeek;
    public String weekModel;
    public int weekBegin;
    public int weekEnd;
    public String type;
    public List<Integer> week;
    public String status;
    // 连上几节
    public int period;
}
