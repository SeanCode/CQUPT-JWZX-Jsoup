package me.xyp.app.ui.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import me.xyp.app.R;
import me.xyp.app.model.Course;

public class CourseDialog {

    public static void show(Context context, ScheduleView.CourseList list) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_schedule, null);
        RedPagerView view = (RedPagerView) layout.findViewById(R.id.course_viewpager);
        CoursePagerAdapter adapter = new CoursePagerAdapter(context, inflater, list);
        view.setAdapter(adapter);
        new MaterialDialog.Builder(context).title("课程详细信息")
                .customView(layout, true)
                .positiveText("确定")
                .cancelable(true)
                .show();
    }

    static class CoursePagerAdapter extends PagerAdapter {
        private ArrayList<Course> course;
        private LayoutInflater mInflater;
        private Context context;

        public CoursePagerAdapter(Context context, LayoutInflater mInflater, ScheduleView.CourseList course) {
            this.context = context;
            this.mInflater = mInflater;
            this.course = course.list;
        }

        @Override
        public int getCount() {
            return course.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View courseview = container.getChildAt(position);
            if (courseview == null) {
                Course course = this.course.get(position);
                int begin_lesson = 2 * course.hash_lesson + 1;
                courseview = mInflater.inflate(R.layout.item_dialog_schedule, container, false);
                TextView name = (TextView) courseview.findViewById(R.id.dialog_course_name);
                TextView teacher = (TextView) courseview.findViewById(R.id.dialog_course_teacher);
                TextView classroom = (TextView) courseview.findViewById(R.id.dialog_course_classroom);
                TextView weeks = (TextView) courseview.findViewById(R.id.dialog_course_weeks);
                TextView time = (TextView) courseview.findViewById(R.id.dialog_course_time);
                TextView timenum = (TextView) courseview.findViewById(R.id.dialog_course_time_num);
                TextView type = (TextView) courseview.findViewById(R.id.dialog_course_type);
                name.setText(course.name);
                teacher.setText(course.teacher);
                classroom.setText(course.classroom);
                time.setText(new StringBuilder().append(context.getResources().getStringArray(R.array.schedule_weekday)[course.hash_day]).append(" ~ ").append(begin_lesson).append("-").append(begin_lesson + course.period - 1).append("节").toString());
                String timeStr = context.getResources().getStringArray(R.array.schedule_time)[course.hash_lesson];
                if (course.period == 2) {
                    timenum.setText(timeStr);
                } else {
                    timenum.setText(new StringBuilder().append(timeStr.substring(0, timeStr.indexOf("~") + 1)).append("下课").toString());
                }
                type.setText(course.type);
                weeks.setText(course.weekRaw);

                container.addView(courseview);
            }
            return courseview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }

}
