package com.ljs.protemplate.listeners;

import java.util.List;

/**
 * Created by ljs on 2018/3/13.
 * Desc:权限处理监听接口规范：同意给出提示，拒绝则加入集合
 */

public interface PermissionListener {

    /**
     * 同意权限处理逻辑
     */
    void onGranted();

    /**
     * @param denies 拒绝权限处理逻辑
     */
    void onDenied(List<String> denies);

}
