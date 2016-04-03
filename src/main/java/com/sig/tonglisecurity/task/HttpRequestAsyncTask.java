package com.sig.tonglisecurity.task;

import android.content.Context;
import android.os.AsyncTask;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.http.HttpManager;
import com.sig.tonglisecurity.http.HttpRequestInfo;
import com.sig.tonglisecurity.http.HttpResponseInfo;
import com.sig.tonglisecurity.utils.ConfigUtil;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Http 请求异步任务类
 */
public class HttpRequestAsyncTask
        extends AsyncTask<Void, Void, HttpResponseInfo> {

    private HttpRequestInfo mRequest;
    private TaskListener mListener;
    private TaskListenerWithState mListenerWithState;
    private Context context;

    public HttpRequestAsyncTask(HttpRequestInfo request, TaskListener listener, Context context) {

        this.context = context;
        request.putParam("device_info", ConfigUtil.getImei(context))
                .putParam("app_version", ConfigUtil.getVersionName(context))
                .putParam("market", context.getString(R.string.channel_str));
        mListener = listener;
        mRequest = request;

    }

    public HttpRequestAsyncTask(HttpRequestInfo request, TaskListenerWithState taskListenerWithState,
                                Context context) {
        this.context = context;
        request.putParam("device_info", ConfigUtil.getImei(context))
                .putParam("app_version", ConfigUtil.getVersionName(context))
                .putParam("market", context.getString(R.string.channel_str));
        mListenerWithState = taskListenerWithState;
        mRequest = request;
    }

    @Override
    protected HttpResponseInfo doInBackground(Void... params) {
        if (!ConfigUtil.isConnect(context)) {
            return new HttpResponseInfo(null, HttpResponseInfo.HttpTaskState.STATE_NO_NETWORK_CONNECT);
        }
        try {
            if (mRequest != null) {
                if (mRequest.getRequestID() == -2) {
                    return new HttpResponseInfo(
                            HttpManager.postHttpRequest(mRequest),
                            HttpResponseInfo.HttpTaskState.STATE_OK);
                }
                return new HttpResponseInfo(
                        HttpManager.postHttpsRequest(mRequest),
                        HttpResponseInfo.HttpTaskState.STATE_OK);
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return new HttpResponseInfo("{\"result\":\"3\"}", HttpResponseInfo.HttpTaskState.STATE_OK);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return new HttpResponseInfo("{\"result\":\"3\"}", HttpResponseInfo.HttpTaskState.STATE_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpResponseInfo("{\"result\":\"3\"}", HttpResponseInfo.HttpTaskState.STATE_OK);
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(HttpResponseInfo response) {
        super.onPostExecute(response);
        if (mListener != null) {
            mListener.onTaskOver(mRequest, response.getResult());
        }
        if (mListenerWithState != null) {
            mListenerWithState.onTaskOver(mRequest, response);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public interface TaskListener {
        void onTaskOver(HttpRequestInfo request, String response);
    }

    public interface TaskListenerWithState {
        void onTaskOver(HttpRequestInfo request, HttpResponseInfo info);
    }
}
