package me.xyp.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.jsoup.helper.StringUtil;
import org.xml.sax.XMLReader;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xyp.app.R;
import me.xyp.app.model.Article;
import me.xyp.app.network.RequestManager;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;

public class ArticleContentActivity extends BaseActivity {

    public static String BUNDLE_KEY_ID = "id";
    public static String BUNDLE_KEY_TITLE = "title";

    String id;
    String title;
    @Bind(R.id.article_content_text_view)
    TextView contentTextView;
    @Bind(R.id.article_content_attachment_text_view)
    TextView attachmentTextView;

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
        ButterKnife.bind(this);
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
            if (title != null) {
                ab.setTitle(title);
            }
        }
        attachmentTextView.setMovementMethod(LinkMovementMethod.getInstance());

        getArticleContent();
    }

    private void getArticleContent() {
        if (id != null) {
            RequestManager.getInstance().getArticleContent(id, new SimpleSubscriber<>(this, new SubscriberListener<Article>() {

                @Override
                public void onNext(Article article) {
                    super.onNext(article);
                    contentTextView.setText(Html.fromHtml(article.contentHtml));
                    if (!StringUtil.isBlank(article.attachmentHtml)) {
                        Logger.d(article.attachmentHtml);
                        attachmentTextView.setText(Html.fromHtml(article.attachmentHtml, null, new Html.TagHandler() {
                            boolean first = true;
                            String parent = null;
                            int index = 1;

                            @Override
                            public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

                                if (tag.equals("ul")) parent = "ul";
                                else if (tag.equals("ol")) parent = "ol";
                                if (tag.equals("li")) {
                                    if (parent.equals("ul")) {
                                        if (first) {
                                            output.append("\n\t•");
                                            first = false;
                                        } else {
                                            first = true;
                                        }
                                    } else {
                                        if (first) {
                                            output.append("\n\t").append(String.valueOf(index)).append(". ");
                                            first = false;
                                            index++;
                                        } else {
                                            first = true;
                                        }
                                    }
                                }
                            }
                        }));
                    }
                }
            }));
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
