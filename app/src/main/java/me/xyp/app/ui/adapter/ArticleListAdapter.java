package me.xyp.app.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.xyp.app.R;
import me.xyp.app.model.ArticleBasic;

/**
 * Created by cc on 16/4/26.
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {

    private List<ArticleBasic> list;

    public void setList(List<ArticleBasic> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_basic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArticleBasic articleBasic = getItem(position);
        holder.readNumTextView.setText("阅读数：" + articleBasic.readNum);
        holder.timeTextView.setText(articleBasic.time);
        holder.titleTextView.setText(articleBasic.title);
    }

    public ArticleBasic getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_article_title_text_view)
        TextView titleTextView;
        @Bind(R.id.item_article_time_text_view)
        TextView timeTextView;
        @Bind(R.id.item_article_read_num_text_view)
        TextView readNumTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
