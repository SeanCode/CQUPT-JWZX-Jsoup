package me.xyp.app.network.service;

import me.xyp.app.config.Const;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by cc on 16/4/23.
 */
public interface JwzxService {

    @GET
    Observable<ResponseBody> download(@Url String url);

    @GET(Const.INDEX)
    Observable<ResponseBody> index();

    @GET(Const.LOGIN)
    Observable<ResponseBody> login();

    @GET(Const.PUBLIC_STU_COURSE_SCHEDULE)
    Observable<ResponseBody> pubStuCourseSchedule(@Field("xh") String xh);

    @GET(Const.COURSE_SCHEDULE)
    Observable<ResponseBody> courseSchedule();

    @GET(Const.STU_EXAM_SCHEDULE)
    Observable<ResponseBody> examSchedule();

    @GET(Const.STU_EXAM_SCHEDULE_MAKE_UP)
    Observable<ResponseBody> makeUpSchedule();

    @GET(Const.STU_GRADE)
    Observable<ResponseBody> gradeList();

    @GET(Const.STU_GRADE_ALL)
    Observable<ResponseBody> gradleListAll();

    @GET(Const.STU_INFO)
    Observable<ResponseBody> studentInfo();

    @GET(Const.TRAINING_PLAN_LIST)
    Observable<ResponseBody> trainingPlanList();

}
