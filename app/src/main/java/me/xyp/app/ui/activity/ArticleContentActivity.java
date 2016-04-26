package me.xyp.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import me.xyp.app.R;

public class ArticleContentActivity extends AppCompatActivity {

    public static String BUNDLE_KEY_ID = "id";
    public static String BUNDLE_KEY_TITLE = "title";

    String id;
    String title;

    public static void startActivityWithId(Context context, String id, String title) {
        Intent intent = new Intent(context, ArticleContentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_ID, id);
        bundle.putString(BUNDLE_KEY_TITLE, title);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);
        initDataWithIntent();
        initView();
    }

    private void initDataWithIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString(BUNDLE_KEY_ID, null);
            title = bundle.getString(BUNDLE_KEY_TITLE, null);
        }
    }

    private void initView() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(title);
        }
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
