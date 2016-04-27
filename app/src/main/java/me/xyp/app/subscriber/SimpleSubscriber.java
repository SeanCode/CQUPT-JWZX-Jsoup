package me.xyp.app.subscriber;

import android.content.Context;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import me.xyp.app.BuildConfig;
import me.xyp.app.component.task.progress.ProgressCancelListener;
import me.xyp.app.component.task.progress.ProgressDialogHandler;
import me.xyp.app.event.LoginEvent;
import me.xyp.app.network.exception.CatchLoginHtmlException;
import me.xyp.app.network.exception.JsoupParaseException;
import rx.Subscriber;

/**
 * Created by cc on 16/3/19.
 */
public class SimpleSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {
    private Context context;
    private SubscriberListener<T> listener;
    private ProgressDialogHandler mProgressDialogHandler;
    private boolean shouldToast = true;

    public SimpleSubscriber(Context context, SubscriberListener<T> listener) {
        this(context, false, listener);
    }

    public SimpleSubscriber(Context context, boolean shouldShowProgressDialog, SubscriberListener<T> listener) {
        this(context, true, shouldShowProgressDialog, false, listener);
    }

    public SimpleSubscriber(Context context, boolean shouldToast, boolean shouldShowProgressDialog, boolean isProgressDialogCancelable, SubscriberListener<T> listener) {
        this.context = context;
        this.listener = listener;
        this.shouldToast = shouldToast;
        if (shouldShowProgressDialog) {
            mProgressDialogHandler = new ProgressDialogHandler(context, this, isProgressDialogCancelable);
        }
    }

    @Override
    public void onStart() {
        showProgressDialog();
        if (listener != null) {
            listener.onStart();
        }
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
        if (listener != null) {
            listener.onCompleted();
        }
    }

    @Override
    public void onError(Throwable e) {
        notifyError(e, this.shouldToast);
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
        dismissProgressDialog();
        if (listener != null) {
            listener.onError(e);
        }
    }

    public void notifyError(Throwable e, boolean shouldToast) {
        String message = e.getMessage();
        if (e instanceof SocketTimeoutException) {
            message = "网络异常，请检查您的网络状态";
        } else if (e instanceof ConnectException) {
            message = "网络异常，请检查您的网络状态";
        } else if (e instanceof UnknownHostException) {
            message = "网络异常，无法连接教务在线";
        } else if (e instanceof CatchLoginHtmlException) {
            EventBus.getDefault().post(new LoginEvent(LoginEvent.TYPE.COOKIE_INVALID));
        }
        if (shouldToast) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNext(T t) {
        if (listener != null) {
            listener.onNext(t);
        }
    }

    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    protected Context getContext() {
        return this.context;
    }

    public void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

}
