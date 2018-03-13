package com.ljs.protemplate.mvp;

/**
 * Created by ljs on 2018/3/13.
 * Desc:逻辑层基类接口
 */

public interface IPresenter<V extends IView> {

    /**
     * @param view 绑定视图
     */
    void attachView(V view);

    /**
     * 防止内存泄漏，解除presenter与视图之间的绑定
     */
    void detachView();

    /**
     * @return 获取视图对象
     */
    IView getIView();

}
