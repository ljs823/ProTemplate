package com.ljs.protemplate.frame;

import com.ljs.protemplate.entity.LoginBean;
import com.ljs.protemplate.http.HttpResult;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by ljs on 2018/3/13.
 * Desc: 网络请求接口集
 */

public interface HttpService {

    /**
     * 登录接口
     */
    @FormUrlEncoded
    @POST("auth")
    Observable<HttpResult<LoginBean>> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("")
    @Headers({
            "Authorization:Bearer ...",
            "Accept:application/json;version=v1.0"
    })
    Observable<HttpResult> postDatas(@FieldMap Map<String, String> params);

    @GET("")
    Observable<HttpResult> getDatas(@Header("Authorization") String author,
                                        @QueryMap Map<String, String> params);
}
