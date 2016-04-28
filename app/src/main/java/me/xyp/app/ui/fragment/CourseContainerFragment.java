package me.xyp.app.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.R;
import me.xyp.app.model.Course;
import me.xyp.app.network.Repository;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;
import me.xyp.app.ui.adapter.TabPagerAdapter;
import me.xyp.app.util.SchoolCalendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseContainerFragment extends Fragment {

    @Bind(R.id.tab_course_viewpager)
    ViewPager pager;
    @Bind(R.id.tab_course_tabs)
    TabLayout tabLayout;
    @Bind(R.id.course_container_fab)
    FloatingActionButton fab;

    private int mNowWeek; // 当前周
    private TabPagerAdapter adapter;
    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentsList = new ArrayList<>();

    public CourseContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleList.addAll(Arrays.asList(getResources().getStringArray(R.array.titles_weeks)));// 得到每周的标题
        mNowWeek = new SchoolCalendar().getWeekOfTerm(); // 获取本周
        if (mNowWeek <= 23 && mNowWeek >= 1) { // 将本周的标题换成“本周”
            titleList.set(mNowWeek, "本周");
        }
        for (int i = 0; i < titleList.size(); i++) { // 创建每周的课表Fragment存在list中
            CourseFragment temp = new CourseFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(CourseFragment.BUNDLE_KEY, i); // 给Fragment附加周数值，让它们心里清楚自己是第几周
            temp.setArguments(bundle);
            fragmentsList.add(temp);
        }
        adapter = new TabPagerAdapter(getActivity().getSupportFragmentManager(), fragmentsList, titleList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_course_container, container, false);

        ButterKnife.bind(this, rootView);
        //pager.setOffscreenPageLimit(0);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        fab.setOnClickListener(v -> changeCurrentItem());
        // Inflate the layout for this fragment
        return rootView;
    }

    private void changeCurrentItem() {
        int position = pager.getCurrentItem();
        if (position != mNowWeek) {
            setCurrentItem(mNowWeek);
        } else if (position != 0) {
            setCurrentItem(0);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mNowWeek <= 18 && mNowWeek >= 1) {
            setCurrentItem(mNowWeek); // 将tab滑到当前周
        }
    }

    private void setCurrentItem(int position) {

        if (position > 18 && position < 1) {
            position = 0;
        }
        pager.setCurrentItem(position, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
