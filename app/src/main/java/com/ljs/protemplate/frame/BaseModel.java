package com.ljs.protemplate.frame;

import com.ljs.protemplate.http.Http;
import com.ljs.protemplate.mvp.IModel;

/**
 * Created by ljs on 2018/3/13.
 * Desc: 数据处理层基类
 */

public class BaseModel implements IModel {

    protected static HttpService httpService;

    static {
        httpService= Http.getHttpService();
    }
}
