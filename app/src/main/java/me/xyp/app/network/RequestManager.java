package me.xyp.app.network;

import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import io.rx_cache.internal.RxCache;
import me.xyp.app.APP;
import me.xyp.app.BuildConfig;
import me.xyp.app.config.Const;
import me.xyp.app.network.service.JwzxService;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by cc on 16/4/23.
 */
public enum RequestManager {

    INSTANCE;

    private static final int DEFAULT_TIMEOUT = 30;

    private JwzxService jwzxService;

    private CacheProviders cacheProviders;

    public static RequestManager getInstance() {
        return INSTANCE;
    }

    RequestManager() {
        OkHttpClient client = configureOkHttp(new OkHttpClient.Builder());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.END_POINT_JWZX)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        cacheProviders = new RxCache.Builder()
                .persistence(APP.getContext().getFilesDir())
                .using(CacheProviders.class);

        jwzxService = retrofit.create(JwzxService.class);
    }

    public OkHttpClient configureOkHttp(OkHttpClient.Builder builder) {
        CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(APP.getContext()));
        builder.cookieJar(cookieJar);
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        //debug
        if (BuildConfig.DEBUG) {
            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.99.165", 8888)));
        }
        CookieInterceptor interceptor = new CookieInterceptor();
        builder.addInterceptor(interceptor);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(logging);
        }

        return builder.build();
    }

    public void index(Subscriber<String> subscriber) {
        Observable<String> observable = jwzxService.index()
                .map(new ResponseBodyParseFunc())
                .map(s -> Jsoup.parse(s).title());
        emitObservable(observable, subscriber);
    }

    public void download(String url, Subscriber<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = jwzxService.download(url);

        emitObservable(observable, subscriber);
    }

    private <T> void emitObservable(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    private static class ResponseBodyParseFunc implements Func1<ResponseBody, String> {

        @Override
        public String call(ResponseBody responseBody) {
            try {
                return new String(responseBody.bytes(), "gb2312");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
