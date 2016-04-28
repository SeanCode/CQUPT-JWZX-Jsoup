package me.xyp.app.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jsoup.helper.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.APP;
import me.xyp.app.R;
import me.xyp.app.model.Grade;
import me.xyp.app.network.Repository;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;
import me.xyp.app.ui.adapter.ExamGradeAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamGradeFragment extends Fragment {


    @Bind(R.id.exam_grade_weight)
    TextView gradeWeight;
    @Bind(R.id.exam_grade_recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.exam_grade_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    ExamGradeAdapter adapter;

    public ExamGradeFragment() {
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

        initView();
        getExamGradeAndRenderView(false);
    }

    private void getExamGradeAndRenderView(boolean update) {
        String stuNum = APP.getStuNum();
        if (!StringUtil.isBlank(stuNum)) {
            Repository.getInstance().getGradeList(stuNum, true, new SimpleSubscriber<>(getActivity(), new SubscriberListener<Grade>() {
                @Override
                public void onNext(Grade grade) {
                    gradeWeight.setText("课程平均成绩级点参考：" + grade.weight);
                    adapter.setList(grade.childGradeList);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCompleted() {
                    super.onCompleted();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onStart() {
                    super.onStart();
                    swipeRefreshLayout.setRefreshing(true);
                }
            }), update);
        }
    }

    private void initView() {
        adapter = new ExamGradeAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));

        swipeRefreshLayout.setOnRefreshListener(() -> getExamGradeAndRenderView(true));
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
