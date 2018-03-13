package com.ljs.protemplate.http;

/**
 * Created by ljs on 2018/3/13.
 * Desc:自定义服务端异常
 */

public class ServerException extends RuntimeException{

    public int state;
    public int code;
    public String msg;

    public ServerException(int state, int code, String msg) {
        super(msg);
        this.state = state;
        this.code = code;
        this.msg = msg;
    }

}
