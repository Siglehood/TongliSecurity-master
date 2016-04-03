package com.sig.tonglisecurity.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.adapter.FundDetailAdapter;
import com.sig.tonglisecurity.app.Config;
import com.sig.tonglisecurity.bean.FundBean;
import com.sig.tonglisecurity.bean.FundDetailBean;
import com.sig.tonglisecurity.bean.FundInfoDetailResultBean;
import com.sig.tonglisecurity.database.DatabaseAdapter;
import com.sig.tonglisecurity.interfaces.FundInfoDetailListener;
import com.sig.tonglisecurity.task.MyAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.widget.XListView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class FundDetailActivity
        extends ParentActivity
        implements XListView.IXListViewListener {

    public static final String TAG = "FundDetailActivity";
    List x = new ArrayList();
    List y = new ArrayList();
    boolean hasRecord = false;
    int id = 0;
    GraphicalView chart;
    private TextView left_btn, right_btn;
    private TextView title;
    private int position;
    private FundBean fund;
    private FundDetailBean bean = new FundDetailBean();
    private DatabaseAdapter mDatabaseAdapter;
    private List<FundBean> fav_data = new ArrayList<FundBean>();
    private int fav_id = -1;
    private boolean need_insert_fav = false;
    private XListView listview;
    private FundDetailAdapter adpater;
    private LinearLayout parent;
    FundInfoDetailListener mFundInfoDetailListener = new FundInfoDetailListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void mFundInfoDetail(
                FundInfoDetailResultBean mFundInfoDetailResultBean) {
            dismissCustomProgressDialog();
            if (mFundInfoDetailResultBean != null && mFundInfoDetailResultBean.mFundDetailBean != null) {
                bean = mFundInfoDetailResultBean.mFundDetailBean;

                adpater = new FundDetailAdapter(bean, context, mOnClick);
                adpater.isLoad = true;
                listview.setAdapter(adpater);

                bean.fund_code = fund.fund_code;
                bean.type = fund.type;
                bean.rate_thisyear = fund.rate_thisyear;
                bean.rate_nearyear = fund.rate_nearyear;

                x.clear();
                y.clear();
                int days_count = bean.mNetvalueFivedaysBean.days.size();
                double[] days = new double[days_count];
                for (int i = 0; i < days_count; i++) {
                    days[i] = i + 1;
                }
                x.add(days);
                double[] netvalues = new double[days_count];
                for (int i = 0; i < days_count; i++) {
                    netvalues[i] = Double.parseDouble(bean.mNetvalueFivedaysBean.netvalues.get(days_count - 1 - i));
                }
                y.add(netvalues);
                initXYLine();
                updateFundDetailDB();
            } else {
                Toast.makeText(context, R.string.dlg_get_data_err, Toast.LENGTH_SHORT).show();
            }
            last_refresh_date = ConfigUtil.getNowTime();
            listview.setRefreshTime(ConfigUtil.getRefreshTime(last_refresh_date));
            onLoad();
        }
    };
    private LinearLayout container;
    private List<FundDetailBean> fund_detail_beans = new ArrayList<FundDetailBean>();
    private String five_bean = "";
    private boolean hasShow = false;
    OnClickListener mOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.left_btn) {
                updateFavData();
                finish();
            } else if (id == R.id.right_btn) {
                if (fund.is_fav.equals("0")) {
                    fund.is_fav = "1";
                    right_btn.setText("取消收藏");
                    right_btn.setBackgroundResource(R.drawable.common_btn_l_drawable);
                    showToast(context.getString(R.string.dlg_fav_success));
                } else {
                    fund.is_fav = "0";
                    right_btn.setText("收藏");
                    right_btn.setBackgroundResource(R.drawable.common_btn_drawable);
                    showToast(context.getString(R.string.dlg_option_success));
                }
                Intent intent = getIntent();
                intent.putExtra(Config.LIST_POSITION, position);
                intent.putExtra(Config.FAV_FLAG, fund.is_fav);
                setResult(RESULT_OK, getIntent());
            } else if (id == R.id.chart_parent) {
                if (!hasShow) {
                    showToast(context.getString(R.string.dlg_check_big_chart));
                    hasShow = true;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "FundDetailActivity--> onCreate");

        Intent i = getIntent();
        if (i != null) {
            position = i.getIntExtra(Config.LIST_POSITION, 0);
            fund = i.getParcelableExtra("fund");
        }
        setContentView(R.layout.a_fund_detail);
        parent = (LinearLayout) findViewById(R.id.parent);
        container = (LinearLayout) findViewById(R.id.container);
        mDatabaseAdapter = new DatabaseAdapter(context, Config.database_version);
        left_btn = (TextView) findViewById(R.id.left_btn);
        right_btn = (TextView) findViewById(R.id.right_btn);
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(fund.fund_name);
        left_btn.setVisibility(View.VISIBLE);
        right_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(mOnClick);
        right_btn.setOnClickListener(mOnClick);
        right_btn.setText("收藏");
        right_btn.setBackgroundResource(R.drawable.common_btn_drawable);
        if (fund.is_fav.equals("1")) {
            need_insert_fav = false;
            right_btn.setText("取消收藏");
            right_btn.setBackgroundResource(R.drawable.common_btn_l_drawable);
        } else {
            need_insert_fav = true;
        }

        listview = (XListView) findViewById(R.id.xListView);
        listview.setPullLoadEnable(true);
        listview.setXListViewListener(this);
        getAllFundDetailFromDB();
        for (FundDetailBean beanDetail : fund_detail_beans) {
            if (beanDetail.fund_code.equals(fund.fund_code)) {
                id = beanDetail.f_id;
                hasRecord = true;
                bean = beanDetail;
                break;
            }
        }

        if (hasRecord) {
            x.clear();
            y.clear();
            int days_count = bean.mNetvalueFivedaysBean.days.size();
            double[] days = new double[days_count];
            for (int j = 0; j < days_count; j++) {
                days[j] = j + 1;
            }
            x.add(days);
            double[] netvalues = new double[days_count];
            for (int j = 0; j < days_count; j++) {
                netvalues[j] = Double.parseDouble(bean.mNetvalueFivedaysBean.netvalues.get(days_count - 1 - j));
            }
            y.add(netvalues);
            initXYLine();
        } else {
            bean.fund_code = fund.fund_code;
            bean.type = fund.type;
            bean.netvalue = fund.netvalue;
            bean.rate_thisyear = fund.rate_thisyear;
            bean.rate_nearyear = fund.rate_nearyear;
        }
        adpater = new FundDetailAdapter(bean, context, mOnClick);
        if (hasRecord) {
            adpater.isLoad = true;
        } else {
            adpater.isLoad = false;
        }
        listview.setAdapter(adpater);
        listview.setRefreshTime(ConfigUtil.getRefreshTime(last_refresh_date));
        showCustomProgressDialog(context, context.getString(R.string.dlg_loading));
        getFavData();
        for (FundBean f : fav_data) {
            if (f.fund_code.equals(fund.fund_code)) {
                fav_id = f.f_id;
                break;
            }
        }
        loadFundDetailData();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            parent.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        }
    }

    private void loadFundDetailData() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("fund_code", fund.fund_code));
        new MyAsyncTask(mFundInfoDetailListener, Config.fund_info_detail_code, params, context).execute();
    }

    /**
     * 更新基金信息
     */
    private void updateFundDetailDB() {
        bean.fund_code = fund.fund_code;
        mDatabaseAdapter.open_fund();
        if (hasRecord) {
            mDatabaseAdapter.updateFundDetail(bean, id);
        } else {
            mDatabaseAdapter.insertFundDetailData(bean);
        }
        mDatabaseAdapter.close_funds();
    }

    /**
     * 从数据库中获取所有的基金信息
     *
     * @return 基金信息集合
     */
    private List<FundDetailBean> getAllFundDetailFromDB() {
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllFundDetailData();
        if (c != null) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    FundDetailBean entry = new FundDetailBean();
                    entry.f_id = c.getInt(c.getColumnIndex("_id"));
                    entry.fund_code = c.getString(c.getColumnIndex("fund_code"));
                    entry.fund_name = c.getString(c.getColumnIndex("fund_name"));
                    entry.level = c.getString(c.getColumnIndex("level"));

                    entry.netvalue = c.getString(c.getColumnIndex("netvalue"));
                    entry.type = c.getString(c.getColumnIndex("type"));
                    entry.rate_thisyear = c.getString(c.getColumnIndex("rate_thisyear"));
                    entry.rate_nearyear = c.getString(c.getColumnIndex("rate_nearyear"));
                    entry.found_date = c.getString(c.getColumnIndex("found_date"));
                    entry.scale = c.getString(c.getColumnIndex("scale"));
                    entry.attention_num = c.getString(c.getColumnIndex("attention_num"));
                    entry.manager = c.getString(c.getColumnIndex("manager"));
                    entry.rank_thisyear = c.getString(c.getColumnIndex("rank_thisyear"));
                    entry.rank_nearyear = c.getString(c.getColumnIndex("rank_nearyear"));
                    five_bean = c.getString(c.getColumnIndex("five_bean"));
                    String[] five_array = five_bean.split("%");
                    if (five_array.length > 1) {
                        int count_colum = five_array.length / 2;
                        for (int i = 0; i < count_colum; i++) {
                            entry.mNetvalueFivedaysBean.days.add(five_array[i]);
                        }
                        for (int i = count_colum; i < five_array.length; i++) {
                            entry.mNetvalueFivedaysBean.netvalues.add(five_array[i]);
                        }
                    }
                    fund_detail_beans.add(entry);
                }
            }
        }
        mDatabaseAdapter.close_funds();
        return fund_detail_beans;
    }

    /**
     * 初始化图表
     */
    @SuppressLint("ResourceAsColor")
    private void initXYLine() {
        String[] titles = new String[]{"单位净值"};
        int values_count = ((double[]) x.get(0)).length;
        XYMultipleSeriesDataset dataset = buildDatset(titles, x, y);
        int[] colors = new int[]{context.getResources().getColor(
                R.color.k_color)};
        PointStyle[] styles = new PointStyle[]{PointStyle.POINT};
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);
        renderer.setMarginsColor(context.getResources().getColor(
                R.color.k_bg));
        if (values_count > 5) {
            for (int i = 0; i < values_count; i++) {
                int x_1 = 0;
                int x_2 = (values_count / 2) / 2;
                int x_3 = values_count / 2;
                int x_4 = (values_count + x_3) / 2;
                int x_5 = values_count - 1;
                if (i == x_1
                        || i == x_2
                        || i == x_3
                        || i == x_4
                        || i == x_5) {
                    renderer.addXTextLabel(i + 1, bean.mNetvalueFivedaysBean.days.get(values_count - i - 1));//����x���������ʾ
                } else {
                    renderer.addXTextLabel(i + 1, "");//����x���������ʾ
                }
            }
        } else {
            for (int i = 0; i < values_count; i++) {
                renderer.addXTextLabel(i + 1, bean.mNetvalueFivedaysBean.days.get(values_count - i - 1));//����x���������ʾ
            }
        }
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setShowGrid(true);
        renderer.setXLabels(0);
        renderer.setLabelsTextSize(18);
        renderer.setLegendTextSize(10);
        renderer.setLegendHeight(20);
        renderer.setMargins(new int[]{60, 60, 60, 60});
        renderer.setShowAxes(true);
        double[] score = ConfigUtil.bubbleSort((double[]) y.get(0));
        renderer.setInScroll(true);
        renderer.setZoomEnabled(false, false);
        renderer.setPanEnabled(false, false);

        setChartSettings(renderer, "", "", "", 1, values_count + 1, score[values_count - 1] - 0.01,
                score[0] + 0.01, Color.BLACK, Color.BLACK);
        chart = ChartFactory.getLineChartView(context, dataset, renderer);
        parent.removeAllViews();
        parent.addView(chart);
    }

    private XYMultipleSeriesDataset buildDatset(String[] titles, List xValues,
                                                List yValues) {
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

        int length = 1;
        for (int i = 0; i < length; i++) {
            XYSeries series = new XYSeries(titles[i]);
            double[] xV = (double[]) xValues.get(i);
            double[] yV = (double[]) yValues.get(i);
            int seriesLength = xV.length;

            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
            }
            mDataset.addSeries(series);
        }

        return mDataset;
    }

    private XYMultipleSeriesRenderer buildRenderer(int[] colors,
                                                   PointStyle[] styles,
                                                   boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = 1;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setFillPoints(fill);
            r.setLineWidth(2.5f);
            r.setShowLegendItem(true);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer,
                                    String title,
                                    String xTitle,
                                    String yTitle,
                                    double xMin,
                                    double xMax,
                                    double yMin,
                                    double yMax,
                                    int axesColor,
                                    int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setYLabelsPadding(5);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setXLabelsColor(labelsColor);
        renderer.setYLabelsColor(0, labelsColor);
        renderer.setXLabelsAlign(Align.LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 获取收藏基金数据
     */
    private void getFavData() {
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllFavFundData();
        if (c != null) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    FundBean entry = new FundBean();
                    entry.fund_code = c.getString(c.getColumnIndex("fund_code"));
                    entry.f_id = c.getInt(c.getColumnIndex("_id"));
                    fav_data.add(entry);
                }
            }
        }
        mDatabaseAdapter.close_funds();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            updateFavData();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateFavData() {
        mDatabaseAdapter.open_fund();
        if (fund.is_fav.equals("1") && need_insert_fav) {
            mDatabaseAdapter.insertFavFundData(//���µ��ղؼ�¼��ӵ��ղ���ݱ���
                    fund.fund_code,
                    fund.fund_name,
                    fund.type,
                    fund.netvalue,
                    fund.day_growth,
                    fund.netvalue_date,
                    fund.rate_sevenday,
                    fund.rate_thounds,
                    fund.rate_thounds_date,
                    fund.rate_threemonth,
                    fund.rate_thisyear,
                    fund.rate_nearyear,
                    fund.is_fav,
                    fund.date);
        }
        if (fund.is_fav.equals("0") && !need_insert_fav) {
            try {
                if (fav_id != -1) {
                    mDatabaseAdapter.deleteFavData(fav_id);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }
        }
        mDatabaseAdapter.close_funds();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        loadFundDetailData();
    }

    @Override
    public void onLoadMore() {
        onLoad();
    }

    private void onLoad() {
        listview.stopRefresh();
        listview.stopLoadMore();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            container.setVisibility(View.GONE);
            parent.setVisibility(View.VISIBLE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            container.setVisibility(View.VISIBLE);
            parent.setVisibility(View.GONE);
        }
    }
}
