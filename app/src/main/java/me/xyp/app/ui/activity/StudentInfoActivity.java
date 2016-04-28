package me.xyp.app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.jsoup.helper.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.APP;
import me.xyp.app.R;
import me.xyp.app.config.Config;
import me.xyp.app.config.Const;
import me.xyp.app.model.Student;
import me.xyp.app.network.Repository;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;
import me.xyp.app.util.Util;

public class StudentInfoActivity extends BaseActivity {

    Picasso picasso;

    @Bind(R.id.student_info_name_text_view)
    TextView nameTextView;
    @Bind(R.id.student_info_num_text_view)
    TextView numTextView;
    @Bind(R.id.student_info_class_text_view)
    TextView classTextView;
    @Bind(R.id.student_info_gender_text_view)
    TextView genderTextView;
    @Bind(R.id.student_info_birthday_text_view)
    TextView birthdayTextView;
    @Bind(R.id.student_info_college_text_view)
    TextView collegeTextView;
    @Bind(R.id.student_info_major_text_view)
    TextView majorTextView;
    @Bind(R.id.student_info_major_period_text_view)
    TextView majorPeriodTextView;
    @Bind(R.id.student_info_grade_text_view)
    TextView gradeTextView;
    @Bind(R.id.student_info_major_kind_text_view)
    TextView majorKindTextView;
    @Bind(R.id.student_info_avatar_image_view)
    ImageView avatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        ButterKnife.bind(this);
        initView();
        getStudentInfo();
    }

    private void initView() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(Repository.getInstance().getOkHttpClient())).build();

    }

    private void getStudentInfo() {
        if (!StringUtil.isBlank(APP.getStuNum())) {

            Repository.getInstance().getStudentInfo(APP.getStuNum(), new SimpleSubscriber<>(this, new SubscriberListener<Student>() {
                @Override
                public void onNext(Student s) {
                    if (s != null) {

                        nameTextView.setText(s.name);
                        numTextView.setText(s.stuNum);
                        classTextView.setText(s.classNum);
                        genderTextView.setText(s.gender);
                        birthdayTextView.setText(s.birthday);
                        collegeTextView.setText(s.college);
                        majorTextView.setText(s.major);
                        majorPeriodTextView.setText(s.majorPeriod);
                        gradeTextView.setText(s.grade);
                        majorKindTextView.setText(s.majorKind);

                        loadAvatar(s.stuNum);
                    }
                }
            }), false);
        }
    }

    private void loadAvatar(String stuNum) {
        picasso.load(Const.END_POINT_JWZX + Const.STU_PIC + stuNum.substring(3)).into(avatarImageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
