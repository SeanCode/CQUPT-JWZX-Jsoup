package me.xyp.app.network.setting;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Arrays;

import me.xyp.app.config.Const;
import me.xyp.app.event.LoginEvent;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cc on 16/4/23.
 */
public class CookieCheckInterceptor implements Interceptor {

    private String[] PATH_COULD_REQUEST_WITHOUT_COOKIE = new String[]{
            Const.URL_LOGIN,
    };

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        String url = request.url().toString();

//        Log.d("Interceptor", "request headers: " + request.headers().toString());
//
//        Headers headers = request.headers();
//        for (int i = 0, count = headers.size(); i < count; i++) {
//            String name = headers.name(i);
//            Log.d("Interceptor->request", "-> " + name + ": " + headers.value(i));
//        }
//
        Response response = chain.proceed(request);
        Headers headers = response.headers();
//        for (int i = 0, count = headers.size(); i < count; i++) {
//            Log.d("Interceptor<-response", "<- " + headers.name(i) + ": " + headers.value(i));
//        }

        if (!Arrays.asList(PATH_COULD_REQUEST_WITHOUT_COOKIE).contains(url)) {
            if (headers.get("Set-Cookie") != null) {
                Log.e("OKHttp$Interceptor", "Request without cookie and get new cookie, go to login");
                EventBus.getDefault().post(new LoginEvent(LoginEvent.TYPE.NO_COOKIE));
            }
        }

        return response;
    }
}
