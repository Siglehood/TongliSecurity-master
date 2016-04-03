package com.sig.tonglisecurity.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.activity.MainActivity;
import com.sig.tonglisecurity.activity.RechargeActivity;
import com.sig.tonglisecurity.activity.RedeemFastActivity;
import com.sig.tonglisecurity.activity.WalletQueryActivity;
import com.sig.tonglisecurity.bean.AccountInfo;
import com.sig.tonglisecurity.fragment.LoginFragment;
import com.sig.tonglisecurity.http.HttpRequestInfo;
import com.sig.tonglisecurity.http.HttpResponseInfo;
import com.sig.tonglisecurity.http.Urls;
import com.sig.tonglisecurity.task.HttpRequestAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.widget.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * 1. 熟悉自定义控件 XListView，将�?��界面作为 view,通过 mXListView.addHeaderView(headerView,null,false);
 * 加载界面，此listview设置�?mXListView.setAdapter(null);
 * 下拉刷新 设置mXlistView.setPullRefreshEnable(true);
               mXListView.setPullLoadEnable(false);
               mXListView.setXListViewListener(mXlistViewlistener);
               
   2. 通过XML编写较为合理的界面
   
   3. 替换另一个framentManager--transaction.replace(R.id.fs_view, new LoginFragment());
   	 FragmentManager回滚---transaction.addToBackStack("login");
   FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.fs_view, new LoginFragment());
		transaction.addToBackStack("login");
		transaction.commit();
 * 
 */

/**
 * 钱袋子
 */
public class FSView extends BaseView {

    public static boolean isLogin = false;
    private TextView title;
    private TextView right_btn;
    private TextView wallet_time, walletSevenDayRate;
    private TextView wallet_available_balance,
            walletUnSignInPrompt;
    private View headerView;
    private View infoPanel1;
    private OnClickListener mOnClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.wallet_charge:
                    if (isLogin) {
                        context.startActivity(new Intent(context, RechargeActivity.class));
                    } else {
//					((MainActivity)context).changeTab(1);
                        gotoLoginFragment();
                    }
                    break;
                case R.id.wallet_fast_cash:
                    if (isLogin) {
                        Intent intent = new Intent(context, RedeemFastActivity.class);
                        intent.putExtra("wallet_available_balance", wallet_available_balance.getText().toString());
                        intent.putExtra("wallet_undistributed_income", "---");
                        context.startActivity(intent);
                    } else {
//					((MainActivity)context).changeTab(1);
                        gotoLoginFragment();
                    }
                    break;
                case R.id.wallet_query:
                    if (isLogin) {
                        context.startActivity(new Intent(context, WalletQueryActivity.class));
                    } else {
                        gotoLoginFragment();
                    }
                    break;
                case R.id.right_btn:
                    if (!isLogin) {
                        gotoLoginFragment();
                    } else {
                        showDoubleAlertDlg(context.getString(R.string.dlg_logout_ask),
                                context.getString(R.string.dlg_sure),
                                context.getString(R.string.dlg_cancel));
                        break;
                    }
                    break;

