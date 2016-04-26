package me.xyp.app.network;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.jsoup.Jsoup;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rx_cache.internal.RxCache;
import me.xyp.app.APP;
import me.xyp.app.BuildConfig;
import me.xyp.app.config.Const;
import me.xyp.app.model.Article;
import me.xyp.app.model.ArticleBasic;
import me.xyp.app.model.Course;
import me.xyp.app.model.Exam;
import me.xyp.app.model.Grade;
import me.xyp.app.model.Result;
import me.xyp.app.model.Student;
import me.xyp.app.network.func.*;
import me.xyp.app.network.service.JwzxService;
import me.xyp.app.network.setting.CacheProviders;
import me.xyp.app.network.setting.CookieCheckInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cc on 16/4/23.
 */
public enum RequestManager {

    INSTANCE;

    private static final int DEFAULT_TIMEOUT = 30;

    private JwzxService jwzxService;

    private CacheProviders cacheProviders;

    private PersistentCookieJar cookieJar;

    private OkHttpClient okHttpClient;

    public static RequestManager getInstance() {
        return INSTANCE;
    }

    RequestManager() {
        okHttpClient = configureOkHttp(new OkHttpClient.Builder());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.END_POINT_JWZX)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        cacheProviders = new RxCache.Builder()
                .persistence(APP.getContext().getFilesDir())
                .using(CacheProviders.class);

        jwzxService = retrofit.create(JwzxService.class);
    }

    public OkHttpClient configureOkHttp(OkHttpClient.Builder builder) {
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(APP.getContext()));
        builder.cookieJar(cookieJar);
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        //debug
//        if (BuildConfig.DEBUG) {
//            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.99.165", 8888)));
//        }
        CookieCheckInterceptor interceptor = new CookieCheckInterceptor();
        builder.addInterceptor(interceptor);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.addInterceptor(logging);
        }

        return builder.build();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public void clearCookie() {
        cookieJar.clear();
    }

    public void index(Subscriber<String> subscriber) {
        Observable<String> observable = jwzxService.index()
                .map(new ResponseBodyParseFunc(true, false))
                .map(html -> Jsoup.parse(html).title());
        emitObservable(observable, subscriber);
    }

    public void login(Subscriber<String> subscriber) {
        Observable<String> observable = jwzxService.login()
                .map(new ResponseBodyParseFunc(false, false))
                .map(html -> Jsoup.parse(html).title());
        emitObservable(observable, subscriber);
    }

    public void loginWithForm(String stuNum, String psw, String code, Subscriber<Result> subscriber) {
        Observable<Result> observable = jwzxService.loginWithForm(stuNum, psw, code)
                .map(new ResponseBodyParseFunc(true, false))
                .map(new LoginResultHtmlParseFunc());

        emitObservable(observable, subscriber);
    }

    public void getPublicStudentCourseSchedule(String stuNum, Subscriber<List<Course>> subscriber) {
        Observable<List<Course>> observable = jwzxService.pubStuCourseSchedule(stuNum)
                .map(new ResponseBodyParseFunc())
                .map(new CourseTableHtmlParseFunc());

        emitObservable(observable, subscriber);
    }

    public void getUserCourseSchedule(Subscriber<List<Course>> subscriber) {
        Observable<List<Course>> observable = jwzxService.courseSchedule()
                .map(new ResponseBodyParseFunc())
                .map(new CourseTableHtmlParseFunc());

        emitObservable(observable, subscriber);
    }

    public void getUserExamSchedule(boolean isMakeUp, Subscriber<List<Exam>> subscriber) {

        Observable<ResponseBody> responseBodyObservable = isMakeUp ? jwzxService.makeUpSchedule() : jwzxService.examSchedule();

        Observable<List<Exam>> observable = responseBodyObservable
                .map(new ResponseBodyParseFunc())
                .map(new ExamScheduleHtmlParseFunc());

        emitObservable(observable, subscriber);
    }

    public void getGradeList(boolean shouldReturnAll, Subscriber<Grade> subscriber) {
        Observable<ResponseBody> responseBodyObservable = shouldReturnAll ? jwzxService.gradleListAll() : jwzxService.gradeList();

        Observable<Grade> observable = responseBodyObservable
                .map(new ResponseBodyParseFunc())
                .map(new GradeListHtmlParseFunc());

        emitObservable(observable, subscriber);
    }

    public void getStudentInfo(Subscriber<Student> subscriber) {
        Observable<Student> observable = jwzxService.studentInfo()
                .map(new ResponseBodyParseFunc())
                .map(new StudentInfoHtmlParseFunc());

        emitObservable(observable, subscriber);
    }

    public void getArticleList(String dirId, Subscriber<List<ArticleBasic>> subscriber) {
        Observable<List<ArticleBasic>> observable = jwzxService.articleList(dirId)
                .map(new ResponseBodyParseFunc(true, false))
                .map(new ArticleListHtmlParseFunc());

        emitObservable(observable, subscriber);
    }

    public void getArticleContent(String id, Subscriber<Article> subscriber) {
        Observable<Article> observable = jwzxService.articleContent(id)
                .map(new ResponseBodyParseFunc())
                .map(new ArticleContentHtmlParseFunc());

        emitObservable(observable, subscriber);
    }

    public void download(String url, Subscriber<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = jwzxService.download(url);
        //todo parse
        emitObservable(observable, subscriber);
    }

    private <T> void emitObservable(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
