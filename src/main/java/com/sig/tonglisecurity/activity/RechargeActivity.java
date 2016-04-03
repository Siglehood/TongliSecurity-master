package com.sig.tonglisecurity.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 充值界面
 */
public class RechargeActivity extends ParentActivity
        implements OnClickListener, TextWatcher, HttpRequestAsyncTask.TaskListenerWithState {

    private TextView title;
    private TextView left_btn, right_btn, recharge_amount_in_words;
    private EditText wallet_recharge_amount;
    private String beforeTextChanged = "";
    private Spinner mSpinner;
    private List<Channel> mListData = new ArrayList<Channel>();
    private MySpinnerAdapter mSpinnerAdapter = new MySpinnerAdapter();
    private View recharge_next_step;
    private int isFormated = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_recharge_step1);
        initViews();
        refresh();
    }

    public void initViews() {
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.wallet_charge));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        right_btn = (TextView) findViewById(R.id.right_btn);
        mSpinner = (Spinner) findViewById(R.id.spinner1);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if (mListData.get(position).getSupport_withhold() != 1) {
                    recharge_next_step.setEnabled(false);
                    showToast(context.getString(R.string.recharge_not_support));
                } else {
                    recharge_next_step.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner.setAdapter(mSpinnerAdapter);
        recharge_next_step = findViewById(R.id.recharge_next_step);
        recharge_next_step.setOnClickListener(this);
        wallet_recharge_amount = (EditText) findViewById(R.id.recharge_amount);
        wallet_recharge_amount.addTextChangedListener(this);
        recharge_amount_in_words = (TextView) findViewById(R.id.recharge_amount_in_words);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge_next_step:
                Channel ch = (Channel) mSpinner.getSelectedItem();
                MainActivity.MyRechargeChannel = ch;
                if (ch == null) {
                    Toast.makeText(this, "��ѡ���ֵ����", Toast.LENGTH_SHORT).show();
                    return;
                }
                String min = ch.getRecharge_min();
                String max = ch.getRecharge_max();
                String amount = wallet_recharge_amount.getText().toString().replace(",", "");
                if (ch.getSupport_withhold() != 1) {
                    Toast.makeText(this, "���ʽ�������֧�ִ��", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount.equals("")) {
                    Toast.makeText(this, "�������ֵ���", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!min.equals("") && Double.parseDouble(min) > Double.parseDouble(amount)) {
                    Toast.makeText(this, "��С��ֵ���Ϊ" + ConfigUtil.digitUppercase(min),
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (!max.equals("") && Double.parseDouble(max) < Double.parseDouble(amount)) {
                    Toast.makeText(this, "����ֵ���Ϊ" + ConfigUtil.digitUppercase(max),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(this, RechargeConfirmActivity.class);
                intent.putExtra("name", ch.getName());
                intent.putExtra("account", ch.getCard_no());
                intent.putExtra("recharge_amount", amount);
                startActivity(intent);
                break;
            case R.id.left_btn:
                finish();
            default:
                break;
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        String str = s.toString();
        Channel ch = (Channel) mSpinner.getSelectedItem();
        try {
            if (!str.equals("") && !ch.getRecharge_max().equals("") && Double.parseDouble(str.replace(",", "")) > Double.parseDouble(ch.getRecharge_max())) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        beforeTextChanged = str;

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Channel ch = (Channel) mSpinner.getSelectedItem();
        String str = s.toString().replace(",", "");
        if (str.equals(".")) {
            wallet_recharge_amount.setText("0.");
            return;
        }
        try {
            if (!str.equals("") && !ch.getRecharge_max().equals("") && Double.parseDouble(str) > Double.parseDouble(ch.getRecharge_max())) {
                wallet_recharge_amount.setText(beforeTextChanged);
                Toast.makeText(this, "����ֵ���Ϊ" + ConfigUtil.digitUppercase(ch.getRecharge_max()), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String str2 = wallet_recharge_amount.getText().toString().replace(",", "");
            if (str2.equals("")) {
                recharge_amount_in_words.setText("");
            } else {
                recharge_amount_in_words.setText(ConfigUtil
                        .digitUppercase(str2));
            }
        } catch (Exception e) {
            Toast.makeText(this, "���������", Toast.LENGTH_SHORT).show();
        }

        int dot = str.indexOf(".");
        if (dot != -1 && dot + 3 < str.length()) {
            str = str.substring(0, dot + 3);
        }
        try {
            if (++isFormated % 2 == 0) {
                NumberFormat format = NumberFormat.getInstance();
                format.setMaximumFractionDigits(2);
                if (dot != -1) {
                    format.setMinimumFractionDigits(str.length() - str.indexOf(".") - 1);
                }
                if (!str.equals("") && !ch.getRecharge_max().equals("") && Double.parseDouble(str) > Double.parseDouble(ch.getRecharge_max())) {
                    wallet_recharge_amount.setText(beforeTextChanged);
                    return;
                }
                if (!str.endsWith(".")) {// || str.endsWith(".0")|| str.endsWith(".00")) {
                    wallet_recharge_amount.setText(format.format(Double.parseDouble(str)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void refresh() {
        showProgressDialog(this, getString(R.string.dlg_loading));
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_wallet_channel);
        request
                .putParam("session_id", MainActivity.MyAccount.getSession_id());
        new HttpRequestAsyncTask(request, this, context).execute();
    }

    protected void querySession() {
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_wallet_rate_login);
        request.putParam("session_id", MainActivity.MyAccount.getSession_id());
        new HttpRequestAsyncTask(request, this, context).execute();
    }

    @Override
    public void onTaskOver(HttpRequestInfo request, HttpResponseInfo info) {
        dismissProgressDialog();
        String req = request.getRequestUrl();
        switch (info.getState()) {
            case STATE_NO_NETWORK_CONNECT:
                Toast.makeText(context, "û�����磬���������������", Toast.LENGTH_SHORT).show();
                break;
            case STATE_TIME_OUT:
                Toast.makeText(context, "���緱æ,���Ժ�����", Toast.LENGTH_SHORT).show();
                break;
            case STATE_UNKNOWN:
                Toast.makeText(context, "δ֪����", Toast.LENGTH_SHORT).show();
                break;
            case STATE_OK:
                String response = info.getResult();
                try {
                    mListData.clear();
                    JSONObject jobj = new JSONObject(response);
                    JSONArray jarr = jobj.getJSONArray("channels");
//				if(jarr.length()==0){
//					((InputMethodManager) context
//							.getSystemService(Context.INPUT_METHOD_SERVICE))
//							.hideSoftInputFromWindow(context.getCurrentFocus()
//									.getWindowToken(),
//									InputMethodManager.HIDE_NOT_ALWAYS);
//					querySession();
//				}
                    if (jarr.getJSONObject(0).has("isLoginOut") && jarr.getJSONObject(0).getString("isLoginOut").equals("Y")) {
                        showSingleAlertDlg(context.getString(R.string.dlg_session_invalid));
                        return;
                    }
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject jchannel = jarr.getJSONObject(i);
                        Channel channel = new Channel();
                        channel.setName(jchannel.getString("name"));
                        channel.setCode(jchannel.getString("code"));
                        channel.setAccount(jchannel.getString("account"));
                        channel.setCard_no(jchannel.getString("card_no"));
                        channel.setRecharge_max(jchannel.getString("recharge_max"));
                        channel.setRecharge_min(jchannel.getString("recharge_min"));
                        channel.setSupport_withhold(jchannel.getInt("support_withhold"));
                        channel.setSupport_redeemf(jchannel.getInt("support_redeemf"));
                        channel.setAmount(jchannel.getString("amount"));
                        channel.setFee_rate(jchannel.getString("fee_rate"));
                        channel.setFund_code(jchannel.getString("fund_code"));
                        channel.setBankIdCode(jchannel.getString("bankIdCode"));
                        channel.setCapitalMode(jchannel.getString("capitalMode"));
                        if (channel.getSupport_withhold() == 1) {
                            mListData.add(0, channel);
                        } else {
                            mListData.add(channel);
                        }
                    }
                    mSpinnerAdapter.notifyDataSetChanged();
                    for (int i = 0; i < mListData.size(); i++) {
                        if (mListData.get(i).getSupport_withhold() == 1) {
                            mSpinner.setSelection(i);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private class MySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

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
                convertView = LayoutInflater.from(context).inflate(R.layout.my_simple_spinner_item_recharge, null);
            }
            TextView view = (TextView) convertView
                    .findViewById(R.id.recharge_spinner_view);
            view.setText(mListData.get(position).getName());
            if (mListData.get(position).getSupport_withhold() == 1) {
                view.setEnabled(true);
            } else {
                view.setEnabled(false);
            }
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.my_simple_spinner_dropdown_item_recharge, null);
            }
            CheckedTextView view = (CheckedTextView) convertView.findViewById(R.id.recharge_spinner_item);
            view.setText(mListData.get(position).getName());
            if (mListData.get(position).getSupport_withhold() == 1) {
                view.setEnabled(true);
                view.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.icon_withhold),
                        null);
            } else {
                view.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
                        null);
                view.setEnabled(false);
            }
            return convertView;
        }

    }
}
