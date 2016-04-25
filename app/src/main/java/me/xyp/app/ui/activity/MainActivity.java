package me.xyp.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.R;
import me.xyp.app.config.Const;
import me.xyp.app.event.LoginEvent;
import me.xyp.app.model.Student;
import me.xyp.app.network.RequestManager;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;
import me.xyp.app.ui.fragment.CourseContainerFragment;
import me.xyp.app.ui.fragment.ExamContainerFragment;
import me.xyp.app.ui.fragment.GradeFragment;
import me.xyp.app.util.Util;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    ImageView avatarImageView;
    TextView stuNameTextView;
    TextView stuNumTextView;

    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        attemptGetStuInfo();
    }

    private void attemptGetStuInfo() {

        RequestManager.getInstance().getStudentInfo(new SimpleSubscriber<>(this, new SubscriberListener<Student>() {
            @Override
            public void onNext(Student s) {
                if (s != null) {
                    stuNameTextView.setText(s.name);
                    stuNumTextView.setText(s.stuNum);
//                    loadAvatar(s.stuNum);
                }
            }
        }));
    }

    private void loadAvatar(String stuNum) {
        picasso.load(Const.END_POINT_JWZX + Const.STU_PIC + stuNum.substring(3)).into(avatarImageView);
    }

    private void testJsoup() {
        RequestManager.getInstance().index(new SimpleSubscriber<>(this, new SubscriberListener<String>() {

            @Override
            public void onNext(String s) {
                Util.toast(MainActivity.this, s);
            }

        }));
    }

    private void initView() {
        updateTitle("学生课表");
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getHeaderView(0).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StudentInfoActivity.class)));

        avatarImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_avatar_image_view);
        stuNameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_stu_name_text_view);
        stuNumTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_stu_num_text_view);

        MenuItem itemDefault = navigationView.getMenu().getItem(0);
        itemDefault.setChecked(true);

        onNavigationItemSelected(itemDefault);

        picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(RequestManager.getInstance().getOkHttpClient())).build();

    }

    private void updateTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_camera:
                fragment = new CourseContainerFragment();
                updateTitle("学生课表");
                break;
            case R.id.nav_slideshow:
                fragment = new ExamContainerFragment();
                updateTitle("考试安排");
                break;
            case R.id.nav_manage:
                fragment = new GradeFragment();
                updateTitle("考试成绩");
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.main_container, fragment).commit();
        }

        if (id == R.id.nav_send) {//培养方案
            startActivity(new Intent(this, TrainingPlanActivity.class));
        } else if (id == R.id.nav_share) {//退出
            this.finish();
            EventBus.getDefault().post(new LoginEvent(LoginEvent.TYPE.NO_COOKIE));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
