package me.xyp.app.network;

import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rx_cache.internal.RxCache;
import me.xyp.app.APP;
import me.xyp.app.BuildConfig;
import me.xyp.app.config.Const;
import me.xyp.app.event.LoginEvent;
import me.xyp.app.model.Course;
import me.xyp.app.model.Exam;
import me.xyp.app.model.Grade;
import me.xyp.app.model.Result;
import me.xyp.app.model.Student;
import me.xyp.app.model.TrainingPlan;
import me.xyp.app.network.exception.LoginFailException;
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
                .map(new ResponseBodyParseFunc())
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
                .map(new ResponseBodyParseFunc(false, false))
                .map(html -> {
                    /**
                     * <SCRIPT LANGUAGE='JavaScript'>alert("xx同学,您好!登录成功!这是您第【160】次登录教务在线!感谢您一直以来的对教务在线关注!") ;location.href='index.php';</SCRIPT>
                     *
                     * <SCRIPT LANGUAGE='JavaScript'>alert("验证码错误或者已经过了有效时间!请您重新登录!");history.go(-1);</SCRIPT>
                     *
                     * <SCRIPT LANGUAGE='JavaScript'>alert("你输入的密码有误,请核实!");history.go(-1);</SCRIPT>
                     *
                     * return a string 你输入的密码有误,请核实! / xx同学,您好.... / 验证码错误或者已经过...
                     */
                    String s = Jsoup.parse(html).select("SCRIPT").first().html();
                    s = s.substring(7, s.indexOf(")") - 1);
                    if (s.startsWith("你")) {
                        throw new LoginFailException(LoginFailException.CODE_PASSWORD_WRONG, s);
                    } else if (s.startsWith("验")) {
                        throw new LoginFailException(LoginFailException.CODE_VCODE_INVALID, s);
                    }
                    return new Result(0, s);
                });

        emitObservable(observable, subscriber);
    }

    public void getPublicStudentCourseSchedule(String stuNum, Subscriber<List<Course>> subscriber) {
        Observable<List<Course>> observable = jwzxService.pubStuCourseSchedule(stuNum)
                .map(new ResponseBodyParseFunc())
                .map(new CourseTableParseFunc());

        emitObservable(observable, subscriber);
    }

    public void getUserCourseSchedule(Subscriber<List<Course>> subscriber) {
        Observable<List<Course>> observable = jwzxService.courseSchedule()
                .map(new ResponseBodyParseFunc())
                .map(new CourseTableParseFunc());

        emitObservable(observable, subscriber);
    }

    public void getUserExamSchedule(boolean isMakeUp, Subscriber<List<Exam>> subscriber) {

        Observable<ResponseBody> responseBodyObservable = isMakeUp ? jwzxService.makeUpSchedule() : jwzxService.examSchedule();

        Observable<List<Exam>> observable = responseBodyObservable
                .map(new ResponseBodyParseFunc())
                .map(html -> {
                    Elements tables = Jsoup.parse(html).getElementsByTag("table");
                    Element tableParent = tables.get(2);
                    Elements tds = tableParent.getElementsByTag("td");
                    List<Exam> list = new ArrayList<>();
                    Elements trs = tds.select("tr");
                    trs.remove(0);
                    for (Element tr : trs) {
                        Elements examElement = tr.getElementsByTag("td");
                        if (examElement.size() == 8) {
                            Exam exam = new Exam(
                                    examElement.get(0).text().substring(1),
                                    examElement.get(1).text().substring(1),
                                    examElement.get(2).text().substring(1),
                                    examElement.get(3).text().substring(1),
                                    examElement.get(4).text().substring(1),
                                    examElement.get(5).text().substring(1),
                                    examElement.get(6).text().substring(1),
                                    examElement.get(7).text().substring(1));
                            list.add(exam);
                        }
                    }
                    return list;
                });

        emitObservable(observable, subscriber);
    }

    public void getGradeList(boolean shouldReturnAll, Subscriber<List<Grade>> subscriber) {
        Observable<ResponseBody> responseBodyObservable = shouldReturnAll ? jwzxService.gradleListAll() : jwzxService.gradeList();

        Observable<List<Grade>> observable = responseBodyObservable
                .map(new ResponseBodyParseFunc())
                .map(html -> null);//todo parse

        emitObservable(observable, subscriber);
    }

    public void getStudentInfo(Subscriber<Student> subscriber) {
        Observable<Student> observable = jwzxService.studentInfo()
                .map(new ResponseBodyParseFunc())
                .map(html -> {
                    if (html == null) {
                        return null;
                    }
                    Elements tables = Jsoup.parse(html).getElementsByTag("table");
                    Element tableParent = tables.get(2);
                    Elements tds = tableParent.getElementsByTag("td");
                    String basicInfo = tds.select("p").html();
                    String[] basic = basicInfo.split(" 】【 ");
                    List<String> list = new ArrayList<>();
                    list.addAll(Arrays.asList(basic));
                    Elements trs = tds.select("td");
                    trs.remove(0);

                    for (Element e : trs) {
                        list.add(e.text());
                    }

                    return Student.getStudentFromElements(list);
                });

        emitObservable(observable, subscriber);
    }

    public void getTrainingPlanList(Subscriber<List<TrainingPlan>> subscriber) {
        Observable<List<TrainingPlan>> observable = jwzxService.trainingPlanList()
                .map(new ResponseBodyParseFunc())
                .map((Func1<String, List<TrainingPlan>>) html -> null);//todo parse

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

    private static class ResponseBodyParseFunc implements Func1<ResponseBody, String> {

        private boolean shouldLogContent;
        private boolean shouldParseHtml;

        public ResponseBodyParseFunc() {
            this(false, true);
        }

        public ResponseBodyParseFunc(boolean shouldLogContent, boolean shouldParseHtml) {
            this.shouldLogContent = shouldLogContent;
            this.shouldParseHtml = shouldParseHtml;
        }

        public ResponseBodyParseFunc(boolean shouldLogContent) {
            this(shouldLogContent, true);
        }

        @Override
        public String call(ResponseBody responseBody) {
            try {
                String html = new String(responseBody.bytes(), "gb2312");
                if (shouldLogContent) {
                    Logger.xml(html);
                }
                if (shouldParseHtml) {
                    Element table = Jsoup.parse(html).select("table").get(2);
                    String tableText = table.select("td").text();
                    if (shouldLogContent) {
                        Logger.xml(tableText);
                    }
                    if (tableText.startsWith("教务在线登录")) {
                        EventBus.getDefault().post(new LoginEvent(LoginEvent.TYPE.COOKIE_INVALID));
                        return null;
                    }
                }

                return html;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    private static class CourseTableParseFunc implements Func1<String, List<Course>> {

        @Override
        public List<Course> call(String s) {
            //todo parse course table html
            return new ArrayList<>();
        }
    }
}
