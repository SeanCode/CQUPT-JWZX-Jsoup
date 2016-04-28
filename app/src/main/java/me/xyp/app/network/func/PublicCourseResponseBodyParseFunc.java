package me.xyp.app.network.func;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

import me.xyp.app.network.exception.JsoupParaseException;
import okhttp3.ResponseBody;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/28.
 */
public class PublicCourseResponseBodyParseFunc implements Func1<ResponseBody, String> {
    @Override
    public String call(ResponseBody responseBody) {
        try {

            String html = new String(responseBody.bytes(), "gb2312");

            Element table = Jsoup.parse(html).select("table").get(0);

            html = table.outerHtml();

            return html;
        } catch (IOException e) {
            throw new JsoupParaseException("数据解析异常");
        }
    }
}
