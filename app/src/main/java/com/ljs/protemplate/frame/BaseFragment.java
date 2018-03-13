package com.ljs.protemplate.frame;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljs.protemplate.mvp.IView;
import com.ljs.protemplate.utils.Logger;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Created by ljs on 2018/3/13.
 * Desc: fragment基类--懒加载，防止页面销毁重复加载数据导致资源浪费
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements
        IView, View.OnClickListener {

    protected Activity act;
    private boolean isFirst = true;   //当前碎片页面是否是首次作为可视页面出现
    protected P mPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        act = getActivity();
        View root = inflater.inflate(getLayoutResId(), null);
        ButterKnife.bind(this, root);
        Logger.d("basefragment", this.getClass() + "::onCreateView");
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        whenShow();
        Logger.d("basefragment", this.getClass() + "::onActivityCreated");
    }

    /**
     * 该方法执行先于onCreateView、onActivityCreated执行。
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;    //当第一次显示后将值改为false，防止二次显示时候重复执行
            getData();
        }
        Logger.d("basefragment", this.getClass() + "::" + isVisibleToUser + "::" + getUserVisibleHint());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                onInnerClick(v);
                break;
        }
    }

    /**
     * 页面可见时候进行数据操作处理
     */
    protected void getData() {
    }

    /**
     * 碎片集中默认显示页面数据操作
     */
    protected void whenShow() {
    }

    /**
     * 子类所有的控件点击回调,必须在这里处理
     *
     * @param v 处理除了返回按钮的点击回调
     */
    public void onInnerClick(View v) {
    }

    /**
     * @return 返回当前Fragment的布局id
     */
    public abstract int getLayoutResId();

    /**
     * 初始化控件
     */
    public void initView() {
    }

    /**
     * 初始化数据
     */
    public void initData() {
        mPresenter = loadPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    /**
     * 初始化监听
     */
    public void initListener() {
    }

    /**
     * @return 初始化逻辑处理类
     */
    protected abstract P loadPresenter();

    //***************************销毁逻辑
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault() != null) {
            EventBus.getDefault().unregister(this);
        }
        Logger.d("basefragment", this.getClass() + "::onDestroyView");
        ButterKnife.unbind(this);
    }

}
