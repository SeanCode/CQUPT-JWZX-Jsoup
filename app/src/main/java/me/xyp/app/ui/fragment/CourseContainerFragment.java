package me.xyp.app.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.R;
import me.xyp.app.ui.adapter.TabPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseContainerFragment extends Fragment {

    @Bind(R.id.tab_course_viewpager)
    ViewPager pager;
    @Bind(R.id.tab_course_tabs)
    TabLayout tabLayout;

    private TabPagerAdapter adapter;
    private List<String> titleList = new ArrayList<>();
    private List<android.support.v4.app.Fragment> fragmentsList = new ArrayList<>();

    public CourseContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleList.addAll(Arrays.asList(getResources().getStringArray(R.array.titles_weeks)));// 得到每周的标题
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
        // Inflate the layout for this fragment
        return rootView;
    }

}
