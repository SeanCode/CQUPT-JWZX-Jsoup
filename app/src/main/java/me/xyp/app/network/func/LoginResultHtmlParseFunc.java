package me.xyp.app.network.func;

import org.jsoup.Jsoup;

import me.xyp.app.model.Result;
import me.xyp.app.network.exception.LoginFailException;
import rx.functions.Func1;

/**
 * Created by cc on 16/4/25.
 */
public class LoginResultHtmlParseFunc implements Func1<String, Result> {
    @Override
    public Result call(String html) {
        /**
         * <SCRIPT LANGUAGE='JavaScript'>alert("xx同学,您好!登录成功!这是您第【160】次登录教务在线!感谢您一直以来的对教务在线关注!") ;location.href='index.php';</SCRIPT>
         *
         * <SCRIPT LANGUAGE='JavaScript'>alert("验证码错误或者已经过了有效时间!请您重新登录!");history.go(-1);</SCRIPT>
         *
         * <SCRIPT LANGUAGE='JavaScript'>alert("你输入的密码有误,请核实!");history.go(-1);</SCRIPT>
         *
         * return a string 你输入的密码有误,请核实! / xx同学,您好.... / 验证码错误或者已经过...
         */
        String s = Jsoup.parse(html).select("SCRIPT").first().html();
        s = s.substring(7, s.indexOf(")") - 1);
        if (s.startsWith("你")) {
            throw new LoginFailException(LoginFailException.CODE_PASSWORD_WRONG, s);
        } else if (s.startsWith("验")) {
            throw new LoginFailException(LoginFailException.CODE_VCODE_INVALID, s);
        }
        return new Result(0, s);
    }
}
