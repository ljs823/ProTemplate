package com.ljs.protemplate.http;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Created by ljs on 2018/3/13.
 * Desc:自定义对于不同网络请求返回结果的处理
 */

public class HttpException {

    //不同网络响应码
    public static final int HTTP_SUCCESS = 200;
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_FORBIDDEN = 403;
    private static final int HTTP_UNFOUND = 404;
    private static final int HTTP_TIMEOUT = 408;
    private static final int HTTP_SERVER_ERROR = 500;
    private static final int HTTP_BAD_GATEWAY = 502;
    private static final int HTTP_SERVICE_UNAVAILABLE = 503;
    private static final int HTTP_GATEWAY_TIMEOUT = 504;
    //待定的其他异常情况
    private static final int UNKONW = 801;
    /**
     * 解析对象为空
     */
    public static final int BEAN_EMPTY = 802;
    /**
     * 对象异常code码
     */
    public static final int BEAN_ERROR = 803;

    static Throwable throwException(Throwable e) {
        Throwable throwable;
        String msg = "";
        if (e instanceof ServerException) {
            ServerException server = (ServerException) e;
            msg = getMsg(server.state, server.code, server.getMessage());
        } else if (e instanceof JSONException) {
            msg = "解析错误";
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException || e
                instanceof ConnectTimeoutException) {
            msg = "连接失败";
        } else if (e instanceof retrofit2.adapter.rxjava2.HttpException) {
            retrofit2.adapter.rxjava2.HttpException http = (retrofit2.adapter.rxjava2.HttpException) e;
            msg = getMsg(http.code(), UNKONW, "其他服务端错误");
        } else {
            msg = "其他错误-请联系管理员";
        }
        throwable = new Throwable(msg);
        return throwable;
    }


    private static String getMsg(int state, int code, String msgs) {
        String msg = "";
        switch (state) {
            case HTTP_SUCCESS:    //因为列表加载更多时候若没有更多数据返回200，这里就不能以异常来处理
                msg = msgs;
                break;
            case HTTP_UNAUTHORIZED:
                msg = "登录过期";
                break;
            case HTTP_FORBIDDEN:
                msg = "权限拒绝";
                break;
            case HTTP_UNFOUND:
                msg = "页面不存在";
                break;
            case HTTP_TIMEOUT:
                msg = "请求超时";
                break;
            case HTTP_GATEWAY_TIMEOUT:
                msg = "网络异常";
                break;
            case HTTP_SERVER_ERROR: //500
                switch (code) {
                    case 1:
                        msg = "参数错误";
                        break;
                    case 2:
                        msg = "未找到数据";
                        break;
                    case 3:
                        msg = "页面不存在";
                        break;
                    case 4:
                        msg = "token校验不通过";
                        break;
                    case 5:
                        msg = "权限不足";
                        break;
                    case 6:
                    case 7:
                        msg = "账号或密码错误";
                        break;
                    default:
                        msg = msgs;
                        break;
                }
                break;
            case HTTP_BAD_GATEWAY:
                msg = "作为网关或者代理工作的服务器尝试执行请求时，从上游服务器接收到无效的响应";
                break;
            case HTTP_SERVICE_UNAVAILABLE:
                msg = "由于临时的服务器维护或者过载，服务器当前无法处理请求";
                break;
            default:
                msg = msgs;  //其它均视为网络错误
                break;
        }
        return msg;
    }

}
