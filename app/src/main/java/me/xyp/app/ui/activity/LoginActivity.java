package me.xyp.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.xyp.app.R;
import me.xyp.app.config.Const;
import me.xyp.app.model.Result;
import me.xyp.app.network.RequestManager;
import me.xyp.app.subscriber.SimpleSubscriber;
import me.xyp.app.subscriber.SubscriberListener;
import me.xyp.app.util.Util;

//FIXME fix code image when code is invalid because of timeout
public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_stu_num_edit_text)
    EditText stuNumEditText;

    @Bind(R.id.login_password_edit_text)
    EditText passwordEditText;

    @Bind(R.id.login_verification_code_edit_text)
    EditText codeEditText;

    @Bind(R.id.login_code_image)
    ImageView codeImage;

    @OnClick(R.id.login_sign_in_button)
    void clickToLogin() {
        attemptLogin();
    }

    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        init();

    }

    private void init() {

        RequestManager.getInstance().clearCookie();

        //use okhttp client in {@link RequestManager}
        picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(RequestManager.getInstance().getOkHttpClient())).build();

        //make sure there exist cookie
        RequestManager.getInstance().login(new SimpleSubscriber<>(this, new SubscriberListener<String>() {
            @Override
            public void onNext(String s) {

                //show code image
                loadCodeToImage();
                Util.toast(LoginActivity.this, "请登录");
            }
        }));

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        String stuNum = stuNumEditText.getText().toString();
        RequestManager.getInstance().loginWithForm(stuNum,
                passwordEditText.getText().toString(),
                codeEditText.getText().toString(),
                new SimpleSubscriber<>(this, true, false, new SubscriberListener<Result>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        loadCodeToImage();
                        Logger.d("load new code image");
                    }

                    @Override
                    public void onNext(Result result) {
//                        Util.set(LoginActivity.this, Config.SP_KEY_STU_NUM, stuNum);
                        LoginActivity.this.finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    }
                }));
    }

    private void loadCodeToImage() {
        picasso.load(Const.END_POINT_JWZX + Const.VCODE).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(codeImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}

