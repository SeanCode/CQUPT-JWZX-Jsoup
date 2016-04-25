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
public class ExamContainerFragment extends Fragment {


    @Bind(R.id.tab_exam_tabs)
    TabLayout tabLayout;
    @Bind(R.id.tab_exam_viewpager)
    ViewPager pager;

    private TabPagerAdapter adapter;
    private List<String> titleList;
    private List<android.support.v4.app.Fragment> fragmentsList = new ArrayList<>();

    public ExamContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleList = Arrays.asList("期末考试安排", "补考安排");
        for (int i = 0; i < titleList.size(); i++) { // 创建每周的课表Fragment存在list中
            ExamScheduleFragment temp = new ExamScheduleFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ExamScheduleFragment.BUNDLE_KEY, i); // 给Fragment附加周数值，让它们心里清楚自己是第几周
            temp.setArguments(bundle);
            fragmentsList.add(temp);
        }
        adapter = new TabPagerAdapter(getActivity().getSupportFragmentManager(), fragmentsList, titleList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exam_container, container, false);

        ButterKnife.bind(this, rootView);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
