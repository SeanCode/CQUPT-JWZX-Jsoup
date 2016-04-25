package me.xyp.app.network.func;

import java.util.ArrayList;
import java.util.List;

import me.xyp.app.model.Course;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/25.
 */
public class CourseTableHtmlParseFunc implements Func1<String, List<Course>> {

    @Override
    public List<Course> call(String s) {
        //todo parse course table html
        return new ArrayList<>();
    }
}
