package me.xyp.app.network.func;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

import me.xyp.app.event.LoginEvent;
import okhttp3.ResponseBody;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/25.
 */
public class ResponseBodyParseFunc implements Func1<ResponseBody, String> {

    private boolean shouldReturnRawHtml;
    private boolean shouldCheckLogin;

    public ResponseBodyParseFunc() {
        this(false, true);
    }

    public ResponseBodyParseFunc(boolean shouldReturnRawHtml, boolean shouldCheckLogin) {
        this.shouldReturnRawHtml = shouldReturnRawHtml;
        this.shouldCheckLogin = shouldCheckLogin;
    }

    public ResponseBodyParseFunc(boolean shouldReturnRawHtml) {
        this(shouldReturnRawHtml, true);
    }

    @Override
    public String call(ResponseBody responseBody) {
        try {
            String html = new String(responseBody.bytes(), "gb2312");
            if (!shouldReturnRawHtml) {

                Element table = Jsoup.parse(html).select("table").get(2);
                html = table.outerHtml();

                if (shouldCheckLogin) {
                    String tableText = table.select("td").text();
                    if (shouldReturnRawHtml) {
                        Logger.xml(tableText);
                    }
                    if (tableText.startsWith("教务在线登录")) {
                        EventBus.getDefault().post(new LoginEvent(LoginEvent.TYPE.COOKIE_INVALID));
                        return null;
                    }
                }
            }

            return html;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
