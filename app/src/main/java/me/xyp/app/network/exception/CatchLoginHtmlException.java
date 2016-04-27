package me.xyp.app.network.exception;

/**
 * Created by cc on 16/4/25.
 */
public class CatchLoginHtmlException extends RuntimeException {

    public CatchLoginHtmlException() {
    }

    public CatchLoginHtmlException(String detailMessage) {
        super(detailMessage);
    }

    public CatchLoginHtmlException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CatchLoginHtmlException(Throwable throwable) {
        super(throwable);
    }
}
