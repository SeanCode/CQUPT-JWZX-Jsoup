package me.xyp.app.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.R;
import me.xyp.app.model.Exam;
import me.xyp.app.network.RequestManager;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;
import me.xyp.app.ui.adapter.ExamScheduleAdapter;
import me.xyp.app.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamScheduleFragment extends Fragment {

    public static final String BUNDLE_KEY = "EXAM_TYPE";

    private int examType = 0;

    @Bind(R.id.exam_schedule_recycler_view)
    RecyclerView recyclerView;

    ExamScheduleAdapter adapter;

    public ExamScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            examType = bundle.getInt(BUNDLE_KEY, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exam_schedule, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getExamScheduleAndRenderView();
    }

    private void initView() {
        adapter = new ExamScheduleAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void getExamScheduleAndRenderView() {
        RequestManager.getInstance().getUserExamSchedule(examType == 1, new SimpleSubscriber<>(getActivity(), new SubscriberListener<List<Exam>>() {
            @Override
            public void onNext(List<Exam> exams) {
                if (exams.size() == 0) {
                    Util.toast(getActivity(), examType == 1 ? "没有补考的孩子~~~" : "暂无考试安排");
                } else {
                    adapter.setList(exams);
                }

            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
