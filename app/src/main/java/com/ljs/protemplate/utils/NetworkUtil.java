package com.ljs.protemplate.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ljs on 2018/3/13.
 * Desc:对于网络环境的判断等逻辑处理
 */

public class NetworkUtil {

    /**
     * @return 检查当前上下文环境下的网络是否正常
     */
    public static boolean isNetAvailable(Context ctx) {
        ConnectivityManager manager = (ConnectivityManager) ctx.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            return false;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (null == info || !info.isAvailable()||!info.isConnected()) {
            return false;
        }
        return true;
    }

}
