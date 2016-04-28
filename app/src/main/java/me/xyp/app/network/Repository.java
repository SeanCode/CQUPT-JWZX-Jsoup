package me.xyp.app.network;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;

import org.jsoup.Jsoup;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rx_cache.DynamicKey;
import io.rx_cache.EvictDynamicKey;
import io.rx_cache.internal.RxCache;
import me.xyp.app.APP;
import me.xyp.app.BuildConfig;
import me.xyp.app.component.persistentcookiejar.SharedPrefsCookiePersistor;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cc on 16/4/23.
 */
public enum Repository {

    INSTANCE;

    private static final int DEFAULT_TIMEOUT = 30;

    private JwzxService jwzxService;

    private CacheProviders cacheProviders;

    private PersistentCookieJar cookieJar;

    private OkHttpClient okHttpClient;

    public static Repository getInstance() {
        return INSTANCE;
    }

    Repository() {
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

    public Subscription index(Subscriber<String> subscriber) {
        Observable<String> observable = jwzxService.index()
                .map(new ResponseBodyParseFunc(true, false))
                .map(html -> Jsoup.parse(html).title());
        return emitObservable(observable, subscriber);
    }

    public Subscription login(Subscriber<String> subscriber) {
        Observable<String> observable = jwzxService.login()
                .map(new ResponseBodyParseFunc(false, false))
                .map(html -> Jsoup.parse(html).title());
        return emitObservable(observable, subscriber);
    }

    public Subscription loginWithForm(String stuNum, String psw, String code, Subscriber<Result> subscriber) {
        Observable<Result> observable = jwzxService.loginWithForm(stuNum, psw, code)
                .map(new ResponseBodyParseFunc(true, false))
                .map(new LoginResultHtmlParseFunc());

        return emitObservable(observable, subscriber);
    }

    public Subscription getPublicStudentCourseSchedule(String stuNum, Subscriber<List<Course>> subscriber, final boolean update) {
        Observable<List<Course>> observable = cacheProviders.getCoursesByStuNum(
                getPublicStudentCourseSchedule(stuNum), new DynamicKey(stuNum), new EvictDynamicKey(update))
                .map(new CacheMapFunc<>());

        return emitObservable(observable, subscriber);
    }

    public Observable<List<Course>> getPublicStudentCourseSchedule(String stuNum) {
        return jwzxService.pubStuCourseSchedule(stuNum)
                .map(new PublicCourseResponseBodyParseFunc())
                .map(new CourseTableHtmlParseFunc(true));
    }

    public Subscription getUserCourseSchedule(String stuNum, Subscriber<List<Course>> subscriber, boolean update) {
        Observable<List<Course>> observable = cacheProviders.getCoursesByStuNum(
                jwzxService.courseSchedule()
                        .map(new ResponseBodyParseFunc())
                        .map(new CourseTableHtmlParseFunc())
                , new DynamicKey(stuNum), new EvictDynamicKey(update))
                .map(new CacheMapFunc<>());

        return emitObservable(observable, subscriber);
    }

    public Subscription getUserExamSchedule(String stuNum, boolean isMakeUp, Subscriber<List<Exam>> subscriber, boolean update) {

        Observable<List<Exam>> observable = cacheProviders.getExamScheduleByStuNum(
                (isMakeUp ? jwzxService.makeUpSchedule() : jwzxService.examSchedule())
                        .map(new ResponseBodyParseFunc())
                        .map(new ExamScheduleHtmlParseFunc())
                , new DynamicKey(stuNum + (isMakeUp ? "-0" : "-1")), new EvictDynamicKey(update))
                .map(new CacheMapFunc<>());

        return emitObservable(observable, subscriber);
    }

    public Subscription getGradeList(String stuNum, boolean shouldReturnAll, Subscriber<Grade> subscriber, boolean update) {

        Observable<Grade> observable = cacheProviders.getGradeByStuNum(
                (shouldReturnAll ? jwzxService.gradleListAll() : jwzxService.gradeList())
                        .map(new ResponseBodyParseFunc())
                        .map(new GradeListHtmlParseFunc())
                , new DynamicKey(stuNum + (shouldReturnAll ? "-0" : "-1")), new EvictDynamicKey(update))
                .map(new CacheMapFunc<>());

        return emitObservable(observable, subscriber);
    }

    public Subscription getStudentInfo(String stuNum, Subscriber<Student> subscriber, boolean update) {
        Observable<Student> observable = cacheProviders.getStudentInfoByStuNum(
                jwzxService.studentInfo()
                        .map(new ResponseBodyParseFunc())
                        .map(new StudentInfoHtmlParseFunc())
                , new DynamicKey(stuNum), new EvictDynamicKey(update))
                .map(new CacheMapFunc<>());

        return emitObservable(observable, subscriber);
    }

    public Subscription getArticleList(String dirId, Subscriber<List<ArticleBasic>> subscriber, boolean update) {
        Observable<List<ArticleBasic>> observable = cacheProviders.getArticleBasicListByDirId(
                jwzxService.articleList(dirId)
                        .map(new ResponseBodyParseFunc(true, false))
                        .map(new ArticleListHtmlParseFunc())
                , new DynamicKey(dirId), new EvictDynamicKey(update))
                .map(new CacheMapFunc<>());

        return emitObservable(observable, subscriber);
    }

    public Subscription getArticleContent(String id, Subscriber<Article> subscriber, boolean update) {
        Observable<Article> observable = cacheProviders.getArticleById(
                jwzxService.articleContent(id)
                        .map(new ResponseBodyParseFunc())
                        .map(new ArticleContentHtmlParseFunc())
                , new DynamicKey(id), new EvictDynamicKey(update))
                .map(new CacheMapFunc<>());

        return emitObservable(observable, subscriber);
    }

    private <T> Subscription emitObservable(Observable<T> o, Subscriber<T> s) {
        return o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
