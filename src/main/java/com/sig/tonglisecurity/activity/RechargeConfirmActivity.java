package com.sig.tonglisecurity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.AccountInfo;
import com.sig.tonglisecurity.http.HttpRequestInfo;
import com.sig.tonglisecurity.http.HttpResponseInfo;
import com.sig.tonglisecurity.http.Urls;
import com.sig.tonglisecurity.task.HttpRequestAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;

import org.json.JSONObject;

/**
 * 充值信息确认界面
 */
public class RechargeConfirmActivity extends ParentActivity implements OnClickListener, HttpRequestAsyncTask.TaskListenerWithState {
    private TextView title;
    private TextView left_btn;
    private Button recharge_go;
    private EditText password;
    private ScrollView scrollview;
    private Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            scrollview.fullScroll(View.FOCUS_DOWN);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_recharge_step2);
        initViews();
        Intent intent = getIntent();
        scrollview = (ScrollView) findViewById(R.id.scrollview);
        ((TextView) findViewById(R.id.recharge_confirm_name)).setText(intent.getStringExtra("name"));
        ((TextView) findViewById(R.id.recharge_confirm_account)).setText(ConfigUtil.getHidedAccount(intent.getStringExtra("account")));
        ((TextView) findViewById(R.id.recharge_confirm_amount)).setText(ConfigUtil
                .getFormatAmount(intent.getStringExtra("recharge_amount")));
        ((TextView) findViewById(R.id.recharge_confirm_chinese_amount)).setText(ConfigUtil.digitUppercase(intent.getStringExtra("recharge_amount")));
        password = ((EditText) findViewById(R.id.recharge_confirm_password));
        password.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hand.sendEmptyMessageDelayed(0, 300);
            }
        });

    }

    @Override
    public void onUserInteraction() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isActive()) {
            hand.sendEmptyMessageDelayed(0, 300);
        }

        super.onUserInteraction();
    }

    public void initViews() {
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.recharge_confirm));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        recharge_go = (Button) findViewById(R.id.recharge_go);
        recharge_go.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                finish();
                break;
            case R.id.recharge_go:

                if (password.getText().toString().equals("")) {
                    Toast.makeText(this, "�����뽻������", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.getText().toString().length() < 6) {
                    Toast.makeText(this, "��������6λ", Toast.LENGTH_SHORT).show();
                    return;
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                recharge();
                break;
            default:
                break;
        }
    }

    private void recharge() {
        showProgressDialog(this, "���ڳ�ֵ...");
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_wallet_recharge);
        request.putParam("session_id", MainActivity.MyAccount.getSession_id())
                .putParam("password", password.getText().toString())
                .putParam("amount", getIntent().getStringExtra("recharge_amount"))
                .putParam("fee", "0.00")
                .putParam("account",
                        MainActivity.MyRechargeChannel.getAccount())
                .putParam("card_no",
                        MainActivity.MyRechargeChannel.getCard_no())
                .putParam("channel_code",
                        MainActivity.MyRechargeChannel.getCode())
                .putParam("bankIdCode",
                        MainActivity.MyRechargeChannel.getBankIdCode())
                .putParam("capitalMode",
                        MainActivity.MyRechargeChannel.getCapitalMode());
        new HttpRequestAsyncTask(request, this, this).execute();
    }

    @Override
    public void onTaskOver(HttpRequestInfo request, HttpResponseInfo info) {
        dismissProgressDialog();

        switch (info.getState()) {
            case STATE_NO_NETWORK_CONNECT:
                Toast.makeText(context, "��������ʧ�ܣ����������������", Toast.LENGTH_SHORT).show();
                break;
            case STATE_TIME_OUT:
                Toast.makeText(context, "���緱æ,���Ժ�����", Toast.LENGTH_SHORT).show();
                break;
            case STATE_UNKNOWN:
                Toast.makeText(context, "δ֪����", Toast.LENGTH_SHORT).show();
                break;
            case STATE_OK:
                try {
                    String response = info.getResult();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("isLoginOut") && jsonObject.getString("isLoginOut").equals("Y")) {
                        showSingleAlertDlg(context.getString(R.string.dlg_session_invalid));
                        return;
                    }
                    if (jsonObject.getString("result").equals("1")) {
                        Intent intent = new Intent(this, RechargeResultActivity.class);
                        intent.putExtra("recharge_requestno", jsonObject.getString("requestno"));
                        intent.putExtra("recharge_trade_date", ConfigUtil.formatDate(jsonObject
                                .getString("trade_date")));
                        intent.putExtra("recharge_time", jsonObject
                                .getString("time"));
                        startActivity(intent);
                        return;
                    } else {
                        showToast(jsonObject.getString("result"));
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void showSingleAlertDlg(String msg) {
        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.dlg_sure),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                singleBtnWork();
                            }
                        }).create().show();
    }

    @Override
    public void singleBtnWork() {
        MainActivity.MyAccount = new AccountInfo();
        context.sendBroadcast(new Intent(
                "com.hctforgf.gff.signin"));
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
