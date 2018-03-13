package com.ljs.protemplate.listeners;

import com.ljs.protemplate.frame.BasePresenter;

/**
 * Created by ljs on 2018/3/13.
 * Desc: 网络请求监听接口
 */

public abstract class HttpListener<T> {

    public HttpListener(BasePresenter presenter){
        if (presenter!=null){
            presenter.addListener(this);
        }
    }

    /**
     * true则取消当前网络请求结果的成败处理
     */
    private boolean isCancel=false;

    /**
     * @param t 请求成功
     */
    public abstract void success(T t);

    /**
     * @param t 请求失败
     */
    public abstract void fail(Throwable t);

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

}
