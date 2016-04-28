package me.xyp.app.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.APP;
import me.xyp.app.R;
import me.xyp.app.model.Course;
import me.xyp.app.network.Repository;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;
import me.xyp.app.ui.widget.ScheduleView;
import me.xyp.app.util.SchoolCalendar;
import me.xyp.app.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends Fragment {

    public static final String BUNDLE_KEY = "WEEK_NUM";


    private int[] todayWeekIds = {R.id.view_course_today_7, R.id.view_course_today_1, R.id.view_course_today_2,
            R.id.view_course_today_3, R.id.view_course_today_4, R.id.view_course_today_5, R.id.view_course_today_6};
    private int week = 0;
    public static final String TAG = "BaseFragment";

    @Bind(R.id.course_swipe_refresh_layout)
    SwipeRefreshLayout courseSwipeRefreshLayout;
    @Bind(R.id.course_progress)
    ProgressWheel courseProgress;
    @Bind(R.id.course_weeks)
    LinearLayout courseWeeks;
    @Bind(R.id.course_weekday)
    LinearLayout courseWeekday;
    @Bind(R.id.course_time)
    LinearLayout courseTime;
    @Bind(R.id.course_schedule_content)
    ScheduleView courseScheduleContent;
    @Bind(R.id.course_schedule_holder)
    LinearLayout courseScheduleHolder;
    @Bind(R.id.course_month)
    TextView courseMonth;

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        week = getArguments().getInt(BUNDLE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* 这一大串都是用来显示课表上方的日期之类的玩意 */
        String[] date = getResources().getStringArray(R.array.course_weekdays); // 获取星期的表头，什么“一 二 三……”
        String month = new SchoolCalendar(week, 1).getMonth() + "月";
        /* 如果超大屏幕课表太短了，给它填满屏幕 */
        int screeHeight = Util.getScreenHeight(getContext());
        if (Util.px2Dp(getContext(), screeHeight) > 700) {
            courseTime.setLayoutParams(new LinearLayout.LayoutParams((int) Util.dp2Px(getContext(), 40), screeHeight));
            courseScheduleContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, screeHeight));
        }

        if (week != 0) courseMonth.setText(month); // 除了整学期，都在左上角加一个月份显示
        for (int i = 0; i < 7; i++) { // 显示星期的标题
            TextView tv = new TextView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            tv.setLayoutParams(params);
            SchoolCalendar schoolCalendar = new SchoolCalendar(week, i + 1);
            tv.setText(date[i]);
            tv.setGravity(Gravity.CENTER);
            courseWeeks.addView(tv);
            if (week != 0) { // 除了整学期，在星期的标题上方显示日期
                TextView textView = new TextView(getActivity());
                textView.setLayoutParams(params);
                String day = schoolCalendar.getDay() + "";
                textView.setText(day);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(14);
                textView.setTextColor(getResources().getColor(R.color.data_light_black));
                courseWeekday.addView(textView);
            }
        }
        for (int i = 0; i < 12; i++) { // 课表左边的课程号
            TextView tv = new TextView(getActivity());
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            String courseNumber = i + 1 + "";
            tv.setText(courseNumber);
            tv.setGravity(Gravity.CENTER);
            courseTime.addView(tv);
        }

        if (week == new SchoolCalendar().getWeekOfTerm()) showTodayWeek(); // 显示当日的选中条

        courseSwipeRefreshLayout.setOnRefreshListener(() -> {
            courseProgress.stopSpinning();
            loadCourses(true);
        });
        courseSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent_2), getResources().getColor(R.color.colorPrimary));
        loadCourses(false);
    }

    private void loadCourses(boolean update) {
        if (APP.getStuNum() != null) {
            Repository.getInstance().getPublicStudentCourseSchedule(APP.getStuNum(), new SimpleSubscriber<List<Course>>(getActivity(), new SubscriberListener<List<Course>>() {
                @Override
                public void onCompleted() {
                    super.onCompleted();
                    Util.setGone(courseProgress, true);
                    courseSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    Util.setGone(courseProgress, true);
                    courseSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onNext(List<Course> courseList) {
                    super.onNext(courseList);
                    List<Course> courses = new ArrayList<>();

                    for (int i = 0; i < courseList.size(); i++) {
                        Course c = courseList.get(i);
                        if (c.week.contains(week) || week == 0) {
                            courses.add(c);
                        }
                    }
                    if (courseScheduleContent != null) {
                        Util.setGone(courseProgress, true);
                        courseScheduleContent.addContentView(courses);
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                    Util.setGone(courseProgress, false);
                    courseSwipeRefreshLayout.setRefreshing(true);
                }
            }), update);
        }
    }

    /* 显示当日的那个选中条 */
    private void showTodayWeek() {
        View view = getView();
        if (view != null)
            getView().findViewById(todayWeekIds[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]).setVisibility(View.VISIBLE);
    }
}
