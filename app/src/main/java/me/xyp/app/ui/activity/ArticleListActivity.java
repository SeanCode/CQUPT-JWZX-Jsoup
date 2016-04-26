package me.xyp.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.R;
import me.xyp.app.component.recyclerView.DividerItemDecoration;
import me.xyp.app.component.recyclerView.RecyclerItemClickListener;
import me.xyp.app.model.ArticleBasic;
import me.xyp.app.network.RequestManager;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;
import me.xyp.app.ui.adapter.ArticleListAdapter;

public class ArticleListActivity extends BaseActivity {

    public static String BUNDLE_KEY_DIR_ID = "dir_id";

    @Bind(R.id.article_list_recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.article_list_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private String dirId;

    private ArticleListAdapter adapter;

    public static void startActivityWithDirId(Context context, String dirId) {
        Intent intent = new Intent(context, ArticleListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_DIR_ID, dirId);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        ButterKnife.bind(this);
        initDataWithIntent();
        initView();
    }

    private void getArticleList() {

        new Handler(Looper.getMainLooper()).postDelayed(() -> RequestManager.getInstance().getArticleList(dirId, new SimpleSubscriber<>(ArticleListActivity.this, new SubscriberListener<List<ArticleBasic>>() {
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

            @Override
            public void onNext(List<ArticleBasic> articleBasics) {
                adapter.setList(articleBasics);
            }
        })), 500);
    }

    private void initDataWithIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dirId = bundle.getString(BUNDLE_KEY_DIR_ID, null);
        }
    }

    private void initView() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        adapter = new ArticleListAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, (view, position) -> showArticleContent(adapter.getItem(position))));

        swipeRefreshLayout.setOnRefreshListener(this::getArticleList);
        swipeRefreshLayout.setEnabled(true);

        getArticleList();
    }

    private void showArticleContent(ArticleBasic articleBasic) {
        ArticleContentActivity.startActivityWithId(ArticleListActivity.this, articleBasic.id, articleBasic.title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
