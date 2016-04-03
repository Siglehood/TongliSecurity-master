package com.sig.tonglisecurity.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.http.HttpRequestInfo;
import com.sig.tonglisecurity.http.HttpResponseInfo;
import com.sig.tonglisecurity.http.UrlsOther;
import com.sig.tonglisecurity.task.HttpRequestAsyncTask;
import com.sig.tonglisecurity.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 反馈界面
 */
public class FeedbackActivity extends ParentActivity implements OnClickListener, HttpRequestAsyncTask.TaskListenerWithState {

    public static final String TAG = "FeedbackActivity";

    private TextView title;
    private TextView left_btn, right_btn;
    private EditText mContext;
    private EditText mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "FeedbackActivity--> onCreate");

        setContentView(R.layout.a_feedback);
        init();
    }

    public void init() {
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(getString(R.string.settings_feedback));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        right_btn = (TextView) findViewById(R.id.right_btn);
        right_btn.setText("提交");
        right_btn.setBackgroundResource(R.drawable.common_btn_drawable);
        right_btn.setOnClickListener(this);
        right_btn.setVisibility(View.VISIBLE);
        mContext = (EditText) findViewById(R.id.context);
        mContext.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mContext.setHint("");
                } else {
                    mContext.setHint(getString(R.string.feedback_content_hint));
                }
            }
        });
        mContact = (EditText) findViewById(R.id.contact);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                if (mContext.getText().toString().equals("")) {
                    showToast("请输入反馈信息");
                    return;
                }
                String contactStr = mContact.getText().toString();
                if (!contactStr.equals("") && !contactStr.matches("^0?\\d{11}$") && !contactStr.matches("^\\(?\\d{3,4}[-\\)]?\\d{7,8}$")
                        && !contactStr.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
                    showToast("请输入联系电话或者邮箱");
                    return;
                }
                feedback();
                break;
            case R.id.left_btn:
                finish();
                break;
            default:
                break;
        }

    }

    private void feedback() {
        showProgressDialog(this, "正在发送反馈信息...");
        HttpRequestInfo request = new HttpRequestInfo(UrlsOther.URL_addOPinion);
        request.putParam("context", mContext.getText().toString())
                .putParam("contact", mContact.getText().toString());
        request.setRequestID(-2);
        new HttpRequestAsyncTask(request, this, this).execute();
    }

    @Override
    public void onTaskOver(HttpRequestInfo request, HttpResponseInfo info) {
        dismissProgressDialog();

        switch (info.getState()) {
            case STATE_NO_NETWORK_CONNECT:
                Toast.makeText(context, "当前网络并没有连接", Toast.LENGTH_SHORT).show();
                break;
            case STATE_TIME_OUT:
                Toast.makeText(context, "连接超时，请重新发送", Toast.LENGTH_SHORT).show();
                break;
            case STATE_UNKNOWN:
                Toast.makeText(context, "未知错误，请重新发送", Toast.LENGTH_SHORT).show();
                break;
            case STATE_OK:
                String response = info.getResult();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("result").equals("3")) {
                        InputStream in = context.getAssets().open(
                                "feedup_info.txt");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = in.read(buffer)) != -1) {
                            bos.write(buffer, 0, length);
                        }

                        in.close();
                        response = bos.toString("UTF-8");
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("state").equals("0")) {
                        showToast("发送成功");
                    } else {
                        showToast("发送失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("出现未知错误，请重新发送");
                }
                break;
            default:
                break;
        }
    }
}
