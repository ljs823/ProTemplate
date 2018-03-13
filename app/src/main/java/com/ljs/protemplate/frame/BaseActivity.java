package com.ljs.protemplate.frame;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ljs.protemplate.R;
import com.ljs.protemplate.listeners.PermissionListener;
import com.ljs.protemplate.mvp.IView;
import com.ljs.protemplate.utils.AppManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by ljs on 2018/3/13.
 * Desc:Activity基类
 */

public abstract class BaseActivity<P extends BasePresenter> extends FragmentActivity
        implements IView, View.OnClickListener {

    protected View view;
    protected P mPresenter;

    private PermissionListener permissionListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getRootView());
        // 默认软键盘不弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.bind(this);
        mPresenter = loadPresenter();
        initCommonData();
        initView();
        initListener();
        registerCommonListener();
        initData();
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
    }

    private void initCommonData() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    //******************************关于视图Activity初始操作***********************
    protected View getRootView() {
        view = View.inflate(this, getLayoutId(), null);
        return view;
    }

    /**
     * 根据需要进行视图控件初始化
     */
    protected void initView() {
    }

    /**
     * 根据需要进行部分监听初始化
     */
    protected void initListener() {
    }

    /**
     * @return 布局文件资源ID
     */
    protected abstract int getLayoutId();

    /**
     * 视图创建数据初始化
     */
    protected abstract void initData();

    /**
     * @return 初始化逻辑处理类
     */
    protected abstract P loadPresenter();

    //**********************关于点击相关处理***************************************

    /**
     * 注册相同监听
     */
    private void registerCommonListener() {
        //给返回按钮设置监听
        View back = findViewById(R.id.back);
        if (back != null) {//并不是所有的界面都有返回按钮
            back.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                onInnerClick(v);
                break;
        }
    }

    /**
     * @param v 处理除了返回按钮的点击回调,子类所有的控件点击回调,必须在这里处理
     */
    protected void onInnerClick(View v) {
    }

    //********************************权限处理相关************************************

    /**
     * @param permissions 要申请的权限组
     * @param listener    权限处理监听接口
     */
    public void requestPermission(String[] permissions, PermissionListener listener) {
        permissionListener = listener;
        //存储被拒绝打开的权限集合
        List<String> perList = new ArrayList<>();
        //遍历要申请的权限数组，进行判断是否给予权限，若没有，将其添加到集合中
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                perList.add(permission);
            }
        }
        //若申请的权限集合为空，则这些要申请的权限已全授予
        if (perList.isEmpty()) {
            permissionListener.onGranted();
        } else {    //若非空，则有未授予的权限，动态进行申请
            ActivityCompat.requestPermissions(this, perList.toArray(new String[perList.size()]),
                    Config.REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Config.REQUEST_PERMISSION:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {    //若为空则权限都已授予
                        permissionListener.onGranted();
                    } else {    //若非空则将拒绝的权限加入onDenied方法处理
                        permissionListener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //////////////////////////////////////软键盘显示隐藏逻辑///////////////////////////////////////
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {//************触摸屏幕根据不同逻辑决定是否显示软键盘
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * @param v
     * @param event
     * @return 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     */
    protected boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * @param token 获取InputMethodManager，隐藏软键盘
     */
    protected void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //////////////////////////////////////Activity销毁逻辑//////////////////////////////////////
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault() != null) {
            EventBus.getDefault().unregister(this);
        }
        //ButterKnife.unbind(this);//官方只对fragment做解绑--这里做解绑会造成null异常
        // 结束Activity&从堆栈中移除
        if (mPresenter != null) {
            mPresenter.detachView();    //解除逻辑层与视图层之间的绑定
        }
        AppManager.getAppManager().finishActivity(this);
    }

    ////////////////////////////////////下面的方法部分页面会用到////////////////////////////////////

    /**
     * 获取文本控件的文本内容
     */
    protected String text(TextView tv) {
        return tv.getText().toString().trim();
    }

}
