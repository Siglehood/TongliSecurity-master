package com.sig.tonglisecurity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.AccountInfo;
import com.sig.tonglisecurity.bean.Trade;
import com.sig.tonglisecurity.http.HttpRequestInfo;
import com.sig.tonglisecurity.http.Urls;
import com.sig.tonglisecurity.task.HttpRequestAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.widget.XListViewForTrades;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 钱袋子查询界面
 */
public class WalletQueryActivity extends ParentActivity implements OnClickListener, HttpRequestAsyncTask.TaskListener {

    private static final int REFRESH_ID = 1;
    private static final int DATA_NUM = 10;
    private ArrayList<View> mListViews = new ArrayList<View>();
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
    private TextView title;
    private TextView left_btn;
    private TextView wallet_query_recharge;
    private TextView wallet_query_all;
    private TextView wallet_query_redeem;
    private XListViewForTrades wallet_query_list_all, wallet_query_list_recharge, wallet_query_list_redeem;
    private ArrayList<Trade> mListData = new ArrayList<Trade>();
    private ArrayList<Trade> mListDataAll = new ArrayList<Trade>();
    private ArrayList<Trade> mListDataRecharge = new ArrayList<Trade>();
    private ArrayList<Trade> mListDataRedeem = new ArrayList<Trade>();
    private MyListAdapter mListAdapter = new MyListAdapter(mListData);
    private MyListAdapter mListAdapterAll = new MyListAdapter(mListDataAll);
    private MyListAdapter mListAdapterRecharge = new MyListAdapter(mListDataRecharge);
    private MyListAdapter mListAdapterRedeem = new MyListAdapter(mListDataRedeem);
    private ViewPager mViewPager;
    private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    wallet_query_all.setBackgroundResource(R.drawable.account_tab1_selected);
                    wallet_query_recharge.setBackgroundResource(R.drawable.account_tab2);
                    wallet_query_redeem.setBackgroundResource(R.drawable.account_tab3);
                    break;
                case 1:
                    wallet_query_all.setBackgroundResource(R.drawable.account_tab1);
                    wallet_query_recharge.setBackgroundResource(R.drawable.account_tab2_selected);
                    wallet_query_redeem.setBackgroundResource(R.drawable.account_tab3);
                    break;
                case 2:
                    wallet_query_all.setBackgroundResource(R.drawable.account_tab1);
                    wallet_query_recharge.setBackgroundResource(R.drawable.account_tab2);
                    wallet_query_redeem.setBackgroundResource(R.drawable.account_tab3_selected);
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
    private boolean isDlgVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_wallet_query2);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.wallet_query_title));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        wallet_query_all = (TextView) findViewById(R.id.wallet_query_all);
        wallet_query_recharge = (TextView) findViewById(R.id.wallet_query_recharge);
        wallet_query_redeem = (TextView) findViewById(R.id.wallet_query_redeem);
        wallet_query_all.setOnClickListener(this);
        wallet_query_redeem.setOnClickListener(this);
        wallet_query_recharge.setOnClickListener(this);
        initViewPager();
        onClick(wallet_query_all);
        showProgressDialog(this, getString(R.string.dlg_loading));
        refresh("", 0, true);
        refresh("022", 0, true);
        refresh("024", 0, true);
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.wallet_query_viewpager);
        View vAll = LayoutInflater.from(this).inflate(R.layout.c_a_wallet_query_viewpager_item, null);
        View vRecharge = LayoutInflater.from(this).inflate(R.layout.c_a_wallet_query_viewpager_item, null);
        View vRedeem = LayoutInflater.from(this).inflate(R.layout.c_a_wallet_query_viewpager_item, null);
        ((TextView) (vAll.findViewById(R.id.wallet_query_request_title)))
                .setText(getString(R.string.wallet_query_request_amountorshare));
        ((TextView) (vRecharge.findViewById(R.id.wallet_query_request_title)))
                .setText(getString(R.string.wallet_query_request_amount));
        ((TextView) (vRedeem.findViewById(R.id.wallet_query_request_title)))
                .setText(getString(R.string.wallet_query_request_share));
        mListViews.add(vAll);
        mListViews.add(vRecharge);
        mListViews.add(vRedeem);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        wallet_query_list_all = (XListViewForTrades) vAll.findViewById(R.id.wallet_query_list);
        wallet_query_list_recharge = (XListViewForTrades) vRecharge.findViewById(R.id.wallet_query_list);
        wallet_query_list_redeem = (XListViewForTrades) vRedeem.findViewById(R.id.wallet_query_list);
        wallet_query_list_all.setAdapter(mListAdapterAll);
        wallet_query_list_recharge.setAdapter(mListAdapterRecharge);
        wallet_query_list_redeem.setAdapter(mListAdapterRedeem);
        wallet_query_list_all.setPullRefreshEnable(true);
        wallet_query_list_recharge.setPullRefreshEnable(true);
        wallet_query_list_redeem.setPullRefreshEnable(true);
        wallet_query_list_all.setPullLoadEnable(false);
        wallet_query_list_recharge.setPullLoadEnable(false);
        wallet_query_list_redeem.setPullLoadEnable(false);
        wallet_query_list_all.setXListViewListener(new XListViewForTrades.IXListViewListener() {

            @Override
            public void onRefresh() {
                refresh("", 0, true);
            }

            @Override
            public void onLoadMore() {
                refresh("", mListDataAll.size(), false);
            }
        });

        wallet_query_list_recharge.setXListViewListener(new XListViewForTrades.IXListViewListener() {

            @Override
            public void onRefresh() {
                refresh("022", 0, true);
            }

            @Override
            public void onLoadMore() {
                refresh("022", mListDataRecharge.size(), false);
            }
        });

        wallet_query_list_redeem.setXListViewListener(new XListViewForTrades.IXListViewListener() {

            @Override
            public void onRefresh() {
                refresh("024", 0, true);
            }

            @Override
            public void onLoadMore() {
                refresh("024", mListDataRedeem.size(), false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wallet_query_all:
                wallet_query_all.setBackgroundResource(R.drawable.account_tab1_selected);
                wallet_query_recharge.setBackgroundResource(R.drawable.account_tab2);
                wallet_query_redeem.setBackgroundResource(R.drawable.account_tab3);
                mListData = mListDataAll;
                mViewPager.setCurrentItem(0, true);
                break;

            case R.id.wallet_query_recharge:
                wallet_query_all.setBackgroundResource(R.drawable.account_tab1);
                wallet_query_recharge.setBackgroundResource(R.drawable.account_tab2_selected);
                wallet_query_redeem.setBackgroundResource(R.drawable.account_tab3);
                mListData = mListDataRecharge;
                mViewPager.setCurrentItem(1, true);
                break;

            case R.id.wallet_query_redeem:
                wallet_query_all.setBackgroundResource(R.drawable.account_tab1);
                wallet_query_recharge.setBackgroundResource(R.drawable.account_tab2);
                wallet_query_redeem.setBackgroundResource(R.drawable.account_tab3_selected);
                mListData = mListDataRedeem;
                mViewPager.setCurrentItem(2, true);
                break;
            case R.id.left_btn:
                finish();
                break;
            default:
                break;
        }
    }

    private void refresh(String type, int data_start, boolean isRefresh) {

        HttpRequestInfo request = new HttpRequestInfo(Urls.URL_wallet_trade);
        request
                .putParam("session_id", MainActivity.MyAccount.getSession_id())
                .putParam("trade_type", type)
                .putParam("date_begin", "")
                .putParam("date_end", "")
                .putParam("data_start", String.valueOf(data_start))
                .putParam("data_num", String.valueOf(DATA_NUM));
        if (isRefresh) {
            request.setRequestID(REFRESH_ID);
        }
        new HttpRequestAsyncTask(request, this, context).execute();
    }

    @Override
    public void onTaskOver(HttpRequestInfo request, String response) {
        dismissProgressDialog();
        XListViewForTrades xlist = null;
        try {
            ArrayList<Trade> list = null;
            String str = request.getRequestParams().get("trade_type");
            if (str.equals("")) {
                list = mListDataAll;
                mListAdapter = mListAdapterAll;
                xlist = wallet_query_list_all;
                wallet_query_list_all.setRefreshTime(ConfigUtil.getRefreshTime(ConfigUtil.getNowTime()));
            } else if (str.equals("022")) {
                list = mListDataRecharge;
                mListAdapter = mListAdapterRecharge;
                xlist = wallet_query_list_recharge;
                wallet_query_list_recharge.setRefreshTime(ConfigUtil.getRefreshTime(ConfigUtil.getNowTime()));
            } else if (str.equals("024")) {
                list = mListDataRedeem;
                mListAdapter = mListAdapterRedeem;
                xlist = wallet_query_list_redeem;
                wallet_query_list_redeem.setRefreshTime(ConfigUtil.getRefreshTime(ConfigUtil.getNowTime()));
            }
            if (request.getRequestID() == REFRESH_ID) {
                if (list != null) {
                    list.clear();
                }
            }
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("trades");
            if (jsonArray.length() > 0 && jsonArray.getJSONObject(0).has("isLoginOut") && jsonArray.getJSONObject(0).getString("isLoginOut").equals("Y")) {
                showSingleAlertDlg(context.getString(R.string.dlg_session_invalid));
                return;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jtrade = jsonArray.getJSONObject(i);
                Trade trade = new Trade();
                trade.setTime(jtrade.getString("time"));
                trade.setTrade_type(jtrade.getString("trade_type"));
                trade.setConfirm_amount(jtrade.getString("confirm_amount"));
                trade.setConfirm_share(jtrade.getString("confirm_share"));
                trade.setRequest_amount(jtrade.getString("request_amount"));
                trade.setRequest_share(jtrade.getString("request_share"));
                trade.setState(jtrade.getString("state"));
                trade.setChannel(jtrade.getString("channel"));
                trade.setFee_type(jtrade.getString("fee_type"));
                trade.setFee(jtrade.getString("fee"));
                list.add(trade);
            }
            if (jsonArray.length() < DATA_NUM) {
                xlist.setPullLoadEnable(false);
            } else {
                xlist.setPullLoadEnable(true);
            }
            mListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (xlist != null) {
            xlist.stopRefresh();
        }
    }

    /**
     * @param msg 显示的信息
     */
    public void showSingleAlertDlg(String msg) {
        if (isDlgVisible) {
            return;
        } else {
            isDlgVisible = true;
        }
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.dlg_i_konw),
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
        isDlgVisible = false;
    }

    private class MyListAdapter extends BaseAdapter {
        private ArrayList<Trade> mListData;

        public MyListAdapter(ArrayList<Trade> data) {
            mListData = data;
        }

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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_wallet_query_list, null);
                ViewHolder holder = new ViewHolder(convertView);
                holder.setData(mListData.get(position));
                convertView.setTag(holder);
            } else {
                ((ViewHolder) convertView.getTag()).setData(mListData.get(position));
            }
            return convertView;
        }
    }

    private class ViewHolder {
        private Trade mData;
        private View mRootView;
        private TextView trade_type;
        private TextView time;
        private TextView confirm_amount;
        private TextView state;

        public ViewHolder(View convertView) {
            mRootView = convertView;
            initViews();
        }

        private void initViews() {
            trade_type = (TextView) mRootView.findViewById(R.id.trade_type);
            time = (TextView) mRootView.findViewById(R.id.time);
            confirm_amount = (TextView) mRootView.findViewById(R.id.confirm_amount);
            state = (TextView) mRootView.findViewById(R.id.state);
        }

        public void setData(Trade trade) {
            mData = trade;
            if (mData != null) {
                trade_type.setText(mData.getTrade_type());
                time.setText(mData.getTime());
                if (mData.getTrade_type().contains("�깺")) {
                    confirm_amount.setText(ConfigUtil.getFormatAmount(mData.getRequest_amount()));
                } else if (mData.getTrade_type().contains("���")) {
                    confirm_amount.setText(ConfigUtil.getFormatAmount(mData.getRequest_share()));
                } else {
                    confirm_amount.setText(ConfigUtil.getFormatAmount(mData.getRequest_amount()));
                }

                state.setText(mData.getState());
                if (state.getText().equals("ʧ��")) {
                    state.setTextColor(getResources().getColor(R.color.red));
                } else {
                    state.setTextColor(getResources().getColor(R.color.black));
                }
            }
        }
    }
}
