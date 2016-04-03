package com.sig.tonglisecurity.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class RechargeResultActivity extends ParentActivity implements
        OnClickListener, HttpRequestAsyncTask.TaskListenerWithState {

    private TextView title;
    private TextView left_btn;
    private LinearLayout recharge_bill;
    private ImageView recharge_stamp;
    private TranslateAnimation billAnim;
    private ScaleAnimation stampAnim;
    private TextView title_recharge_or_redeem;
    private TextView recharge_time;
    private TextView recharge_trade_date;
    private TextView recharge_requestno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_recharge_step3);
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.recharge_result));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        recharge_bill = (LinearLayout) findViewById(R.id.recharge_bill);
        recharge_bill.setVisibility(View.INVISIBLE);
        recharge_stamp = (ImageView) findViewById(R.id.recharge_stamp);
        title_recharge_or_redeem = (TextView) findViewById(R.id.title_recharge_or_redeem);
        Intent intent = getIntent();
        recharge_time = (TextView) findViewById(R.id.recharge_time);
        recharge_time.setText(intent.getStringExtra("recharge_time"));
        recharge_requestno = (TextView) findViewById(R.id.recharge_requestno);
        recharge_requestno.setText(intent.getStringExtra("recharge_requestno"));
        recharge_trade_date = (TextView) findViewById(R.id.recharge_trade_date);
        recharge_trade_date.setText(intent.getStringExtra("recharge_trade_date"));

        if (intent.getIntExtra("recharge_or_redeem", 0) == 1) {
            title.setText(R.string.redeem_result);
            recharge_stamp.setImageResource(R.drawable.redeem_succed);
            title_recharge_or_redeem.setText(R.string.wallet_fast_cash);
        }

        startBillAnimation();
    }

    private void recharge() {
        showProgressDialog(this, "���ڴ�����...");
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_wallet_recharge);
        request.putParam("session_id", MainActivity.MyAccount.getSession_id())
                .putParam("password", getIntent().getStringExtra("password"))
                .putParam("amount", getIntent().getStringExtra("amount"))
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

    private void redeem() {
        showProgressDialog(this, "���ڴ�����...");
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_wallet_redeemf);
        request.putParam("session_id", MainActivity.MyAccount.getSession_id())
                .putParam("password", getIntent().getStringExtra("password"))
                .putParam("amount", getIntent().getStringExtra("amount").replace(",", ""))
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
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startBillAnimation() {
        Handler hand = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                billAnim = new TranslateAnimation(0, 0,
                        recharge_bill.getHeight() * -1, 0);
                billAnim.setDuration(2000);
                billAnim.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        recharge_stamp.setVisibility(View.VISIBLE);
                        startStampAnimation();

                    }
                });
                recharge_bill.startAnimation(billAnim);
                recharge_bill.setVisibility(View.VISIBLE);
            }
        };

        hand.sendEmptyMessageDelayed(0, 200);
    }

    private void startStampAnimation() {

        stampAnim = new ScaleAnimation(5, 1, 5, 1);
        stampAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                playSound();
            }
        });
        stampAnim.setDuration(500);
        recharge_stamp.startAnimation(stampAnim);

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
                    recharge_requestno.setText(jsonObject.getString("requestno"));
                    recharge_trade_date.setText(ConfigUtil.formatDate(jsonObject
                            .getString("trade_date")));
                    recharge_time.setText(ConfigUtil.formatTime(jsonObject
                            .getString("time")));
                    if (req.equals(Urls.URL_wallet_recharge)) {
                        if (jsonObject.getString("result").equals("1")) {
                            recharge_time.setText(jsonObject.getString("time"));
                            recharge_stamp.setImageResource(R.drawable.recharge_succed);
                        } else if (jsonObject.getString("result").equals("2")) {
                            recharge_stamp.setImageResource(R.drawable.recharge_failed);
                        } else {
                            recharge_stamp.setImageResource(R.drawable.recharge_failed);
                            Toast.makeText(this, jsonObject.getString("result"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else if (request.getRequestUrl().equals(Urls.URL_wallet_redeemf)) {
                        if (jsonObject.getString("result").equals("1")) {
                            recharge_time.setText(jsonObject.getString("time"));
                            recharge_stamp.setImageResource(R.drawable.redeem_succed);
                        } else if (jsonObject.getString("result").equals("2")) {
                            recharge_stamp.setImageResource(R.drawable.redeem_failed);
                        } else {
                            recharge_stamp.setImageResource(R.drawable.redeem_failed);
                            Toast.makeText(this, jsonObject.getString("result"),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    startBillAnimation();
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

    private void playSound() {
        NotificationManager notifMgr = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notif = new Notification();
        notif.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tap);
        notifMgr.notify(1, notif);
    }
}