                default:
                    break;
            }
        }
    };
    private XListView mXListView;
    private HttpRequestAsyncTask.TaskListenerWithState mHttpTaskListener = new HttpRequestAsyncTask.TaskListenerWithState() {

        @Override
        public void onTaskOver(HttpRequestInfo request, HttpResponseInfo info) {
            dismissProgressDialog();
            mXListView.stopRefresh();
            switch (info.getState()) {
                case STATE_NO_NETWORK_CONNECT:
                    Toast.makeText(context, "没有网络，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_TIME_OUT:
                    Toast.makeText(context, "网络繁忙,请稍后在试", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_UNKNOWN:
                    Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_OK:
                    String response = info.getResult();
                    if (request.getRequestUrl().equals(Urls.URL_base_logout)) {  //�˳����ؽ��
                        try {
                            if (response != null) {
                                JSONObject json = new JSONObject(response);
                                if (json.getString("result").equals("1")) {
                                    isLogin = !isLogin;
                                    MainActivity.MyAccount = new AccountInfo();
                                    context.sendBroadcast(new Intent(
                                            "com.hctforgf.gff.signin"));
                                    switchView();
                                } else if (json.getString("result").equals("2")) {
                                    Toast.makeText(
                                            context,
                                            context.getString(R.string.account_logout_fail),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, json.getString("result"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            if (response != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if (jsonObject.getString("result").equals("3")) {
                                        InputStream in = context.getAssets().open(
                                                "sevenday_rate_info.txt");
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


                                JSONObject json = new JSONObject(response);
                                if (json.has("isLoginOut") && json.getString("isLoginOut").equals("Y")) {
                                    showSingleAlertDlg(context.getString(R.string.dlg_session_invalid));
                                    return;
                                }
                                wallet_time.setText(ConfigUtil.formatDate(json.getString("time")));
                                String str = json.getString("sevenday_rate");
                                float rate = Float.parseFloat(str);
                                if (rate < 0) {
                                    str = "-" + str + "%";
                                } else if (rate > 0) {
                                    str = "+" + str + "%";
                                }
                                walletSevenDayRate.setText(str);
                                if (!MainActivity.MyAccount.getSession_id().equals("")) {

                                    wallet_available_balance.setText(ConfigUtil
                                            .getFormatAmount(json
                                                    .getString("balance")));

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mXListView.stopRefresh();
                        mXListView.setRefreshTime(ConfigUtil.getRefreshTime(ConfigUtil.getNowTime()));
                    }
                    break;
                default:
                    break;
            }
        }

    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String session_id = intent.getStringExtra("session_id");
            if (session_id != null) {
                isLogin = true;
            } else {
                isLogin = false;
            }
            switchView();
            query();
        }
    };
    private XListView.IXListViewListener mXlistViewlistener = new XListView.IXListViewListener() {

        @Override
        public void onRefresh() {
            query();
        }

        @Override
        public void onLoadMore() {

        }
    };

    public FSView(FragmentActivity activity, View v) {
        super(activity, v);
        activity.registerReceiver(broadcastReceiver, new IntentFilter("com.hctforgf.gff.signin"));
    }

    protected void query() {
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_wallet_rate_logout);
        if (!MainActivity.MyAccount.getSession_id().equals("")) {
            request.setRequestUrl(Urls.URL_wallet_rate_login);
            request.putParam("session_id", MainActivity.MyAccount.getSession_id());
        }
        new HttpRequestAsyncTask(request, mHttpTaskListener, context).execute();
    }


    @Override
    public void initBaseView(View v) {
        super.initBaseView(v);
        title = (TextView) v.findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.tab_title_03));
        right_btn = (TextView) v.findViewById(R.id.right_btn);
        right_btn.setVisibility(View.VISIBLE);
        right_btn.setOnClickListener(mOnClick);

        mXListView = (XListView) v.findViewById(R.id.wallet_xlistview);
        headerView = LayoutInflater.from(context).inflate(R.layout.c_a_wallet, mXListView, false);
        mXListView.addHeaderView(headerView, null, false);
        mXListView.setAdapter(null);
        mXListView.setPullRefreshEnable(true);
        mXListView.setPullLoadEnable(false);
        mXListView.setXListViewListener(mXlistViewlistener);
        wallet_time = (TextView) headerView.findViewById(R.id.wallet_time);
        walletSevenDayRate = (TextView) headerView.findViewById(R.id.wallet_sevenday_rate);
        wallet_available_balance = (TextView) headerView.findViewById(R.id.wallet_available_balance);
        walletUnSignInPrompt = (TextView) headerView.findViewById(R.id.wallet_unsignin_prompt);
        headerView.findViewById(R.id.wallet_charge).setOnClickListener(mOnClick);
        headerView.findViewById(R.id.wallet_fast_cash).setOnClickListener(mOnClick);
        headerView.findViewById(R.id.wallet_query).setOnClickListener(mOnClick);
        infoPanel1 = headerView.findViewById(R.id.wallet_infopanel1);
        switchView();
        query();
    }

    private void logout() {
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_base_logout);
        request.putParam("session_id", "" + MainActivity.MyAccount.getSession_id());
        new HttpRequestAsyncTask(request, mHttpTaskListener, context).execute();
    }

    private void switchView() {
        if (isLogin) {
            right_btn.setText(R.string.login_sign_out);
            title.setText(MainActivity.MyAccount.getName() + "的" + context.getString(R.string.tab_title_03));
            walletUnSignInPrompt.setVisibility(View.GONE);
            infoPanel1.setVisibility(View.VISIBLE);

        } else {
            right_btn.setText(R.string.login_sign_in);
            walletUnSignInPrompt.setVisibility(View.VISIBLE);
            infoPanel1.setVisibility(View.GONE);
            title.setText(context.getString(R.string.tab_title_03));
        }
    }

    public void yesBtnWork() {
        showProgressDialog(context, context.getString(R.string.dlg_logout));
        logout();
    }

    public void noBtnWork() {
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
        logout();
    }

    @Override
    protected void finalize() throws Throwable {
        context.unregisterReceiver(broadcastReceiver);
        super.finalize();
    }

    private void gotoLoginFragment() {
        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fs_view, new LoginFragment());
        transaction.addToBackStack("login");
        transaction.commit();
    }
}
