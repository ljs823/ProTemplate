package com.ljs.protemplate;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


/**
 * Created by ljs on 2017/10/27.
 * Desc: 应用实例
 */

public class ProApplication extends Application {

    private static ProApplication pro; //全局上下文

    @Override
    public void onCreate() {
        super.onCreate();
        pro = this;
    }

    /**
     * @return 全局上下文
     */
    public static ProApplication getAppContext() {
        return pro;
    }

    /**
     * @return 当前应用版本名称，若获取异常返回固定值
     */
    public String getVersionName() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getString(R.string.version_default);
        }
    }
}
