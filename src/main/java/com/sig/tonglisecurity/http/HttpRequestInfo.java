package com.sig.tonglisecurity.http;



import com.sig.tonglisecurity.utils.LogUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Http 请求
 */
public class HttpRequestInfo {

    public static final String TAG = "HttpRequestInfo";

    /* Http 请求的 URL */
    private String requestUrl;
    private int requestID;
    private Map<String, String> requestParams;

    public HttpRequestInfo(String url) {
        this.setRequestUrl(url);
        requestParams = new HashMap<String, String>();
    }

    public HttpRequestInfo(String url, Map<String, String> params) {
        this.setRequestUrl(url);
        this.setRequestParams(params);
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, String> requestParams) {
        this.requestParams = requestParams;
    }

    public String getParamsStr() {
        String str = "";
        if (requestParams != null) {
            for (Entry<String, String> entry : requestParams.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                key = URLEncoder.encode(key);
                val = URLEncoder.encode(val);
                str += key + "=" + val + "&";
            }
        }
        if (str.equals("")) {
            return null;
        }
        LogUtil.i(TAG, this.requestUrl + str);
        return str;
    }

    public HttpRequestInfo putParam(String key, String value) {
        this.requestParams.put(key, value);
        return this;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

}
