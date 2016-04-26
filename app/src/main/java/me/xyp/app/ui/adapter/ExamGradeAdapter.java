package me.xyp.app.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.R;
import me.xyp.app.model.Grade;

/**
 * Created by cc on 16/4/26.
 */
public class ExamGradeAdapter extends RecyclerView.Adapter<ExamGradeAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<ExamGradeAdapter.HeaderViewHolder> {

    private List<Grade.ChildGrade> list;

    public void setList(List<Grade.ChildGrade> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_grade, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Grade.ChildGrade childGrade = getItem(position);
        setItemBackgroundColor(holder.itemView, position);
        holder.nameTextView.setText(childGrade.name);
        holder.propertyTextView.setText(childGrade.property);
        holder.scoreTextView.setText(childGrade.score);
        holder.weightTextView.setText(childGrade.weight);
    }

    private void setItemBackgroundColor(View view, int position) {
        if (position % 2 != 0) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.darker_grey));
        } else {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.white));
        }
    }

    public Grade.ChildGrade getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getHeaderId(int position) {
        return Long.parseLong(getItem(position).semester);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_exam_grade, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        Grade.ChildGrade childGrade = getItem(position);
        holder.semesterTextView.setText(childGrade.semester + "学期");
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_grade_name_text_view)
        TextView nameTextView;
        @Bind(R.id.item_grade_property_text_view)
        TextView propertyTextView;
        @Bind(R.id.item_grade_score_text_view)
        TextView scoreTextView;
        @Bind(R.id.item_grade_weight_text_view)
        TextView weightTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_header_exam_grade_semester)
        TextView semesterTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
