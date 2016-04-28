package me.xyp.app.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.xyp.app.R;
import me.xyp.app.model.Course;
import me.xyp.app.util.Util;

public class ScheduleView extends FrameLayout {

    private final int width = (int) ((Util.getScreenWidth(getContext()) - Util.dp2Px(getContext(), 56)) / 7);
    private int height = (int) Util.dp2Px(getContext(), 100);
    private CourseColorSelector colorSelector = new CourseColorSelector();
    public CourseList[][] course = new CourseList[7][7];
    private Context context;

    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        /* 如果超大屏幕课表太短了，给它填满屏幕 */
        int screeHeight = Util.getScreenHeight(context);
        if (Util.px2Dp(context, screeHeight) > 700) height = screeHeight / 6;
        initCourse();
        setWillNotDraw(false);
    }

    public ScheduleView(Context context) {
        super(context);
    }

    private void initCourse() {
        for (int i = 0; i < 7; i++) {
            course[i] = new CourseList[7];
        }
    }

    public void addContentView(List<Course> data) {
        removeAllViews();
        initCourse();
        if (data != null) {

            for (Course c : data) {
                //colorSelector.addCourse(c.course);
                colorSelector.addCourse(2 * c.hash_lesson + 1, c.hash_day);
                int x = c.hash_lesson;
                int y = c.hash_day;
                if (course[x][y] == null) {
                    course[x][y] = new CourseList();
                }
                course[x][y].list.add(c);
            }
        }
        loadingContent();
    }

    private void loadingContent() {
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                if (course[x][y] != null) {
                    createLessonText(course[x][y]);
                }
            }
        }
        addAnchorView();
    }

    private void addAnchorView() {
        View anchor = new View(getContext());
        LayoutParams flparams = new LayoutParams(1, 1);
        flparams.topMargin = 0;
        flparams.leftMargin = width * 7 - 1;
        anchor.setBackgroundColor(Color.WHITE);
        anchor.setLayoutParams(flparams);
        addView(anchor);
    }


    private void createLessonText(final CourseList courses) {
        final Course course = courses.list.get(0);

        TextView tv = new TextView(context);
        int mTop = height * course.hash_lesson;
        int mLeft = width * course.hash_day;
        int mWidth = width;
        int mHeight = (int) (height * (float) course.period / 2);

        LayoutParams flParams = new LayoutParams((int) (mWidth - Util.dp2Px(getContext(), 1f)), (int) (mHeight - Util.dp2Px(getContext(), 1f)));
        flParams.topMargin = (int) (mTop + Util.dp2Px(getContext(), 1f));
        flParams.leftMargin = (int) (mLeft + Util.dp2Px(getContext(), 1f));
        tv.setLayoutParams(flParams);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv.setGravity(Gravity.CENTER);
        tv.setText(String.format("%s@%s", course.name, course.classroom));

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(Util.dp2Px(getContext(), 1));
        //gd.setColor(colorSelector.getCourseColor(course.course));
        gd.setColor(colorSelector.getCourseColor(2 * course.hash_lesson + 1, course.hash_day));
        tv.setBackgroundDrawable(gd);

        tv.setOnClickListener(v -> CourseDialog.show(getContext(), courses));
        addView(tv);
        if (courses.list.size() > 1) {
            View drop = new View(getContext());
            drop.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_corner_right_bottom));

            LayoutParams droplayout = new LayoutParams(mWidth / 5, mWidth / 5);
            droplayout.topMargin = mTop + mHeight - mWidth / 5;
            droplayout.leftMargin = mLeft + mWidth * 4 / 5;
            drop.setLayoutParams(droplayout);
            addView(drop);
        }
    }

    public static class CourseList {
        public ArrayList<Course> list = new ArrayList<>();
    }

    public static class CourseColorSelector {
        private int[] colors = new int[]{
                Color.argb(255, 254, 145, 103),
                Color.argb(255, 120, 201, 252),
                Color.argb(255, 111, 219, 188),
                Color.argb(255, 191, 161, 233),
//                Color.argb(200, 255, 161, 16),
//                Color.argb(200, 56, 188, 242),
//                Color.argb(200, 149, 213, 27),
//                Color.argb(200, 247, 96, 96),
//                Color.rgb(128, 79, 242),
//                Color.rgb(240, 68, 189),
//                Color.rgb(229, 28, 35),
//                Color.rgb(156, 39, 176),
//                Color.rgb(3, 169, 244),
//                Color.rgb(255, 193, 7),
//                Color.rgb(255, 152, 0),
//                Color.rgb(96, 125, 139),
        };

        private HashMap<String, Integer> mCourseColorMap = new HashMap<>();

        @SuppressWarnings("unchecked")
        public void addCourse(String name) {
            Set set = mCourseColorMap.entrySet();
            for (Object aSet : set) {
                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) aSet;
                if (entry.getKey().equals(name)) {
                    return;
                }
            }
            mCourseColorMap.put(name, colors[mCourseColorMap.size() % colors.length]);
        }

        public void addCourse(int beginLesson, int hashDay) {
            if (hashDay >= 5) mCourseColorMap.put(beginLesson + "," + hashDay, colors[3]);
            else if (beginLesson < 4) mCourseColorMap.put(beginLesson + "," + hashDay, colors[0]);
            else if (beginLesson < 7) mCourseColorMap.put(beginLesson + "," + hashDay, colors[1]);
            else mCourseColorMap.put(beginLesson + "," + hashDay, colors[2]);
        }

        public int getCourseColor(int beginLesson, int hashDay) {
            return mCourseColorMap.get(beginLesson + "," + hashDay);
        }

        public int getCourseColor(String name) {
            return mCourseColorMap.get(name);
        }
    }
}
