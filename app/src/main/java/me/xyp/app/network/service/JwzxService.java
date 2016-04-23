package me.xyp.app.network.service;

import me.xyp.app.config.Const;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by cc on 16/4/23.
 */
public interface JwzxService {

    @GET
    Observable<ResponseBody> download(@Url String url);

    @GET(Const.INDEX)
    Observable<ResponseBody> index();

}
