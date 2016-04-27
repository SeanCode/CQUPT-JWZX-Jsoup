package me.xyp.app.network.exception;

/**
 * Created by cc on 16/4/23.
 */
public class JsoupParaseException extends RuntimeException {
    public JsoupParaseException() {
    }

    public JsoupParaseException(String detailMessage) {
        super(detailMessage);
    }

    public JsoupParaseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public JsoupParaseException(Throwable throwable) {
        super(throwable);
    }
}
