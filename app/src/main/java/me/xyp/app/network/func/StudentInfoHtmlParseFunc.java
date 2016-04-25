package me.xyp.app.network.func;

import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.xyp.app.model.Student;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/25.
 */
public class StudentInfoHtmlParseFunc implements Func1<String, Student> {

    @Override
    public Student call(String html) {
        if (html == null) {
            return null;
        }
        Elements tds = Jsoup.parse(html).select("td");
        String basicInfo = tds.select("p").html();
        String[] basic = basicInfo.split(" 】【 ");
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(basic));
        Elements trs = tds.select("td");
        trs.remove(0);

        for (Element e : trs) {
            list.add(e.text());
        }

        return Student.getStudentFromElements(list);
    }
}
