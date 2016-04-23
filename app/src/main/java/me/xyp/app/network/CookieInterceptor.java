package me.xyp.app.network;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import me.xyp.app.config.Const;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import me.xyp.app.event.LoginEvent;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Platform;
import okhttp3.internal.http.HttpEngine;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by cc on 16/4/23.
 */
public class CookieInterceptor implements Interceptor {

    private String[] PATH_COULD_REQUEST_WITHOUT_COOKIE = new String[]{
            Const.URL_LOGIN,
    };

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        String url = request.url().toString();

        Log.d("Interceptor", "request headers: " + request.headers().toString());

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            Log.d("Interceptor->request", name + ": " + headers.value(i));
        }

        Response response = chain.proceed(request);
        headers = response.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            Log.d("Interceptor->response", headers.name(i) + ": " + headers.value(i));
        }

        if (!Arrays.asList(PATH_COULD_REQUEST_WITHOUT_COOKIE).contains(url)) {
            if (headers.get("Set-Cookie") != null) {
                Log.e("Interceptor", "Request without cookie and get new cookie, go to login");
                EventBus.getDefault().post(new LoginEvent());
            }
        }

        return response;
    }
}
