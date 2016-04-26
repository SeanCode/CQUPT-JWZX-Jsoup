package me.xyp.app.network.func;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.xyp.app.model.ArticleBasic;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/26.
 */
public class ArticleListHtmlParseFunc implements Func1<String, List<ArticleBasic>> {
    @Override
    public List<ArticleBasic> call(String s) {
        Element table = Jsoup.parse(s).getElementsByTag("table").first();
        Elements trs = table.getElementsByTag("tr");
        trs.remove(0);
        List<ArticleBasic> basics = new ArrayList<>();
        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            if (tds.size() == 3) {
                ArticleBasic basic = new ArticleBasic();
                Element nameWithLinkElement = tds.get(0).select("a").first();
                String href = nameWithLinkElement.attr("href");
                basic.id = href.substring(href.indexOf("=") + 1);
                basic.title = nameWithLinkElement.text();
                Element timeElement = tds.get(1);
                basic.time = timeElement.text();
                Element readNumElement = tds.get(2);
                basic.readNum = readNumElement.text();
                basics.add(basic);
            }
        }
        return basics;
    }
}
