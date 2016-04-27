package me.xyp.app.network.func;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.xyp.app.model.Course;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/25.
 */
public class CourseTableHtmlParseFunc implements Func1<String, List<Course>> {

    @Override
    public List<Course> call(String html) {
        List<Course> courses = new ArrayList<>();
        Element table = Jsoup.parse(html).getElementsByTag("table").get(1);
        Elements trs = table.getElementsByTag("tr");
        trs.remove(0);//remove table head
        int hash_lesson, hash_day;
        for (hash_lesson = 0; hash_lesson < 6; hash_lesson++) {
            Element tr = trs.first();
            Elements tds = tr.getElementsByTag("td");

            for (hash_day = 0; hash_day < 7; hash_day++) {
                Element td = tds.first();
                String tdHtml = td.html().substring(6);
                if (!tdHtml.startsWith("<div")) {

                    String[] courseRaw = tdHtml.split("<font color=\"336699\"></font><br>");
                    for (String raw : courseRaw) {
                        List<String> properties = Arrays.asList(raw.split("<br>"));

                        int size = properties.size();
                        if (size > 6) {
                            Course course = new Course();
                            course.hash_day = hash_day;
                            course.hash_lesson = hash_lesson;
                            course.id = properties.get(0);
                            course.name = properties.get(1);
                            course.teacher = properties.get(2);
                            course.classroom = properties.get(3);
                            String type = properties.get(4);
                            course.type = type.substring(type.indexOf(">") + 1, type.lastIndexOf("<"));
                            course.weekRaw = properties.get(5);
                            course.week = parseWeek(course.weekRaw);
                            course.status = properties.get(6);
                            course.period = size > 7 ? 3 : 2;
                            courses.add(course);
                        }
                    }
                }
                tds.remove(0);
            }
            trs.remove(0);
        }

        return courses;
    }

    private List<Integer> parseWeek(String weekRaw) {
        List<Integer> weekList = new ArrayList<>();

        if (weekRaw.contains(",")) {
            String[] weekArr = weekRaw.split(",");
            for (String s : weekArr) {
                weekList.addAll(parseWeek(s));
            }
            return weekList;
        }

        String[] temp = weekRaw.split("-");

        if (temp.length == 1) {
            weekList.add(convertWeekString2Integer(temp[0]));
            return weekList;
        }

        int begin = convertWeekString2Integer(temp[0]);

        int end = convertWeekString2Integer(temp[1]);

        if (weekRaw.contains("双")) {
            begin = begin % 2 == 0 ? begin : begin + 1;
            for (int i = begin; i <= end; i += 2) {
                weekList.add(i);
            }
        } else if (weekRaw.contains("单")) {
            begin = begin % 2 == 1 ? begin : begin + 1;
            for (int i = begin; i <= end; i += 2) {
                weekList.add(i);
            }
        } else {
            for (int i = begin; i <= end; i++) {
                weekList.add(i);
            }
        }
        return weekList;
    }

    private Integer convertWeekString2Integer(String w) {
        return Integer.parseInt(w.substring(0, !w.contains("周") ? w.length() : w.indexOf("周")));
    }
}
