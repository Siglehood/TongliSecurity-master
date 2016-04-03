package com.sig.tonglisecurity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.AccountInfo;
import com.sig.tonglisecurity.bean.Channel;
import com.sig.tonglisecurity.http.HttpRequestInfo;
import com.sig.tonglisecurity.http.HttpResponseInfo;
import com.sig.tonglisecurity.http.Urls;
import com.sig.tonglisecurity.task.HttpRequestAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.widget.XListViewForTrades;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 快速兑换界面
 */
public class RedeemFastActivity extends ParentActivity implements
        OnClickListener, OnItemClickListener, HttpRequestAsyncTask.TaskListenerWithState {

    private TextView title;
    private TextView left_btn;
    private XListViewForTrades redeem_list;
    private RedeemAdapter mRedeemListAdapter = new RedeemAdapter();
    private ArrayList<Channel> mListData = new ArrayList<Channel>();
    private XListViewForTrades.IXListViewListener mxlistListener = new XListViewForTrades.IXListViewListener() {

        @Override
        public void onRefresh() {
            refresh();
        }

        @Override
        public void onLoadMore() {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_redeem_step1);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.wallet_fast_cash));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        redeem_list = (XListViewForTrades) findViewById(R.id.redeem_list);
        redeem_list.setXListViewListener(mxlistListener);
        redeem_list.setAdapter(mRedeemListAdapter);
        redeem_list.setPullRefreshEnable(true);
        redeem_list.setPullLoadEnable(false);
        redeem_list.setOnItemClickListener(this);
        Intent intent = getIntent();
        ((TextView) findViewById(R.id.redeem_available_balance)).setText(intent
                .getStringExtra("wallet_available_balance"));
        ((TextView) findViewById(R.id.redeem_undistributed_income))
                .setText(intent.getStringExtra("wallet_undistributed_income"));
        showProgressDialog(this, getString(R.string.dlg_loading));
        refresh();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (mListData.size() > position - 2 && position >= 2) {

            MainActivity.MyRedeemChannel = mListData.get(position - 2);
            Intent intent = new Intent(this, RedeemFastInputActivity.class);
            intent.putExtra("channel", mListData.get(position - 2).getName());
            intent.putExtra("amount", mListData.get(position - 2).getAmount());
            intent.putExtra("recharge_max", mListData.get(position - 2)
                    .getRecharge_max());
            intent.putExtra("recharge_min", mListData.get(position - 2)
                    .getRecharge_min());
            startActivity(intent);
        }
    }

    private void refresh() {
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_wallet_channel);
        request.putParam("session_id", MainActivity.MyAccount.getSession_id());
        new HttpRequestAsyncTask(request, this, context).execute();
    }

    @Override
    public void onTaskOver(HttpRequestInfo request, HttpResponseInfo info) {
        dismissProgressDialog();
        redeem_list.stopRefresh();
        String req = request.getRequestUrl();
        switch (info.getState()) {
            case STATE_NO_NETWORK_CONNECT:
                Toast.makeText(context, "网络无连接", Toast.LENGTH_SHORT)
                        .show();
                break;
            case STATE_TIME_OUT:
                Toast.makeText(context, "连接超时", Toast.LENGTH_SHORT).show();
                break;
            case STATE_UNKNOWN:
                Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                break;
            case STATE_OK:
                String response = info.getResult();
                try {
                    mListData.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("channels");
                    if (jsonArray.getJSONObject(0).has("isLoginOut")
                            && jsonArray.getJSONObject(0).getString("isLoginOut")
                            .equals("Y")) {
                        showSingleAlertDlg(context
                                .getString(R.string.dlg_session_invalid));
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jChannel = jsonArray.getJSONObject(i);
                        if (jChannel.getInt("support_redeemf") == 2
                                || Double.parseDouble(jChannel.getString("amount")) == 0) {
                            continue;
                        }

                        Channel channel = new Channel();
                        channel.setName(jChannel.getString("name"));
                        channel.setCode(jChannel.getString("code"));
                        channel.setAccount(jChannel.getString("account"));
                        channel.setCard_no(jChannel.getString("card_no"));
                        channel.setRecharge_max(jChannel.getString("recharge_max"));
                        channel.setRecharge_min(jChannel.getString("recharge_min"));
                        channel.setSupport_withhold(jChannel
                                .getInt("support_withhold"));
                        channel.setSupport_redeemf(jChannel
                                .getInt("support_redeemf"));
                        channel.setAmount(jChannel.getString("amount"));
                        channel.setFee_rate(jChannel.getString("fee_rate"));
                        channel.setFund_code(jChannel.getString("fund_code"));
                        channel.setBankIdCode(jChannel.getString("bankIdCode"));
                        channel.setCapitalMode(jChannel.getString("capitalMode"));
                        mListData.add(channel);
                    }
                    mRedeemListAdapter.notifyDataSetChanged();
                    redeem_list.stopRefresh();
                    redeem_list.setRefreshTime(ConfigUtil.getRefreshTime(ConfigUtil
                            .getNowTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                    showSingleAlertDlg(getString(R.string.dlg_session_invalid));
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
        context.sendBroadcast(new Intent("com.hctforgf.gff.signin"));
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private class RedeemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.item_redeem_list, null);
                ViewHolder holder = new ViewHolder(convertView);
                holder.setData(mListData.get(position));
                convertView.setTag(holder);
            } else {
                ((ViewHolder) convertView.getTag()).setData(mListData
                        .get(position));
            }
            return convertView;
        }
    }

    private class ViewHolder {
        private Channel mData;
        private TextView name;
        private TextView amount;
        private ImageView logo;
        private View mRootView;

        public ViewHolder(View convertView) {
            mRootView = convertView;
            initChildView();
        }

        private void initChildView() {
            name = (TextView) mRootView.findViewById(R.id.redeem_name);
            amount = (TextView) mRootView.findViewById(R.id.redeem_amount);
            logo = (ImageView) mRootView.findViewById(R.id.redeem_logo);
        }

        public void setData(Channel data) {
            mData = data;
            if (mData != null) {
                name.setText(mData.getName());
                amount.setText(ConfigUtil.getFormatAmount(mData.getAmount()));
                String name = mData.getName();
                logo.setVisibility(View.VISIBLE);
                if (name.equals("��������")) {
                    // 工商银行
                    logo.setImageResource(R.drawable.ic_icbc);
                } else if (name.equals("�������")) {
                    logo.setImageResource(R.drawable.ic_gd);
                } else if (name.equals("�㷢����")) {
                    logo.setImageResource(R.drawable.ic_gf);
                } else if (name.equals("�㸶����")) {
                    logo.setImageResource(R.drawable.ic_hftx);
                } else if (name.equals("��������")) {
                    logo.setImageResource(R.drawable.ic_js);
                } else if (name.equals("��ͨ����")) {
                    logo.setImageResource(R.drawable.ic_jt);
                } else if (name.equals("������")) {
                    logo.setImageResource(R.drawable.ic_jh);
                } else if (name.equals("�Ͼ�����")) {
                    logo.setImageResource(R.drawable.ic_nj);
                } else if (name.equals("ũҵ����")) {
                    logo.setImageResource(R.drawable.ic_ny);
                } else if (name.equals("�ַ�����")) {
                    logo.setImageResource(R.drawable.ic_pf);
                } else if (name.equals("�Ϻ�ũ������")) {
                    logo.setImageResource(R.drawable.ic_shns);
                } else if (name.equals("�Ϻ�����")) {
                    logo.setImageResource(R.drawable.ic_sh);
                } else if (name.equals("����ӯ")) {
                    logo.setImageResource(R.drawable.ic_icbc);
                } else if (name.equals("ͨ������")) {
                    logo.setImageResource(R.drawable.ic_tltx);
                } else if (name.equals("��ҵ����")) {
                    logo.setImageResource(R.drawable.ic_xy);
                } else if (name.equals("��ɳ����")) {
                    logo.setImageResource(R.drawable.ic_cs);
                } else if (name.equals("��������")) {
                    logo.setImageResource(R.drawable.ic_cmbc);
                } else if (name.equals("�й�����")) {
                    logo.setImageResource(R.drawable.ic_bc);
                } else if (name.equals("֧����")) {
                    logo.setImageResource(R.drawable.ic_zfb);
                } else if (name.equals("�й���������")) {
                    logo.setImageResource(R.drawable.ic_zgms);
                } else if (name.equals("�й�ƽ��")) {
                    logo.setImageResource(R.drawable.ic_zgpa);
                } else if (name.equals("��������")) {
                    logo.setImageResource(R.drawable.ic_zx);
                } else {
                    logo.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
