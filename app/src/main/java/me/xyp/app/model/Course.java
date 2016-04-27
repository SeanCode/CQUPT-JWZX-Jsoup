package me.xyp.app.model;

import java.util.List;

/**
 * Created by cc on 16/4/24.
 */
public class Course {

    public int hash_day;
    public int hash_lesson;
    public String id;
    public String name;
    public String teacher;
    public String classroom;
    public String type;
    public String weekRaw;
    public List<Integer> week;
    public String status;
    public int period;

    @Override
    public String toString() {
        return "Course{" +
                "classroom='" + classroom + '\'' +
                ", hash_day=" + hash_day +
                ", hash_lesson=" + hash_lesson +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", teacher='" + teacher + '\'' +
                ", type='" + type + '\'' +
                ", weekRaw='" + weekRaw + '\'' +
                ", week=" + week +
                ", status='" + status + '\'' +
                ", period=" + period +
                '}';
    }
}