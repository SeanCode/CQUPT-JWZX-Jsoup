package me.xyp.app.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.R;
import me.xyp.app.model.Grade;
import me.xyp.app.network.RequestManager;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;
import me.xyp.app.ui.adapter.ExamGradeAdapter;
import me.xyp.app.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradeFragment extends Fragment {


    @Bind(R.id.exam_grade_weight)
    TextView gradeWeight;
    @Bind(R.id.exam_grade_recycler_view)
    RecyclerView recyclerView;

    ExamGradeAdapter adapter;

    public GradeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exam_grade, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ExamGradeAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));

        RequestManager.getInstance().getGradeList(true, new SimpleSubscriber<>(getActivity(), new SubscriberListener<Grade>() {
            @Override
            public void onNext(Grade grade) {
                gradeWeight.setText("课程平均成绩级点参考：" + grade.weight);
                adapter.setList(grade.childGradeList);
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
