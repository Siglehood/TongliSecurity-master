package com.sig.tonglisecurity.task;

public class BaseHandlerUI {
    public final static int TASK_NOTIFY_RETURN_DATA = 1;    //

    // 定义了 Http 请求的参数常量
    public final static int REQUEST_GET_FAV = 10000;
    public final static int REQUEST_GET_INIT_DATA = REQUEST_GET_FAV + 1;
    public final static int REQUEST_GET_TOPIC_DATA = REQUEST_GET_INIT_DATA + 1;
    public final static int REQUEST_fund_tenstock = REQUEST_GET_TOPIC_DATA + 1;
    public final static int REQUEST_fund_feerate = REQUEST_fund_tenstock + 1;
    public final static int REQUEST_getHelps = REQUEST_fund_feerate + 1;
    public final static int REQUEST_fund_tenbond = REQUEST_getHelps + 1;

    public final static int REQUEST_getMessageByPage = REQUEST_fund_tenbond + 1;
    public final static int REQUEST_getMsgByLogin = REQUEST_getMessageByPage + 1;
    public final static int REQUEST_getVersion = REQUEST_getMsgByLogin + 1;
    public final static int REQUEST_add_device = REQUEST_getVersion + 1;
    public final static int REQUEST_get_allfund = REQUEST_add_device + 1;
}
