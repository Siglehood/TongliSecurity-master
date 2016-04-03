package com.sig.tonglisecurity.http;

/**
 * Http 响应
 */
public class HttpResponseInfo {
    private HttpTaskState state;
    private String result;

    public HttpResponseInfo(String result, HttpTaskState state) {
        this.result = result;
        this.state = state;
    }

    public HttpTaskState getState() {
        return state;
    }

    public void setState(HttpTaskState state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public enum HttpTaskState {
        STATE_OK,
        STATE_NO_NETWORK_CONNECT,
        STATE_TIME_OUT,
        STATE_UNKNOWN,
    }

}
