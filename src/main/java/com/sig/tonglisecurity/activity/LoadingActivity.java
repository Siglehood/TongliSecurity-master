package com.sig.tonglisecurity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.app.Config;
import com.sig.tonglisecurity.bean.AddDeviceResultBean;
import com.sig.tonglisecurity.task.BaseHandlerUI;
import com.sig.tonglisecurity.task.Controller;
import com.sig.tonglisecurity.task.UIAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.DataUtil;
import com.sig.tonglisecurity.utils.LogUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载界面
 */
public class LoadingActivity extends ParentActivity {

    public static final String TAG = "LoadingActivity";
    private Handler getUploadHandler = new UpLoadHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "LoadingActivity");

        if (!DataUtil.getUploadInfo(context)) {
            addDeviceInfo();
        }
        setContentView(R.layout.a_loading);

        begin();
    }

    private void startIndex() {
        if (DataUtil.getNavigate(this)) {
            LogUtil.i(TAG, "startIndex [A]");
            gotoMain();
        } else {
            LogUtil.i(TAG, "startIndex [B]");
            gotoMain();
        }
    }

    private void begin() {
        AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
            protected String doInBackground(String... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {
                startIndex();
            }
        };
        task.execute("");
    }

    /**
     * 跳转到主页
     */
    private void gotoMain() {
        Intent intent = new Intent();
        intent.setClass(LoadingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void addDeviceInfo() {
        // 在 onCreate 中调用 ..
        // 封装键值对, 再通过
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", ConfigUtil.getImei(context)));
        params.add(new BasicNameValuePair("phonesys", "an"));
        params.add(new BasicNameValuePair("phonemodel", "an"));
        params.add(new BasicNameValuePair("osversion", "an"));
        params.add(new BasicNameValuePair("tel", ConfigUtil.getPhoneNumber(context)));
        params.add(new BasicNameValuePair("channel", getString(R.string.channel_str)));
        params.add(new BasicNameValuePair("pwd", Config.UP_PWD));

        Controller.getInstance().execute(
                new UIAsyncTask(
                        context, getUploadHandler,
                        params, BaseHandlerUI.REQUEST_add_device
                ));
    }

    static final class UpLoadHandler extends Handler {

        private Context context;

        public UpLoadHandler(Context context) {
            this.context = context;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseHandlerUI.TASK_NOTIFY_RETURN_DATA:
                    if (msg.obj != null) {
                        try {
                            AddDeviceResultBean bean = (AddDeviceResultBean) msg.obj;
                            if (bean.state.equals("0")) {
                                DataUtil.saveUploadInfo(context, true);
                            }
                        } catch (Exception e) {
                            LogUtil.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }
}
