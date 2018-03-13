package com.ljs.protemplate.http.transformer;

import com.ljs.protemplate.http.HttpResult;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ljs on 2018/3/13.
 * Desc:类型转换器
 */

public class BaseTransformer<T> implements ObservableTransformer<HttpResult, T> {

    @Override
    public ObservableSource<T> apply(Observable<HttpResult> upstream) {
        //与new Thread的区别是io()内部实现了一个可以重用的无数量上限的线程池
        return upstream.subscribeOn(Schedulers.io())     //指定被观察者所在的线程
                .observeOn(AndroidSchedulers.mainThread())  //指定的操作将在主线程进行（观察者所运行的线程）
                .compose(ErrorTransformer.<T>getInstance());    //操作转换的是整个流
    }

}
