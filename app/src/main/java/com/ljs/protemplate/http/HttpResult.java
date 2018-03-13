package com.ljs.protemplate.http;

import static android.R.id.message;

/**
 * Created by ljs on 2018/3/13.
 * Desc:后台返回数据基类对象
 */

public class HttpResult<T> {

    private int state;
    private int code;
    private String msg;
    private T data;


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseHttpResult{" +
                "status=" + state +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

}
