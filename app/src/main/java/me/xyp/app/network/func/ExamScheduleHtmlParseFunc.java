package me.xyp.app.network.func;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.xyp.app.model.Exam;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/25.
 */
public class ExamScheduleHtmlParseFunc implements Func1<String, List<Exam>> {
    @Override
    public List<Exam> call(String html) {

        Elements tds = Jsoup.parse(html).getElementsByTag("td");
        List<Exam> list = new ArrayList<>();
        Elements trs = tds.select("tr");
        trs.remove(0);
        for (Element tr : trs) {
            Elements examElement = tr.getElementsByTag("td");
            if (examElement.size() == 8) {
                Exam exam = new Exam(
                        examElement.get(0).text().substring(1),
                        examElement.get(1).text().substring(1),
                        examElement.get(2).text().substring(1),
                        examElement.get(3).text().substring(1),
                        examElement.get(4).text().substring(1),
                        examElement.get(5).text().substring(1),
                        examElement.get(6).text().substring(1),
                        examElement.get(7).text().substring(1));
                list.add(exam);
            }
        }
        return list;
    }
}
