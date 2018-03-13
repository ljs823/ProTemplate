package com.ljs.protemplate.http.transformer;

import com.ljs.protemplate.http.HttpException;
import com.ljs.protemplate.http.HttpResult;
import com.ljs.protemplate.http.ServerException;
import com.ljs.protemplate.utils.Logger;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

/**
 * Created by ljs on 2018/3/13.
 * Desc:对于后台返回数据判断校验并返回需要的结果
 */

public class ErrorTransformer<T> implements ObservableTransformer<HttpResult<T>,T> {

    private static ErrorTransformer errorTransformer = null;

    @Override
    public ObservableSource<T> apply(Observable<HttpResult<T>> upstream) {
        return upstream.map(new Function<HttpResult<T>, T>() {
            @Override
            public T apply(HttpResult<T> httpResult) throws Exception {
                if (httpResult==null){
                    throw new ServerException(HttpException.BEAN_EMPTY,HttpException.BEAN_ERROR,"解析对象为空");
                }
                Logger.d("http transform::", httpResult.toString());
                if (httpResult.getState()!=HttpException.HTTP_SUCCESS){
                    throw new ServerException(httpResult.getState(),httpResult.getCode(),httpResult.getMsg());
                }
                return httpResult.getData();
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
            @Override
            public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                return Observable.error(throwable);
            }
        });
    }

    /**
     * @return 单例，双层校验，保证安全
     */
    static <T> ErrorTransformer getInstance() {
        if (errorTransformer == null) {
            synchronized (ErrorTransformer.class) {
                if (errorTransformer == null) {
                    errorTransformer = new ErrorTransformer();
                }
            }
        }
        return errorTransformer;
    }

}
