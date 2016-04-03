package com.sig.tonglisecurity.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.activity.ForgetPasswordActivity;
import com.sig.tonglisecurity.activity.MainActivity;
import com.sig.tonglisecurity.adapter.ExtensibleListViewAdapter;
import com.sig.tonglisecurity.app.Config;
import com.sig.tonglisecurity.bean.AccountInfo;
import com.sig.tonglisecurity.bean.Capital;
import com.sig.tonglisecurity.bean.Profit;
import com.sig.tonglisecurity.bean.SalesChannel;
import com.sig.tonglisecurity.bean.Trade;
import com.sig.tonglisecurity.database.DatabaseAdapter;
import com.sig.tonglisecurity.http.HttpRequestInfo;
import com.sig.tonglisecurity.http.HttpResponseInfo;
import com.sig.tonglisecurity.http.Urls;
import com.sig.tonglisecurity.task.HttpRequestAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.utils.SortUtil;
import com.sig.tonglisecurity.widget.XListViewForTrades;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MemberView extends BaseView implements TextWatcher {

    public static final String TAG = "MemberView";
    private final int DATA_NUM = 10;
    String result;
    private TextView mTitle;
    private TextView mLeftBtn, mRightBtn;
    private Button login_sign_in;
    private EditText login_number;
    private EditText login_password;
    private CheckBox login_keep_password;
    private ViewPager mQueryResultPanel;
    private XListViewForTrades mListViewProfits, mListViewCapitals;
    private XListViewForTrades mListViewTrades;
    private TextView mCapitals, mWalletBalance, mYearProfit;
    private TextView mTotalDate, mTotalProfits, mTotalProfitsRate;
    private TextView mDay, mMonth, mYear, mThisYear;
    private int mProfitsRequestID;
    private Spinner mSpinnerIdType;
    private View mListFloatTitle;
    private List<Object> mListProfit = new ArrayList<Object>();
    private List<Object> mListCapital = new ArrayList<Object>();
    private List<Object> mListTrade = new ArrayList<Object>();
    private DatabaseAdapter mDatabaseAdapter;
    private String sort_capitals_attributeName;
    private String sort_profits_attributeName;
    private String sort_trades_attributeName;
    private SortUtil.Order sort_capitals_order = SortUtil.Order.DEFAULT;
    private SortUtil.Order sort_profits_order = SortUtil.Order.DEFAULT;
    private SortUtil.Order sort_trades_order = SortUtil.Order.DEFAULT;
    private TextView sort_t_confirm_amount,
            sort_p_profit, sort_p_profit_rate;
    private TextView[] sort_c_fund_share = new TextView[2];
    private TextView[] sort_c_fund_capital = new TextView[2];
    private View sort_p_profit_rate_container;
    private View sort_p_profit_container;
    private View[] sort_c_fund_share_container = new View[2];
    private View[] sort_c_fund_capital_container = new View[2];
    private View verification_layout, verification_change;
    private TextView verification_input, verification_code;
    private boolean mIsLogined;
    private View mIndicatorCapitals;
    private View mIndicatorProfits;
    private View mIndicatorTrades;
    private OnPageChangeListener mPageChageListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    changePage(R.id.account_indicator_capitals);
                    break;
                case 1:
                    changePage(R.id.account_indicator_profits);
                    break;
                case 2:
                    changePage(R.id.account_indicator_trades);
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private List<View> mListViews;
    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + position;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position));
            return mListViews.get(position);
        }
    };
    private ExtensibleListViewAdapter mAdapterProfits;
    private ExtensibleListViewAdapter mAdapterCapitals;
    private ExtensibleListViewAdapter mAdapterTrades;
    private PopupWindow menuWindow;
    private View mProfitsListFooter;
    private TextView mTotalProfitsTitle;
    private TextView mCapitalsRefreshTime;
    private TextView mProfitsRefreshTime;
    private TextView mTradessRefreshTime;
    private HttpRequestAsyncTask.TaskListenerWithState mHttpTaskListener = new HttpRequestAsyncTask.TaskListenerWithState() {

        @Override
        public void onTaskOver(HttpRequestInfo request, HttpResponseInfo info) {
            dismissProgressDialog();
            String req = request.getRequestUrl();
            switch (info.getState()) {
                case STATE_NO_NETWORK_CONNECT:
                    Toast.makeText(context, "没有网络，请检查您的网络连接", Toast.LENGTH_SHORT).show();

                    if (req.equals(Urls.URL_acco_profit_new)) {
                        mListViewProfits.stopRefresh();
                    } else if (req.equals(Urls.URL_acco_hold_new)) {
                        mListViewCapitals.stopRefresh();
                    } else if (req.equals(Urls.URL_acco_trade_new)) {
                        mListViewTrades.stopRefresh();
                        mListViewTrades.stopLoadMore();
                    }

                    break;

                case STATE_TIME_OUT:

                    Toast.makeText(context, "网络繁忙,请稍后在试", Toast.LENGTH_SHORT).show();
                    if (req.equals(Urls.URL_acco_profit_new)) {
                        mListViewProfits.stopRefresh();
                    } else if (req.equals(Urls.URL_acco_hold_new)) {
                        mListViewCapitals.stopRefresh();
                    } else if (req.equals(Urls.URL_acco_trade_new)) {
                        mListViewTrades.stopRefresh();
                        mListViewTrades.stopLoadMore();
                    }

                    break;

                case STATE_UNKNOWN:

                    Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                    if (req.equals(Urls.URL_acco_profit_new)) {
                        mListViewProfits.stopRefresh();
                    } else if (req.equals(Urls.URL_acco_hold_new)) {
                        mListViewCapitals.stopRefresh();
                    } else if (req.equals(Urls.URL_acco_trade_new)) {
                        mListViewTrades.stopRefresh();
                        mListViewTrades.stopLoadMore();
                    }

                    break;

                case STATE_OK:
                    try {
                        JSONObject jsonObject = new JSONObject(info.getResult());
                        if (jsonObject.has("isLoginOut") && jsonObject.getString("isLoginOut").equals("Y")) {
                            if (req.equals(Urls.URL_acco_login_new)) {
                                verification_layout.setVisibility(View.VISIBLE);
                                changeVerification();
                                Toast.makeText(context, info.getResult(), Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                            showSingleAlertDlg(context.getString(R.string.dlg_session_invalid));
                            return;
                        }
                        if (req.equals(Urls.URL_acco_login_new)) {   //
                            if (jsonObject.getString("result").equals("3")) {  //
                                try {
                                    InputStream in = context.getAssets().open("login_in_info.txt");
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                                    byte[] buffer = new byte[2048];
                                    int length;
                                    while ((length = in.read(buffer)) != -1) {
                                        bos.write(buffer, 0, length);   //
                                    }
                                    in.close();
                                    result = bos.toString("UTF-8");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            jsonObject = new JSONObject(result);

                            if (jsonObject.getString("result").equals("1")) {
                                saveCacheAccount();
                                MainActivity.MyAccount.setSession_id(jsonObject
                                        .getString("session_id"));
                                MainActivity.MyAccount.setName(jsonObject
                                        .getString("name"));
                                mIsLogined = true;
                                switchView();
                                Intent broadcastIntent = new Intent(
                                        "com.hctforgf.gff.signin");
                                broadcastIntent.putExtra("session_id",
                                        jsonObject.getString("session_id"));
                                context.sendBroadcast(broadcastIntent);

                                showProgressDialog(context, context.getString(R.string.dlg_loading));
                                queryCapitals(jsonObject.getString("session_id"));
                                queryProfits(jsonObject.getString("session_id"), R.id.thisyear);
                                queryTrades(jsonObject.getString("session_id"), mListTrade.size(), DATA_NUM, true);
                                verification_layout.setVisibility(View.GONE);
                                changeVerification();
                            } else {
                                verification_layout.setVisibility(View.VISIBLE);
                                changeVerification();
                                Toast.makeText(context, jsonObject.getString("result"), Toast.LENGTH_SHORT)
                                        .show();
                            }

                        } else if (req.equals(Urls.URL_base_logout)) {
                            if (jsonObject.getString("result").equals("3")) {
                                try {
                                    InputStream in = context.getAssets().open("login_in_info.txt");
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    byte[] buffer = new byte[2048];
                                    int length;
                                    while ((length = in.read(buffer)) != -1) {
                                        bos.write(buffer, 0, length);
                                    }
                                    in.close();
                                    result = bos.toString("UTF-8");
                                    LogUtil.i(TAG, "!!!!!############--result--��" + result);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            jsonObject = new JSONObject(result);


                            if (jsonObject.getString("result").equals("1")) {
                                clearAccountData();
                                mIsLogined = false;
                                context.sendBroadcast(new Intent("com.hctforgf.gff.signin"));
                                switchView();
                            } else if (jsonObject.getString("result").equals("2")) {
                                Toast.makeText(context, context.getString(R.string.account_logout_fail), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                            }
                        } else if (req.equals(Urls.URL_acco_profit_new)) {
                            if (jsonObject.getString("result").equals("3")) {
                                try {
                                    InputStream in = context.getAssets().open("profits_info.txt");
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                                    byte[] buffer = new byte[2048];
                                    int length;
                                    while ((length = in.read(buffer)) != -1) {
                                        bos.write(buffer, 0, length);
                                    }
                                    in.close();
                                    result = bos.toString("UTF-8");
                                    LogUtil.i(TAG, "!!!!!############--result--��" + result);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            jsonObject = new JSONObject(result);

                            sort_p_profit
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.sort), null);
                            sort_p_profit_rate
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.sort), null);
                            sort_profits_order = SortUtil.Order.DEFAULT;
                            mListViewProfits.stopRefresh();
                            JSONArray arr = jsonObject.getJSONArray("profits");
                            mListProfit.clear();
                            mTotalProfits.setText(ConfigUtil.getFormatAmount(jsonObject.getString("acco_profit")));
                            mTotalProfitsRate.setText(jsonObject.getString("acco_profit_rate") + "%");
                            mTotalDate.setText(request.getRequestParams().get(
                                    "date_begin")
                                    + "到" + request.getRequestParams().get("date_end"));
                            switch (request.getRequestID()) {
                                case R.id.day:
                                    mTotalProfitsTitle.setText("当天盈亏");
                                    break;
                                case R.id.month:
                                    mTotalProfitsTitle.setText("最近一月");
                                    break;
                                case R.id.year:
                                    mTotalProfitsTitle.setText("最近一年");
                                    break;
                                case R.id.thisyear:
                                    mTotalProfitsTitle.setText("今年以来");
                                    break;
                                default:
                                    break;
                            }
                            mDatabaseAdapter.open_fund();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject j = arr.getJSONObject(i);
                                Profit profit = new Profit();
                                profit.setProfit_rate(j.getString("profit_rate"));
                                if (Double.parseDouble(j.getString("profit")) == 0) {
                                    continue;
                                }
                                profit.setProfit(j.getString("profit"));
                                profit.setFund_code(j.getString("fund_code"));
                                Cursor c = mDatabaseAdapter.fetchFromFundByFundCode(j.getString("fund_code"));
                                if (c != null) {
                                    if (c.moveToNext()) {
                                        profit.setFund_code(c.getString(c
                                                .getColumnIndex("fund_name")));
                                    }
                                }
                                c.close();
                                profit.setCost(j.getString("cost"));
                                profit.setCapital_end(j.getString("capital_end"));
                                profit.setCapital_begin(j.getString("capital_begin"));
                                mListProfit.add(profit);
                            }
                            mDatabaseAdapter.close_funds();
                            mAdapterProfits.notifyDataSetChanged();
                            mListViewProfits.stopRefresh();
                            String time = ConfigUtil.getRefreshTime(ConfigUtil.getNowTime());
                            mListViewProfits.setRefreshTime(time);
                            mProfitsRefreshTime.setText(time);
                        } else if (req.equals(Urls.URL_acco_hold_new)) {
                            LogUtil.i(TAG, "!!!!!#####��½���ص�����1###----��");

                            if (jsonObject.getString("result").equals("3")) {
                                try {
                                    InputStream in = context.getAssets().open("hold_cap_info.txt");
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    //��ȡ����
                                    byte[] buffer = new byte[2048];
                                    int length;
                                    while ((length = in.read(buffer)) != -1) {
                                        bos.write(buffer, 0, length);
                                    }
                                    in.close();
                                    result = bos.toString("UTF-8");
                                    LogUtil.i(TAG, "!!!!!############--result--��" + result);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            jsonObject = new JSONObject(result);

                            mListViewCapitals.stopRefresh();
                            sort_c_fund_capital[0]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.sort), null);
                            sort_c_fund_share[0]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.sort), null);
                            sort_c_fund_capital[1]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.sort), null);
                            sort_c_fund_share[1]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.sort), null);
                            sort_capitals_order = SortUtil.Order.DEFAULT;
                            mCapitals.setText(ConfigUtil.getFormatAmount(jsonObject.getString("capitals")));
                            mWalletBalance.setText(ConfigUtil.getFormatAmount(jsonObject.getString("wallet_balance")));
                            mYearProfit.setText(ConfigUtil.getFormatAmount(jsonObject.getString("year_profit")));
                            JSONArray arr = jsonObject.getJSONArray("funds");
                            mListCapital.clear();
                            mDatabaseAdapter.open_fund();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject j = arr.getJSONObject(i);
                                Capital capital = new Capital();
                                capital.setFund_code(j.getString("fund_code"));
                                Cursor c = mDatabaseAdapter.fetchFromFundByFundCode(j.getString("fund_code"));
                                if (c != null) {
                                    if (c.moveToNext()) {
                                        capital.setFund_code(c.getString(c
                                                .getColumnIndex("fund_name")));
                                    }
                                }
                                c.close();
                                if (Double.parseDouble(j.getString("fund_share")) == 0) {
                                    continue;

                                }
                                capital.setFund_share(j.getString("fund_share"));
                                capital.setFund_capital(j.getString("fund_capital"));
                                capital.setFund_channel(j.getString("fund_channel"));
                                capital.setFund_fee_type(j.getString("fund_fee_type"));
                                capital.setFund_bonus(j.getString("fund_bonus"));
                                boolean existedFund = false;
                                SalesChannel saleschannel = new SalesChannel(
                                        capital.getFund_channel(),
                                        j.getString("fund_share"),
                                        j.getString("fund_capital"));
                                for (int index = 0; index < mListCapital.size(); index++) {
                                    Capital temp = (Capital) mListCapital
                                            .get(index);
                                    if (temp.getFund_code().equals(
                                            capital.getFund_code())) {
                                        temp.addSalesChannel(saleschannel);
                                        existedFund = true;
                                        break;
                                    }
                                }
                                if (!existedFund) {
                                    capital.addSalesChannel(saleschannel);
                                    mListCapital.add(capital);
                                }
                            }
                            mDatabaseAdapter.close_funds();
                            mAdapterCapitals.notifyDataSetChanged();
                            mListViewCapitals.stopRefresh();
                            String time = ConfigUtil.getRefreshTime(ConfigUtil.getNowTime());
                            mListViewCapitals.setRefreshTime(time);
                            mCapitalsRefreshTime.setText(time);
                        } else if (req.equals(Urls.URL_acco_trade_new)) {
                            LogUtil.i(TAG, "!!!!!#####��½���ص�����1###----��");
                            if (jsonObject.getString("result").equals("3")) {
                                try {
                                    InputStream in = context.getAssets().open("trades_info.txt");
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    byte[] buffer = new byte[2048];
                                    int length = 0;
                                    while ((length = in.read(buffer)) != -1) {
                                        bos.write(buffer, 0, length);
                                    }
                                    in.close();
                                    result = bos.toString("UTF-8");
                                    LogUtil.i(TAG, "!!!!!############--result--��" + result);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            jsonObject = new JSONObject(result);


                            mListViewTrades.stopRefresh();
                            JSONArray arr = jsonObject.getJSONArray("trades");
                            if (arr.length() == 0) {
                                queryCapitals(MainActivity.MyAccount.getSession_id());
                                return;
                            }
                            if (request.getRequestID() == 1) {
                                mListTrade.clear();
                                mListViewTrades.setPullLoadEnable(true);
                            }
                            int oldSize = mListTrade.size();
                            mDatabaseAdapter.open_fund();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject jFundTrades = arr.getJSONObject(i);
                                String fund_code = jFundTrades.getString("fund_code");
                                Cursor c = mDatabaseAdapter.fetchFromFundByFundCode(jFundTrades.getString("fund_code"));
                                if (c != null) {
                                    if (c.moveToNext()) {
                                        fund_code = c.getString(c.getColumnIndex("fund_name"));
                                    }
                                }
                                c.close();
                                JSONArray trade_records = jFundTrades.getJSONArray("trade_records");
                                for (int index = 0; index < trade_records.length(); index++) {
                                    JSONObject j = trade_records.getJSONObject(index);
                                    Trade trade = new Trade();
                                    trade.setFund_code(fund_code);
                                    trade.setBusi_name(j.getString("busi_name"));
                                    trade.setTime(j.getString("time"));
                                    trade.setConfirm_amount(j.getString("confirm_amount"));
                                    trade.setConfirm_share(j.getString("confirm_share"));
                                    trade.setFee(j.getString("fee"));
                                    trade.setFee_type(j.getString("fee_type"));
                                    trade.setChannel(j.getString("channel"));
                                    trade.setState(j.getString("state"));
                                    mListTrade.add(trade);
                                }
                            }
                            mDatabaseAdapter.close_funds();
                            mAdapterTrades.notifyDataSetChanged();
                            mListViewTrades.stopRefresh();
                            mListViewTrades.stopLoadMore();
                            if (mListTrade.size() - oldSize < DATA_NUM) {
                                mListViewTrades.setPullLoadEnable(false);
                            } else {
                                mListViewTrades.setPullLoadEnable(true);
                            }
                            String time = ConfigUtil.getRefreshTime(ConfigUtil.getNowTime());
                            mListViewTrades.setRefreshTime(time);
                            mTradessRefreshTime.setText(time);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private OnClickListener mOnClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            LogUtil.i(TAG, "onClick");
            switch (view.getId()) {
                case R.id.login_sign_in:

                    if (login_number.getText().toString().equals("")) {
                        showToast(context.getString(R.string.MemberView_01));
                        return;
                    }
                    if (login_password.getText().toString().equals("")) {
                        showToast(context.getString(R.string.MemberView_02));
                        return;
                    }
                    if (verification_layout.getVisibility() == View.VISIBLE && !verification_input.getText().toString().equals(verification_code.getText().toString())) {
                        Toast.makeText(context, context.getString(R.string.MemberView_03), Toast.LENGTH_SHORT).show();
                        changeVerification();
                        return;
                    }
                    login();
                    break;

                case R.id.login_change_verification:
                    changeVerification();
                    break;

                case R.id.right_btn:
                    showDoubleAlertDlg(context.getString(R.string.dlg_logout_ask),
                            context.getString(R.string.dlg_sure),
                            context.getString(R.string.dlg_cancel));
                    break;

                case R.id.login_forget_pasword:
                    context.startActivity(new Intent(context, ForgetPasswordActivity.class));
                    break;

                case R.id.account_indicator_profits:
                    popupMenu();
                case R.id.account_indicator_trades:
                case R.id.account_indicator_capitals:
                    changePage(view.getId());
                    break;

                case R.id.day:
                case R.id.month:
                case R.id.year:
                case R.id.thisyear:
                    menuWindow.dismiss();
                    mProfitsRequestID = view.getId();
                    showProgressDialog(context, context.getString(R.string.dlg_loading));
                    queryProfits(MainActivity.MyAccount.getSession_id(), view.getId());
                    break;

                case R.id.sort_t_confirm_amount_container:
                    sort_trades_attributeName = "confirm_amount";
                    switch (sort_trades_order) {
                        case DEFAULT:
                        case ASC:
                            sort_trades_order = SortUtil.Order.DESC;
                            sort_t_confirm_amount
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.desc), null);
                            break;
                        case DESC:
                            sort_trades_order = SortUtil.Order.ASC;
                            sort_t_confirm_amount
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.asc), null);
                            break;
                        default:
                            break;
                    }
                    SortUtil.sortTrades(mListTrade, sort_trades_attributeName, sort_trades_order);
                    mAdapterTrades.notifyDataSetChanged();
                    break;
                case R.id.sort_p_profit_container:
                    sort_p_profit_rate
                            .setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    context.getResources().getDrawable(
                                            R.drawable.sort), null);
                    sort_profits_attributeName = "profit";
                    switch (sort_profits_order) {
                        case DEFAULT:
                        case ASC:
                            sort_profits_order = SortUtil.Order.DESC;
                            sort_p_profit
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.desc), null);
                            break;
                        case DESC:
                            sort_profits_order = SortUtil.Order.ASC;
                            sort_p_profit
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.asc), null);
                            break;
                        default:
                            break;
                    }
                    SortUtil.sortProfits(mListProfit, sort_profits_attributeName, sort_profits_order);
                    mAdapterProfits.notifyDataSetChanged();
                    break;
                case R.id.sort_p_profit_rate_container:
                    sort_p_profit
                            .setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    context.getResources().getDrawable(
                                            R.drawable.sort), null);
                    sort_profits_attributeName = "profit_rate";
                    switch (sort_profits_order) {
                        case DEFAULT:
                        case ASC:
                            sort_profits_order = SortUtil.Order.DESC;
                            sort_p_profit_rate
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.desc), null);
                            break;
                        case DESC:
                            sort_profits_order = SortUtil.Order.ASC;
                            sort_p_profit_rate
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.asc), null);
                            break;
                        default:
                            break;
                    }
                    SortUtil.sortProfits(mListProfit, sort_profits_attributeName, sort_profits_order);
                    mAdapterProfits.notifyDataSetChanged();
                    break;
                case R.id.sort_c_fund_capital_container:
                case R.id.sort_c_fund_capital_container2:
                    sort_c_fund_share[0]
                            .setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    context.getResources().getDrawable(
                                            R.drawable.sort), null);
                    sort_c_fund_share[1]
                            .setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    context.getResources().getDrawable(
                                            R.drawable.sort), null);
                    sort_capitals_attributeName = "fund_capital";
                    switch (sort_capitals_order) {
                        case DEFAULT:
                        case ASC:
                            sort_capitals_order = SortUtil.Order.DESC;
                            sort_c_fund_capital[0]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.desc), null);
                            sort_c_fund_capital[1]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.desc), null);
                            break;
                        case DESC:
                            sort_capitals_order = SortUtil.Order.ASC;
                            sort_c_fund_capital[0]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.asc), null);
                            sort_c_fund_capital[1]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.asc), null);
                            break;
                        default:
                            break;
                    }
                    SortUtil.sortCapitals(mListCapital, sort_capitals_attributeName, sort_capitals_order);
                    mAdapterCapitals.notifyDataSetChanged();
                    break;
                case R.id.sort_c_fund_share_container:
                case R.id.sort_c_fund_share_container2:
                    sort_c_fund_capital[0]
                            .setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    context.getResources().getDrawable(
                                            R.drawable.sort), null);
                    sort_c_fund_capital[1]
                            .setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    context.getResources().getDrawable(
                                            R.drawable.sort), null);
                    sort_capitals_attributeName = "fund_share";
                    switch (sort_capitals_order) {
                        case DEFAULT:
                        case ASC:
                            sort_capitals_order = SortUtil.Order.DESC;
                            sort_c_fund_share[0]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.desc), null);
                            sort_c_fund_share[1]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.desc), null);
                            break;
                        case DESC:
                            sort_capitals_order = SortUtil.Order.ASC;
                            sort_c_fund_share[0]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.asc), null);
                            sort_c_fund_share[1]
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            null,
                                            context.getResources().getDrawable(
                                                    R.drawable.asc), null);
                            break;
                        default:
                            break;
                    }
                    SortUtil.sortCapitals(mListCapital, sort_capitals_attributeName, sort_capitals_order);
                    mAdapterCapitals.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

        }
    };
    private XListViewForTrades.IXListViewListener mListViewProfitslistener = new XListViewForTrades.IXListViewListener() {

        @Override
        public void onRefresh() {
            queryProfits(MainActivity.MyAccount.getSession_id(), mProfitsRequestID);
        }

        @Override
        public void onLoadMore() {
        }
    };
    private XListViewForTrades.IXListViewListener mListViewCapitalslistener = new XListViewForTrades.IXListViewListener() {

        @Override
        public void onRefresh() {
            queryCapitals(MainActivity.MyAccount.getSession_id());
        }

        @Override
        public void onLoadMore() {
        }
    };
    private XListViewForTrades.IXListViewListener mListViewTradeslistener = new XListViewForTrades.IXListViewListener() {

        @Override
        public void onRefresh() {
            queryTrades(MainActivity.MyAccount.getSession_id(), 0, DATA_NUM, true);
        }

        @Override
        public void onLoadMore() {
            queryTrades(MainActivity.MyAccount.getSession_id(), mListTrade.size(), DATA_NUM, false);
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String session_id = intent.getStringExtra("session_id");
            if (session_id == null) {
                mIsLogined = false;
                clearAccountData();
                switchView();
            } else {
                if (intent.getBooleanExtra("from_wallet_login", false)) {
                    mIsLogined = true;
                    switchView();
                    queryCapitals(session_id);
                    queryProfits(session_id, R.id.thisyear);
                    queryTrades(session_id, mListTrade.size(), DATA_NUM, true);
                }
            }
        }
    };

    public MemberView(FragmentActivity activity, View v) {
        super(activity, v);
        activity.registerReceiver(broadcastReceiver, new IntentFilter("com.hctforgf.gff.signin"));
    }

    /**
     * 保存用户数据
     */
    private void saveCacheAccount() {
        if (mSpinnerIdType.getSelectedItem() == null) {
            return;
        }
        context.getSharedPreferences("account_history",
                Context.MODE_PRIVATE)
                .edit()
                .putString(
                        mSpinnerIdType.getSelectedItem().toString(),
                        login_keep_password.isChecked() ? login_number
                                .getText().toString() : "")
                .commit();
    }

    private String getCacheAccount() {
        if (mSpinnerIdType.getSelectedItem() == null) {
            return "";
        }
        SharedPreferences prefer = context.getSharedPreferences("account_history", Context.MODE_PRIVATE);
        if (prefer != null) {
            return prefer.getString(mSpinnerIdType.getSelectedItem().toString(), "");
        }
        return "";
    }

    @Override
    public void initBaseView(View v) {
        super.initBaseView(v);
        mDatabaseAdapter = new DatabaseAdapter(context, Config.database_version);
        mTitle = (TextView) v.findViewById(R.id.title_txt);
        mTitle.setText(context.getString(R.string.login_tab_title));
        mLeftBtn = (TextView) v.findViewById(R.id.left_btn);
        mRightBtn = (TextView) v.findViewById(R.id.right_btn);

        login_sign_in = (Button) v.findViewById(R.id.login_sign_in);
        login_sign_in.setOnClickListener(mOnClick);
        login_number = (EditText) v.findViewById(R.id.login_id_number);
        login_number.addTextChangedListener(this);
        login_password = (EditText) v.findViewById(R.id.login_id_password);
        login_password.addTextChangedListener(this);
        login_keep_password = (CheckBox) v.findViewById(R.id.login_keep_password);
        login_keep_password.setChecked(true);
        v.findViewById(R.id.login_forget_pasword).setOnClickListener(mOnClick);

        mSpinnerIdType = (Spinner) v.findViewById(R.id.spinner_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mSpinnerIdType.setPadding(10, 0, 0, 0);
        }
        mSpinnerIdType.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                v.getContext(), R.array.id_types, R.layout.my_simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.my_simple_spinner_dropdown_item);
        mSpinnerIdType.setAdapter(adapter);
        login_number.setText(getCacheAccount());
        mSpinnerIdType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                login_number.setText(getCacheAccount());
                login_password.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setSpinnerPopupStyle();
        }
        initVerificationView(v);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setSpinnerPopupStyle() {
        mSpinnerIdType.setPopupBackgroundResource(R.drawable.bg_spinner_popup);
        mSpinnerIdType.setDropDownVerticalOffset(10);
    }

    private void changePage(int IndicatorID) {
        int position = 0;
        switch (IndicatorID) {
            case R.id.account_indicator_capitals:
                mIndicatorCapitals.setBackgroundResource(R.drawable.account_tab1_selected);
                mIndicatorProfits.setBackgroundResource(R.drawable.account_tab2);
                mIndicatorTrades.setBackgroundResource(R.drawable.account_tab3);
                position = 0;
                break;
            case R.id.account_indicator_profits:
                mIndicatorCapitals.setBackgroundResource(R.drawable.account_tab1);
                mIndicatorProfits.setBackgroundResource(R.drawable.account_tab2_selected);
                mIndicatorTrades.setBackgroundResource(R.drawable.account_tab3);
                position = 1;
                break;
            case R.id.account_indicator_trades:
                mIndicatorCapitals.setBackgroundResource(R.drawable.account_tab1);
                mIndicatorProfits.setBackgroundResource(R.drawable.account_tab2);
                mIndicatorTrades.setBackgroundResource(R.drawable.account_tab3_selected);
                position = 2;
                break;
            default:
                break;
        }
        mQueryResultPanel.setCurrentItem(position);

    }

    protected void queryProfits(String session_id, int buttonID) {
        mProfitsRequestID = buttonID;
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_acco_profit_new);
        Date now = new Date();
        Date dateBegin;
        long milliseconds = 0;
        request.putParam("identity_type", "0")
                .putParam("fund_code", "")
                .putParam("session_id", session_id)
                .putParam("date_end", ConfigUtil.getDate(now));
        switch (buttonID) {
            case R.id.day:
                request.setRequestID(R.id.day);
                dateBegin = new Date(now.getYear(), now.getMonth(), now.getDate());
                milliseconds = now.getTime() - dateBegin.getTime();
                break;
            case R.id.month:
                request.setRequestID(R.id.month);
                milliseconds = 24L * 3600 * 1000 * 30;
                break;
            case R.id.year:
                request.setRequestID(R.id.year);
                milliseconds = 24L * 3600 * 1000 * 365;
                break;
            case R.id.thisyear:
                request.setRequestID(R.id.thisyear);
                dateBegin = new Date(now.getYear(), 0, 1);
                milliseconds = now.getTime() - dateBegin.getTime();
                break;
        }
        Date date = new Date(now.getTime() - milliseconds);
        request.putParam("date_begin", ConfigUtil.getDate(date));
        new HttpRequestAsyncTask(request, mHttpTaskListener, context).execute();
    }

    protected void queryCapitals(String session_id) {
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_acco_hold_new);
        request
                .putParam("session_id", session_id);
        new HttpRequestAsyncTask(request, mHttpTaskListener, context).execute();
    }

    protected void queryTrades(String session_id, int data_start, int data_num, boolean refresh) {
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_acco_trade_new);
        request
                .putParam("session_id", session_id)
                .putParam("date_begin", "")
                .putParam("date_end", "")
                .putParam("data_start", String.valueOf(data_start))
                .putParam("data_num", String.valueOf(data_num))
                .putParam("fund_code", "");
        if (refresh) {
            request.setRequestID(1);
        }
        new HttpRequestAsyncTask(request, mHttpTaskListener, context).execute();
    }

    protected void switchView() {

        ViewGroup root = (ViewGroup) v;
        root.removeAllViews();
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) context.getSystemService(infService);

        if (mIsLogined) {
            li.inflate(R.layout.c_a_account, root, true);
            mTitle = (TextView) v.findViewById(R.id.title_txt);
            mTitle.setText(MainActivity.MyAccount.getName() + "的账户");
            mLeftBtn = (TextView) v.findViewById(R.id.left_btn);
            mLeftBtn.setVisibility(View.INVISIBLE);
            mLeftBtn.setOnClickListener(mOnClick);
            mRightBtn = (TextView) v.findViewById(R.id.right_btn);
            mRightBtn.setVisibility(View.VISIBLE);
            mRightBtn.setText(R.string.login_sign_out);
            mRightBtn.setOnClickListener(mOnClick);
            initViewPager();
        } else {
            li.inflate(R.layout.c_a_member, root, true);
            initBaseView(root);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void login() {
        showProgressDialog(context, context.getString(R.string.dlg_login));
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_acco_login_new);
        String identity_type = "0";
        switch (mSpinnerIdType.getSelectedItemPosition()) {
            case 0:
                identity_type = "0";
                break;
            case 1:
                identity_type = "7";
                break;
            case 2:
                identity_type = "1";
                break;
            case 3:
                identity_type = "2";
                break;
            case 4:
                identity_type = "3";
                break;
            case 5:
                identity_type = "5";
                break;
            case 6:
                identity_type = "8";
                break;
            case 7:
                identity_type = "9";
                break;
            default:
                break;
        }
        request
                .putParam("identity_type", identity_type)
                .putParam("identity_num", login_number.getText().toString())
                .putParam("password", login_password.getText().toString());
        new HttpRequestAsyncTask(request, mHttpTaskListener, context).execute();
    }

    private void logout() {
        showProgressDialog(context, context.getString(R.string.dlg_logout));
        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_base_logout);
        request.putParam("session_id", "" + MainActivity.MyAccount.getSession_id());
        new HttpRequestAsyncTask(request, mHttpTaskListener, context).execute();
    }

    private void clearAccountData() {
        MainActivity.MyAccount = new AccountInfo();
        mListCapital.clear();
        mListProfit.clear();
        mListTrade.clear();
        mAdapterProfits.notifyDataSetChanged();
        mAdapterCapitals.notifyDataSetChanged();
        mAdapterTrades.notifyDataSetChanged();
        mCapitals.setText("");
        mWalletBalance.setText("");
        mYearProfit.setText("");
        mTotalDate.setText("");
        mTotalProfits.setText("");
        mTotalProfitsRate.setText("");
        mTotalProfitsTitle.setText("");
    }


    private void initViewPager() {
        mIndicatorCapitals = v.findViewById(R.id.account_indicator_capitals);
        mIndicatorProfits = v.findViewById(R.id.account_indicator_profits);
        mIndicatorTrades = v.findViewById(R.id.account_indicator_trades);
        mIndicatorCapitals.setOnClickListener(mOnClick);
        mIndicatorProfits.setOnClickListener(mOnClick);
        mIndicatorTrades.setOnClickListener(mOnClick);

        if (mQueryResultPanel != null) {
            mQueryResultPanel.removeAllViews();
        }
        mQueryResultPanel = (ViewPager) v.findViewById(R.id.account_query_result_panel);
        mQueryResultPanel.setOnPageChangeListener(mPageChageListener);
        if (mListViews == null) {
            mListViews = new ArrayList<View>();
            LayoutInflater lf = LayoutInflater.from(context);
            View vCapitals = lf.inflate(R.layout.c_a_account_capitals, null);
            View vProfits = lf.inflate(R.layout.c_a_account_profiles, null);
            View vTrades = lf.inflate(R.layout.c_a_account_trades, null);

            initSortView(vCapitals, vProfits, vTrades);

            mListViews.add(vCapitals);
            mListViews.add(vProfits);
            mListViews.add(vTrades);
            mCapitalsRefreshTime = (TextView) vCapitals.findViewById(R.id.refresh_time);
            mProfitsRefreshTime = (TextView) vProfits.findViewById(R.id.refresh_time);
            mTradessRefreshTime = (TextView) vTrades.findViewById(R.id.refresh_time);
            mAdapterProfits = new ExtensibleListViewAdapter(mListProfit, ExtensibleListViewAdapter.AdapterType.Type_Profit);
            mAdapterCapitals = new ExtensibleListViewAdapter(mListCapital, ExtensibleListViewAdapter.AdapterType.Type_Capital);
            mAdapterTrades = new ExtensibleListViewAdapter(mListTrade, ExtensibleListViewAdapter.AdapterType.Type_Trade);

            mListFloatTitle = vCapitals.findViewById(R.id.list_float_title);
            mListViewCapitals = (XListViewForTrades) vCapitals
                    .findViewById(R.id.accout_profits_listview);
            mListViewCapitals.setXListViewListener(mListViewCapitalslistener);
            initListViewCapitals();
            mListViewCapitals.setAdapter(mAdapterCapitals);
            mListViewCapitals.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    mAdapterCapitals.setUnfoldPosition(position - 5);
                }
            });
            mListViewCapitals.setPullRefreshEnable(true);
            mListViewCapitals.setPullLoadEnable(false);

            mListViewProfits = (XListViewForTrades) vProfits
                    .findViewById(R.id.accout_profits_listview);
            mListViewProfits.setXListViewListener(mListViewProfitslistener);
            mProfitsListFooter = vProfits.findViewById(R.id.account_profits_footer);
            mTotalDate = (TextView) mProfitsListFooter.findViewById(R.id.account_total_date);
            mTotalProfits = (TextView) mProfitsListFooter.findViewById(R.id.account_total_profits);
            mTotalProfitsRate = (TextView) mProfitsListFooter.findViewById(R.id.account_total_profits_rate);
            mTotalProfitsTitle = (TextView) mProfitsListFooter.findViewById(R.id.account_total_title);
            mListViewProfits.setAdapter(mAdapterProfits);
            mListViewProfits.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    mAdapterProfits.setUnfoldPosition(position - 2);
                }
            });
            mListViewProfits.setPullRefreshEnable(true);
            mListViewProfits.setPullLoadEnable(false);

            mListViewTrades = (XListViewForTrades) vTrades
                    .findViewById(R.id.accout_profits_listview);
            mListViewTrades.setXListViewListener(mListViewTradeslistener);
            mListViewTrades.setAdapter(mAdapterTrades);
            mListViewTrades.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    mAdapterTrades.setUnfoldPosition(position - 2);
                }
            });
            mListViewTrades.setPullRefreshEnable(true);
            mListViewTrades.setPullLoadEnable(false);
        }
        mQueryResultPanel.setAdapter(mPagerAdapter);
        mQueryResultPanel.setCurrentItem(1);
        mQueryResultPanel.setCurrentItem(0);
    }

    private void popupMenu() {
        int width = (int) (mIndicatorProfits.getWidth() * 1.3);
        if (menuWindow == null) {
            LayoutInflater lf = LayoutInflater.from(context);
            View layout = lf.inflate(R.layout.c_a_account_profiles_popupmenu, null);
            menuWindow = new PopupWindow(layout, width, LayoutParams.WRAP_CONTENT);
            menuWindow.setFocusable(true);
            menuWindow.setOutsideTouchable(true);
            menuWindow.update();
            menuWindow.setBackgroundDrawable(new BitmapDrawable());
            mDay = (TextView) layout.findViewById(R.id.day);
            mMonth = (TextView) layout.findViewById(R.id.month);
            mYear = (TextView) layout.findViewById(R.id.year);
            mThisYear = (TextView) layout.findViewById(R.id.thisyear);
            mDay.setOnClickListener(mOnClick);
            mMonth.setOnClickListener(mOnClick);
            mYear.setOnClickListener(mOnClick);
            mThisYear.setOnClickListener(mOnClick);
        }
        menuWindow.showAsDropDown(mIndicatorProfits, (mIndicatorProfits.getWidth() - width) / 2, 0);
    }

    private void initListViewCapitals() {
        if (mListViewCapitals != null) {
            View header1 = LayoutInflater.from(context).inflate(R.layout.c_a_account_capitals_header1, mListViewCapitals, false);
            View header2 = LayoutInflater.from(context).inflate(R.layout.c_a_account_capitals_header2, null);
            mListViewCapitals.addHeaderView(header1);
            mListViewCapitals.addHeaderView(header2);
            mListViewCapitals.resetNoDataHeaderView();
            mCapitals = (TextView) header1.findViewById(R.id.account_capitals);
            mWalletBalance = (TextView) header1.findViewById(R.id.account_wallet_balance);
            mYearProfit = (TextView) header1.findViewById(R.id.account_year_profit);
            sort_c_fund_share[1] = (TextView) header2.findViewById(R.id.sort_c_fund_share2);
            sort_c_fund_share_container[1] = header2.findViewById(R.id.sort_c_fund_share_container2);
            sort_c_fund_share_container[1].setOnClickListener(mOnClick);
            sort_c_fund_capital[1] = (TextView) header2.findViewById(R.id.sort_c_fund_capital2);
            sort_c_fund_capital_container[1] = header2.findViewById(R.id.sort_c_fund_capital_container2);
            sort_c_fund_capital_container[1].setOnClickListener(mOnClick);
            mListViewCapitals.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem > 2) {
                        mListFloatTitle.setVisibility(View.VISIBLE);
                    } else {
                        mListFloatTitle.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void initVerificationView(View parent) {
        verification_layout = parent.findViewById(R.id.login_verification_layout);
        verification_input = (TextView) parent.findViewById(R.id.login_verification_input);
        verification_code = (TextView) parent.findViewById(R.id.login_verification);
        verification_change = parent.findViewById(R.id.login_change_verification);
        verification_change.setOnClickListener(mOnClick);
    }

    private void changeVerification() {
        String verification = "";
        Random ran = new Random(System.currentTimeMillis());
        verification += String.valueOf(ran.nextInt(10));
        verification += String.valueOf(ran.nextInt(10));
        verification += String.valueOf(ran.nextInt(10));
        verification += String.valueOf(ran.nextInt(10));
        verification_code.setText(verification);
    }

    private void initSortView(View pc, View pp, View pt) {
        sort_c_fund_share[0] = (TextView) pc.findViewById(R.id.sort_c_fund_share);
        sort_c_fund_share_container[0] = pc.findViewById(R.id.sort_c_fund_share_container);
        sort_c_fund_share_container[0].setOnClickListener(mOnClick);

        sort_c_fund_capital[0] = (TextView) pc.findViewById(R.id.sort_c_fund_capital);
        sort_c_fund_capital_container[0] = pc.findViewById(R.id.sort_c_fund_capital_container);
        sort_c_fund_capital_container[0].setOnClickListener(mOnClick);

        sort_p_profit = (TextView) pp.findViewById(R.id.sort_p_profit);
        sort_p_profit_container = pp.findViewById(R.id.sort_p_profit_container);
        sort_p_profit_container.setOnClickListener(mOnClick);
        sort_p_profit_rate = (TextView) pp.findViewById(R.id.sort_p_profit_rate);
        sort_p_profit_rate_container = pp.findViewById(R.id.sort_p_profit_rate_container);
        sort_p_profit_rate_container.setOnClickListener(mOnClick);

        sort_t_confirm_amount = (TextView) pt.findViewById(R.id.sort_t_confirm_amount);
    }

    public void yesBtnWork() {
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
        clearAccountData();
        mIsLogined = false;
        context.sendBroadcast(new Intent("com.hctforgf.gff.signin"));
        switchView();
    }

    @Override
    protected void finalize() throws Throwable {
        context.unregisterReceiver(broadcastReceiver);
        super.finalize();
    }
}
