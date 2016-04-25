package me.xyp.app.model;

/**
 * Created by cc on 16/4/24.
 */
public class Exam {

    public String id;
    public String name;
    public String status;
    public String week;
    public String period;
    public String time;
    public String place;
    public String right;

    public Exam(String id, String name, String status, String week, String period, String place, String time, String right) {
        this.id = id;
        this.name = name;
        this.period = period;
        this.place = place;
        this.right = right;
        this.status = status;
        this.time = time;
        this.week = week;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", week='" + week + '\'' +
                ", period='" + period + '\'' +
                ", time='" + time + '\'' +
                ", place='" + place + '\'' +
                ", right='" + right + '\'' +
                '}';
    }
}
