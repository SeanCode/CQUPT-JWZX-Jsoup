package me.xyp.app.event;

/**
 * Created by cc on 16/4/20.
 */
public class LoginEvent {

    public enum TYPE {
        NO_COOKIE,
        COOKIE_INVALID
    }

    public TYPE type;

    public LoginEvent(TYPE type) {
        this.type = type;
    }
}
