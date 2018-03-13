package com.ljs.protemplate.http;

import com.alibaba.fastjson.parser.Feature;
import com.ljs.protemplate.ProApplication;
import com.ljs.protemplate.frame.Config;
import com.ljs.protemplate.frame.HttpService;
import com.ljs.protemplate.utils.Logger;
import com.ljs.protemplate.utils.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * Created by ljs on 2018/3/13.
 * Desc:
 */

public class Http {

    private static OkHttpClient client;    //网络框架实例
    private static volatile Retrofit retrofit;  //retrofit实例
    private static HttpService httpService; //集合了项目所有网络请求接口的实例
    private static CallAdapter.Factory callFactory;
    private static Converter.Factory convertFactory;

    /**
     * @return Retrofit底层使用反射方式，获取请求接口的实例
     */
    public static HttpService getHttpService() {
        if (httpService == null) {
            httpService = getRetrofit().create(HttpService.class);
        }
        return httpService;
    }

    /**
     * @return Retrofit实例——针对除了车牌相关请求
     */
    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (Http.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(Config.BASE_URL)
                            .client(getClient())
                            .addCallAdapterFactory(callFactory)
                            .addConverterFactory(convertFactory) //fastjson
                            .build();
                }
            }
        }
        return retrofit;
    }

    private static OkHttpClient getClient() {
        if (client == null) {
            synchronized (Http.class) {
                if (client == null) {
                    //添加一个log拦截器，打印所有的log
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            Logger.d("OKHTTP::", "=====" + message);
                        }
                    });
                    //可以设置请求过滤的水平，body、basic、headers
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    //设置请求缓存的大小、位置
                    File cacheFile = new File(ProApplication.getAppContext().getCacheDir(), "cache");
                    Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);  //50Mb大小的缓存

                    callFactory = RxJava2CallAdapterFactory.create();
                    convertFactory = FastJsonConverterFactory.create()
                            .setParserFeatures(new Feature[]{Feature.OrderedField});
                    client = new OkHttpClient.Builder()
                            .addInterceptor(addQueryParamterInterceptor())  //参数拦截器
                            .addInterceptor(addHeaderInterceptor()) //头信息拦截器
                            .addInterceptor(addCacheInterceptor())  //缓存拦截器
                            .addInterceptor(loggingInterceptor) //日志拦截器
                            .cache(cache)   //添加缓存
                            .connectTimeout(120L, TimeUnit.SECONDS)
                            .readTimeout(120L, TimeUnit.SECONDS)
                            .writeTimeout(120L, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return client;
    }

    /**
     * @return 设置公共参数的拦截器
     */
    private static Interceptor addQueryParamterInterceptor() {
        Interceptor queryParamterInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request request;
                HttpUrl httpUrl = originalRequest.url().newBuilder()
                        //在这里添加手机型号、系统等内容
                        //.addQueryParameter("phoneSystem", "")
                        //.addQueryParameter("phoneModel", "")
                        .build();
                request = originalRequest.newBuilder().url(httpUrl).build();
                return chain.proceed(request);
            }
        };
        return queryParamterInterceptor;
    }

    /**
     * @return 设置头信息的拦截器
     */
    private static Interceptor addHeaderInterceptor() {
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder()
                        //在这里添加既定的头信息
//                        .header("", "")
                        .method(originalRequest.method(), originalRequest.body());
                Request request = builder.build();
                return chain.proceed(request);
            }
        };
        return headerInterceptor;
    }

    /**
     * @return 设置不同网络状态下的缓存拦截器
     */
    private static Interceptor addCacheInterceptor() {
        Interceptor cacheInterceptro = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!NetworkUtil.isNetAvailable(ProApplication.getAppContext())) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                String cacheControl = response.header("Cache-Control");
                Logger.d("cache", cacheControl);
                if (NetworkUtil.isNetAvailable(ProApplication.getAppContext())) {
                    //有网络时，设置缓存超时时间0，即不读取缓存数据，只对get有用，post没有缓存
                    int maxAge = 0;
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            //清除头信息，因为服务器若不支持会返回一些干扰信息，不清楚下面无效
                            .removeHeader("Retrofit")
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 28;//无网时设置超时四周，只对get有效，post无缓存
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("nyn")
                            .build();
                }
                return response;
            }
        };
        return cacheInterceptro;
    }

}
