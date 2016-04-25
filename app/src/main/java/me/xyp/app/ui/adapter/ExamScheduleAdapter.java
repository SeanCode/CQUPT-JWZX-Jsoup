package me.xyp.app.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.R;
import me.xyp.app.model.Exam;

/**
 * Created by cc on 16/4/25.
 */
public class ExamScheduleAdapter extends RecyclerView.Adapter<ExamScheduleAdapter.ViewHolder> {

    private List<Exam> list;

    public ExamScheduleAdapter(List<Exam> list) {
        this.list = list;
    }

    public void setList(List<Exam> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_schedule, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Exam exam = getItem(position);
        holder.itemExamNameTextView.setText(exam.name);
        if (exam.place.length() > 0) {
            holder.itemExamPlaceTextView.setText(exam.place);
        }
        holder.itemExamStatusTextView.setText(exam.right);
        String time = exam.week + " " + exam.period + exam.time;
        if (time.length() > 1) {
            holder.itemExamTimeTextView.setText(time);
        }
    }

    public Exam getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_exam_name_text_view)
        TextView itemExamNameTextView;
        @Bind(R.id.item_exam_place_text_view)
        TextView itemExamPlaceTextView;
        @Bind(R.id.item_exam_time_text_view)
        TextView itemExamTimeTextView;
        @Bind(R.id.item_exam_status_text_view)
        TextView itemExamStatusTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
