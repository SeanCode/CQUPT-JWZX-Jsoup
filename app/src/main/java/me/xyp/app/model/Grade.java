package me.xyp.app.model;

import java.util.List;

/**
 * Created by cc on 16/4/24.
 */
public class Grade {

    public String weight;
    public List<ChildGrade> childGradeList;

    public Grade(List<ChildGrade> childGradeList, String weight) {
        this.childGradeList = childGradeList;
        this.weight = weight;
    }

    public static class ChildGrade {
        public String semester;
        public String id;
        public String name;
        public String type;
        public String contribution;
        public String property;
        public String score;
        public String weight;
        public String scoreExperimental;
        public String makeUpFrequency;
        public String bxf;

        @Override
        public String toString() {
            return "ChildGrade{" +
                    "bxf='" + bxf + '\'' +
                    ", semester='" + semester + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", contribution='" + contribution + '\'' +
                    ", property='" + property + '\'' +
                    ", score='" + score + '\'' +
                    ", weight='" + weight + '\'' +
                    ", scoreExperimental='" + scoreExperimental + '\'' +
                    ", makeUpFrequency='" + makeUpFrequency + '\'' +
                    '}';
        }
    }
}
