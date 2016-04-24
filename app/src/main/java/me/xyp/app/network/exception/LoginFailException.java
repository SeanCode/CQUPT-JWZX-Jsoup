package me.xyp.app.network.exception;

/**
 * Created by cc on 16/4/25.
 */
public class LoginFailException extends RuntimeException {
    public int code;

    public static final int CODE_VCODE_INVALID = 1;
    public static final int CODE_PASSWORD_WRONG = 2;


    public LoginFailException(int code, String message) {
        super(message);
        this.code = code;
    }
}
