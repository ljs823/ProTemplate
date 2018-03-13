package com.ljs.protemplate.http;

import com.ljs.protemplate.listeners.HttpListener;
import com.ljs.protemplate.utils.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by ljs on 2018/3/13.
 * Desc:自定义网络请求观察者
 */

public class NetSubscriber<T> implements Observer<T> {

    private HttpListener listener;

    public NetSubscriber(HttpListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSubscribe(Disposable d) {
        Logger.d("NetSubscriber", "onSubscribe");
    }

    @Override
    public void onNext(T t) {
        //这里返回的类型是LinkedTreeMap类型，在监听返回数据接口中按Map类型进行数据读取
        Logger.d("NetSubscriber", "onNext" + t.toString());
        if (!listener.isCancel()) {
            listener.success(t);
        }
    }

    @Override
    public void onError(Throwable t) {
        Logger.d("NetSubscriber", "onError——" + t.getMessage());
        if (!listener.isCancel()) {
            listener.fail(HttpException.throwException(t));
        }
    }

    @Override
    public void onComplete() {
    }
}
