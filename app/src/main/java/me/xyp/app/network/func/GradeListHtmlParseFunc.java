package me.xyp.app.network.func;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.xyp.app.model.Grade;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/25.
 */
public class GradeListHtmlParseFunc implements Func1<String, Grade> {
    @Override
    public Grade call(String html) {
        Document doc = Jsoup.parse(html);
        String weightText = doc.select("p").get(1).text();
        String weightAvg = weightText.substring(weightText.indexOf("：") + 1);
        List<Grade.ChildGrade> childGrades = new ArrayList<>();
        Element table = doc.getElementsByTag("table").get(1);
        Elements trs = table.select("tr");
        trs.remove(0);
        for (Element tr : trs) {
            Elements elements = tr.getElementsByTag("td");
            Grade.ChildGrade childGrade = new Grade.ChildGrade();
            childGrade.semester = elements.get(0).text();
            childGrade.id = elements.get(1).text();
            childGrade.name = elements.get(2).text();
            childGrade.type = elements.get(3).text();
            childGrade.contribution = elements.get(4).text();
            childGrade.property = elements.get(5).text();
            childGrade.score = elements.get(6).text().substring(1);
            childGrade.weight = elements.get(7).text().substring(1);
            childGrade.scoreExperimental = elements.get(8).text().substring(1);
            childGrade.makeUpFrequency = elements.get(9).text().substring(1);
            childGrade.bxf = elements.get(10).text();
            childGrades.add(childGrade);
        }
        Collections.reverse(childGrades);
        return new Grade(childGrades, weightAvg);
    }
}
