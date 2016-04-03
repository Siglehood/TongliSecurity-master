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
 * 快速提现确认
 */
public class RedeemFastConfirmActivity extends ParentActivity implements OnClickListener, HttpRequestAsyncTask.TaskListenerWithState {

    private TextView title;
    private TextView left_btn;
    private Button redeem_immediately;
    private EditText password;
    private TextView amount;
    private ScrollView scrollview;
    private Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            scrollview.fullScroll(View.FOCUS_DOWN);
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_redeem_step3);
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.redeem_confirm));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        redeem_immediately = (Button) findViewById(R.id.redeem_immediately);
        redeem_immediately.setOnClickListener(this);
        ((TextView) findViewById(R.id.redeem_arrive_bank)).setText(MainActivity.MyRedeemChannel.getName());
        ((TextView) findViewById(R.id.redeem_arrive_account)).setText(ConfigUtil.getHidedAccount(MainActivity.MyRedeemChannel.getCard_no()));
        ((TextView) findViewById(R.id.redeem_amount_chinese)).setText(ConfigUtil.digitUppercase(getIntent().getStringExtra("amount")));
        amount = ((TextView) findViewById(R.id.redeem_amount));
        amount.setText(ConfigUtil
                .getFormatAmount(getIntent().getStringExtra("amount")));
        Double fee = Double.parseDouble(MainActivity.MyRedeemChannel.getFee_rate()) * Double.parseDouble(getIntent().getStringExtra("amount"));
        ((TextView) findViewById(R.id.redeem_fee)).setText(fee.toString());
        password = (EditText) findViewById(R.id.redeem_password);
        password.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hand.sendEmptyMessageDelayed(0, 300);
            }
        });
        scrollview = (ScrollView) findViewById(R.id.scrollview);
    }

    @Override
    public void onUserInteraction() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //如果没有�?��
        if (imm.isActive()) {
            hand.sendEmptyMessageDelayed(0, 300);
        }
        super.onUserInteraction();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redeem_immediately:

                if (password.getText().toString().equals("")) {
                    Toast.makeText(this, "�����뽻������", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.getText().toString().length() < 6) {
                    Toast.makeText(this, "��������6λ", Toast.LENGTH_SHORT).show();
                    return;
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                redeem();
                break;
            case R.id.left_btn:
                finish();
            default:
                break;
        }
    }

    private void redeem() {
        showProgressDialog(this, "正在处理�?..");
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_wallet_redeemf);
        request.putParam("session_id", MainActivity.MyAccount.getSession_id())
                .putParam("password", password.getText().toString())
                .putParam("amount", amount.getText().toString().replace(",", ""))
                .putParam("fee", "0.00")
                .putParam("account",
                        MainActivity.MyRedeemChannel.getAccount())
                .putParam("card_no",
                        MainActivity.MyRedeemChannel.getCard_no())
                .putParam("bankIdCode",
                        MainActivity.MyRedeemChannel.getBankIdCode())
                .putParam("capitalMode",
                        MainActivity.MyRedeemChannel.getCapitalMode());
        new HttpRequestAsyncTask(request, this, this).execute();
    }

    @Override
    public void onTaskOver(HttpRequestInfo request, HttpResponseInfo info) {
        dismissProgressDialog();
        String req = request.getRequestUrl();
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
                        intent.putExtra("recharge_or_redeem", 1);
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
