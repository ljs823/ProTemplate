package com.ljs.protemplate.frame;

import com.ljs.protemplate.listeners.HttpListener;
import com.ljs.protemplate.mvp.IModel;
import com.ljs.protemplate.mvp.IPresenter;
import com.ljs.protemplate.mvp.IView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ljs on 2018/3/13.
 * Desc:逻辑层基类
 */

public abstract class BasePresenter<V extends IView> implements IPresenter {

    private WeakReference actReference;
    protected V view;
    private List<HttpListener> listeners; //存储当前逻辑层的所有网络请求

    @Override
    public void attachView(IView view) {
        actReference = new WeakReference(view);
        listeners = new ArrayList<>();
    }

    @Override
    public void detachView() {
        if (actReference != null) {
            actReference.clear();
            actReference = null;
        }
        if (listeners.size()>0){    //设置listener的isCancel为true，则网络请求道到结果也不再处理
            for (HttpListener listener:listeners ) {
                listener.setCancel(true);
            }
            listeners.clear();
        }
    }

    @Override
    public V getIView() {
        return (V) actReference.get();
    }

    public abstract Map<String, IModel> getIModelMap();

    /**
     * @param iModels
     * @return 添加多个model，若需要额外的网络请求
     */
    public abstract Map<String, IModel> loadModelMap(IModel... iModels);

    /**
     * @param listener 每创建一个HttpListener就添加到当前presenter中
     */
    public void addListener(HttpListener listener) {
        listeners.add(listener);
    }

}
