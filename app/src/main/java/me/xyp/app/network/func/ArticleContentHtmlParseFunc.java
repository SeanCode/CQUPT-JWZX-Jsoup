package me.xyp.app.network.func;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.xyp.app.config.Const;
import me.xyp.app.model.Article;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/26.
 */
public class ArticleContentHtmlParseFunc implements Func1<String, Article> {
    @Override
    public Article call(String html) {
        Element table = Jsoup.parse(html).getElementsByTag("table").get(1);
        Article article = new Article();
        article.contentHtml = table.select("div.contentShow").first().outerHtml();
        Element ul = table.getElementsByTag("ul").first();
        Elements liElements = ul.getElementsByTag("li");
        for (Element li : liElements) {
            String ahref = li.select("a").attr("href");
            li.select("a").attr("href", Const.END_POINT_JWZX + ahref);
        }
        article.attachmentHtml = ul.outerHtml();

        return article;
    }
}
