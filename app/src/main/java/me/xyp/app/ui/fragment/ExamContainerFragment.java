package me.xyp.app.ui.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.xyp.app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamContainerFragment extends Fragment {


    public ExamContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exam_container, container, false);
    }

}