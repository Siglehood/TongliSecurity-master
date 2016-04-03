package com.sig.tonglisecurity.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.activity.FundDetailActivity;
import com.sig.tonglisecurity.activity.FundFavActivity;
import com.sig.tonglisecurity.activity.HeadDetailActivity;
import com.sig.tonglisecurity.activity.MainActivity;
import com.sig.tonglisecurity.activity.NetErrActivity;
import com.sig.tonglisecurity.adapter.ImagePagerAdapter;
import com.sig.tonglisecurity.adapter.Text_Adapter;
import com.sig.tonglisecurity.adapter.TitledListAdapter;
import com.sig.tonglisecurity.app.Config;
import com.sig.tonglisecurity.app.GFFApp;
import com.sig.tonglisecurity.bean.FundBean;
import com.sig.tonglisecurity.bean.FundTitleBean;
import com.sig.tonglisecurity.bean.GetFundAllResultBean;
import com.sig.tonglisecurity.bean.GetTopicInfoResultBean;
import com.sig.tonglisecurity.database.DatabaseAdapter;
import com.sig.tonglisecurity.interfaces.SureActionListener;
import com.sig.tonglisecurity.task.BaseHandlerUI;
import com.sig.tonglisecurity.task.UIAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.DataUtil;
import com.sig.tonglisecurity.widget.FixedSpeedScroller;
import com.sig.tonglisecurity.widget.MyViewPager;
import com.sig.tonglisecurity.widget.PageControlView;
import com.sig.tonglisecurity.widget.TitledListView;
import com.sig.tonglisecurity.widget.XListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JijinView extends BaseView implements XListView.IXListViewListener {

    public PopupWindow popupWindow;
    GetTopicInfoResultBean mGetTopicInfoResultBean = new GetTopicInfoResultBean();
    List<FundBean> entrys = new ArrayList<FundBean>();
    int fund_type, sort;
    Handler bannerStartHandler = new Handler();
    List<FundTitleBean> data_left, data_right;
    FundTitleBean left_bean, right_bean;
    FundTitleBean left_bean_temp, right_bean_temp;
    SureActionListener mSureActionListener = new SureActionListener() {
        @Override
        public void sureAction(int type, FundTitleBean bean) {
            if (type == 1) {
                left_bean_temp = bean;
            } else {
                right_bean_temp = bean;
            }
        }
    };
    OnClickListener mOnClick_r = new OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.name) {
                int p = (Integer) view.getTag();
                for (FundTitleBean f : data_right) {
                    f.isSelected = false;
                }
                data_right.get(p).isSelected = true;
                adapter_right.notifyDataSetChanged();
                mSureActionListener.sureAction(2, data_right.get(p));
            }
        }

    };
    private TextView title;
    private TextView left_btn, right_btn, right_btn_sure;
    OnTouchListener onTouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                changeRightBtnStatus();
            }
            return false;
        }
    };
    private TitledListView mListView;
    /**
     * 选项点击监听器
     */
    OnItemClickListener itemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i,
                                long l) {
            int p = 2;
            if (i > 1) {
                p = i - 2;
            }
            if (parent == mListView) {
                if (!entrys.get(p).fund_code.equals("")) {
                    gotoFundDetail(p);
                }
            }
        }
    };
    private ImagePagerAdapter mImagePagerAdapter;
    private MyViewPager ad_pager;
    /**
     * 广告页 Handler
     */
    Handler adPagerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int position = (Integer) msg.obj + 1;
            ad_pager.setCurrentItem(position);
        }
    };
    private TextView ad_text;
    private PageControlView page_control;
    /**
     * viewPage 监听器
     */
    OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {

            if (mGetTopicInfoResultBean.list.size() > 0) {
                int p = position % mGetTopicInfoResultBean.list.size();
                page_control.generatePageControl(p);
                if (mGetTopicInfoResultBean.state.equals("0")) {
                    ad_text.setText(mGetTopicInfoResultBean.list.get(p).title);
                }
                adPagerHandler.removeMessages(0);
                adPagerHandler.sendMessageDelayed(
                        adPagerHandler.obtainMessage(0, position), Config.ad_title_speed);
            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private int displayWidth;
    private int currentPage = 0;
    Runnable bannerStartRun = new Runnable() {
        @Override
        public void run() {
            ad_pager.setCurrentItem(currentPage + 1);
        }
    };
    private RelativeLayout net_err_view;
    private List<Integer> imgs = new ArrayList<Integer>();
    private View headerView;
    private TitledListAdapter adapter;
    private DatabaseAdapter mDatabaseAdapter;
    private String last_refresh_date;
    private LinearLayout container;
    /**
     * 处理主题数据的句柄
     */
    private Handler getTopicDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseHandlerUI.TASK_NOTIFY_RETURN_DATA:
                    if (msg.obj != null) {
                        try {
                            mGetTopicInfoResultBean = (GetTopicInfoResultBean) msg.obj;
                            if (mGetTopicInfoResultBean.state.equals("0")) {
                                mImagePagerAdapter = new ImagePagerAdapter(context, mOnClick);
                                mImagePagerAdapter.list = mGetTopicInfoResultBean.list;
                                ad_pager.setAdapter(mImagePagerAdapter);
                                page_control.count = mGetTopicInfoResultBean.amount;
                                page_control.generatePageControl(currentPage + 1);
                                ad_pager.setCurrentItem(currentPage + 1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                    break;
            }
        }
    };
    private List<FundBean> fav_data = new ArrayList<FundBean>();
    private boolean isDefaultSort = false;
    private Text_Adapter adapter_left, adapter_right;
    OnClickListener mOnClick_l = new OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.name) {
                int p = (Integer) view.getTag();
                for (FundTitleBean f : data_left) {
                    f.isSelected = false;
                }
                data_left.get(p).isSelected = true;
                adapter_left.notifyDataSetChanged();
                mSureActionListener.sureAction(1, data_left.get(p));
            }
        }

    };
    private Handler mGetAllFundHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            dismissProgressDialog();
            switch (msg.what) {
                case BaseHandlerUI.TASK_NOTIFY_RETURN_DATA:
                    dismissProgressDialog();
                    initChoose();
                    if (msg.obj != null) {
                        GetFundAllResultBean mGetFundAllResultBean = (GetFundAllResultBean) msg.obj;
                        if (mGetFundAllResultBean != null && mGetFundAllResultBean.fund_list.size() > 0) {
                            changeRightBtnStatus();
                            initFundData(mGetFundAllResultBean.fund_list);
                        } else {
                            Toast.makeText(context, R.string.dlg_get_data_err, Toast.LENGTH_SHORT).show();
                        }
                        showNetNotice();
                        last_refresh_date = ConfigUtil.getNowTime();
                        mListView.setRefreshTime(ConfigUtil.getRefreshTime(last_refresh_date));
                        onLoad();
                        mController.execute(new UIAsyncTask(context, getTopicDataHandler, BaseHandlerUI.REQUEST_GET_TOPIC_DATA));
                    }
                    break;
            }
        }
    };
    /**
     *
     */
    private Handler getInitDataHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseHandlerUI.TASK_NOTIFY_RETURN_DATA:
                    dismissProgressDialog();
                    initChoose();
                    if (msg.obj != null) {
                        boolean hasCacheData = true;
                        try {
                            entrys = (List<FundBean>) msg.obj;
                            if (entrys.size() <= 0) {
                                hasCacheData = false;
                                for (int i = 0; i < 7; i++) {
                                    entrys.add(new FundBean());
                                }
                                adapter = new TitledListAdapter(context, entrys);
                                mListView.setAdapter(adapter);
                                mListView.setOnScrollListener(adapter);
                            } else {
                                hasCacheData = true;
                                selection();
                            }
                            if (DataUtil.getNavigate(context)) {
                                showProgressDialog(context, context.getString(R.string.dlg_loading));
                            }
                            loadFundData(hasCacheData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
    private boolean open_ever = false;
    OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == 1000) {

                int p = (Integer) view.getTag();
                if (mGetTopicInfoResultBean.list.size() > 0) {
                    Intent i = new Intent();
                    i.setClass(context, HeadDetailActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("topic_bean", mImagePagerAdapter.list.get(p));
                    context.startActivity(i);
                }
            } else if (id == R.id.left_btn) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    open_ever = false;
                    disPopListener();
                    changeRightBtnStatus();
                } else {
                    showPopListener();
                }
            } else if (id == R.id.right_btn) {

                if (!hasFav()) {
                    Toast tr = Toast.makeText(context, "没有收藏记录", Toast.LENGTH_LONG);
                    tr.setGravity(Gravity.CENTER, 0, 0);
                    tr.show();
                } else {
                    gotoFundFav();
                }
            } else if (id == R.id.right_btn_sure) {
                left_bean = left_bean_temp;
                right_bean = right_bean_temp;
                open_ever = false;
                disPopListener();
                changeRightBtnStatus();
                select_action();
            } else if (id == R.id.check_net_txt) {
                gotoNetErr();
            }
        }
    };

    public JijinView(FragmentActivity activity, View v) {
        super(activity, v);
    }

    @Override
    public void initBaseView(View v) {

        super.initBaseView(v);
        mDatabaseAdapter = new DatabaseAdapter(context, Config.database_version);
        displayWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        container = (LinearLayout) v.findViewById(R.id.container);
        net_err_view = (RelativeLayout) v.findViewById(R.id.net_err_view);

        TextView check_net_txt = (TextView) v.findViewById(R.id.check_net_txt);
        check_net_txt.setOnClickListener(mOnClick);

        title = (TextView) v.findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.tab_title_01));
        title.setText("全部基金");

        left_btn = (TextView) v.findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setText("筛选");
        left_btn.setBackgroundResource(R.drawable.common_btn_drawable);
        left_btn.setOnClickListener(mOnClick);
        right_btn = (TextView) v.findViewById(R.id.right_btn);
        right_btn.setOnClickListener(mOnClick);
        right_btn.setVisibility(View.VISIBLE);

        right_btn.setText("我的收藏");
        right_btn_sure = (TextView) v.findViewById(R.id.right_btn_sure);
        right_btn_sure.setOnClickListener(mOnClick);
        right_btn_sure.setOnTouchListener(onTouch);
        mListView = (TitledListView) v.findViewById(R.id.xListView);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(itemClick);
        mListView.setPullLoadEnable(true);
        initHeadView();
        mListView.addHeaderView(headerView);
        adapter = new TitledListAdapter(context, entrys);
        mListView.setAdapter(adapter);
        mListView.setOnScrollListener(adapter);

        mListView.setRefreshTime(ConfigUtil.getRefreshTime(last_refresh_date));
        mController.execute(new UIAsyncTask(getInitDataHandler, mDatabaseAdapter, BaseHandlerUI.REQUEST_GET_INIT_DATA));
    }

    /**
     * 读取基金数据
     *
     * @param hasCacheData 是否有缓冲数据
     */
    private void loadFundData(boolean hasCacheData) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("type", "0"));
        mController.execute(new UIAsyncTask(context, mGetAllFundHandler, params, BaseHandlerUI.REQUEST_get_allfund, hasCacheData));
    }

    /**
     * 初始化标题栏
     */
    private void initHeadView() {
        imgs.add(R.drawable.ad_null_default_pic);

        headerView = LayoutInflater.from(context).inflate(R.layout.item_pager, null);
        ad_pager = (MyViewPager) headerView.findViewById(R.id.ad_image);
        mImagePagerAdapter = new ImagePagerAdapter(context, mOnClick);
        mImagePagerAdapter.imgs = imgs;
        ad_pager.setAdapter(mImagePagerAdapter);
        ad_pager.setOnPageChangeListener(mOnPageChangeListener);

        ad_text = (TextView) headerView.findViewById(R.id.ad_text);

        page_control = (PageControlView) headerView.findViewById(R.id.pageControlView);
        page_control.count = 0;
        page_control.generatePageControl(currentPage);
        bannerStartHandler.postDelayed(bannerStartRun, Config.ad_title_speed);

        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            FixedSpeedScroller mScroller = new FixedSpeedScroller(ad_pager.getContext(), new AccelerateInterpolator());
            mField.set(ad_pager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        entrys.clear();
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllFundData();
        if (c != null) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    FundBean entry = new FundBean();
                    entry.f_id = c.getInt(c.getColumnIndex("_id"));
                    entry.fund_code = c.getString(c.getColumnIndex("fund_code"));
                    entry.fund_name = c.getString(c.getColumnIndex("fund_name"));
                    entry.type = c.getString(c.getColumnIndex("type"));
                    entry.day_growth = c.getString(c.getColumnIndex("day_growth"));
                    entry.netvalue = c.getString(c.getColumnIndex("netvalue"));
                    entry.rate_thounds_date = c.getString(c.getColumnIndex("rate_thounds_date"));
                    entry.rate_sevenday = c.getString(c.getColumnIndex("rate_sevenday"));
                    entry.rate_thounds = c.getString(c.getColumnIndex("rate_thounds"));
                    entry.netvalue_date = c.getString(c.getColumnIndex("netvalue_date"));
                    entry.rate_threemonth = c.getString(c.getColumnIndex("rate_threemonth"));
                    entry.rate_thisyear = c.getString(c.getColumnIndex("rate_thisyear"));
                    entry.rate_nearyear = c.getString(c.getColumnIndex("rate_nearyear"));
                    entry.is_fav = c.getString(c.getColumnIndex("is_fav"));
                    entry.date = c.getString(c.getColumnIndex("date"));
                    if (entry.type.equals("6")) {
                        entry.title02 = "万份收益";
                        entry.title03 = "七日年化";
                        entry.show_value = entry.rate_sevenday;
                    } else {
                        entry.title02 = "单位净值";
                        entry.title03 = "日涨跌幅";
                        entry.show_value = entry.day_growth;
                    }
                    entrys.add(entry);
                }
            }
        }
        c.close();
        mDatabaseAdapter.close_funds();
        List<FundBean> list_6 = new ArrayList<FundBean>();
        List<FundBean> list_c = new ArrayList<FundBean>();
        for (FundBean f : entrys) {
            if (f.type.equals("6")) {
                list_6.add(f);
            } else {
                list_c.add(f);
            }
        }
        list_6.addAll(list_c);
        entrys.clear();
        entrys.addAll(list_6);
    }

    /**
     * 获取数据
     */
    private void getDataBase() {
        getData();
        getFavData();
        for (FundBean f : entrys) {
            for (FundBean fundBean : fav_data) {
                if (fundBean.fund_code.equals(f.fund_code)) {
                    f.is_fav = "1";
                    break;
                }
            }
        }
    }

    /**
     * 判断是否有收藏基金
     *
     * @return 返回是否有收藏基金
     */
    private boolean hasFav() {
        boolean hasFav = false;
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllFavFundData();
        if (c != null) {
            if (c.getCount() > 0) {
                hasFav = true;
            }
        }
        mDatabaseAdapter.close_funds();
        return hasFav;
    }

    /**
     * 获取收藏基金
     */
    private void getFavData() {
        fav_data.clear();
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

    /**
     * 初始化基金信息
     *
     * @param fund_list 基金列表数据
     */
    private void initFundData(List<FundBean> fund_list) {
        String now = ConfigUtil.getNowDate();
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllFundData();
        if (c != null) {
            if (c.getCount() > 0) {
                mDatabaseAdapter.deleteAllFundData();
            }
            c.close();
        }
        for (int i = 0; i < fund_list.size(); i++) {
            FundBean entry = fund_list.get(i);
            if (fund_list.get(i).type.equals("6")) {
                entry.title02 = "万份收益";
                entry.title03 = "七日年化";
                entry.show_value = entry.rate_sevenday;
            } else {
                entry.title02 = "单位净值";
                entry.title03 = "日涨跌幅";
                entry.show_value = entry.day_growth;
            }
            mDatabaseAdapter.insertFundData(
                    entry.fund_code,
                    entry.fund_name,
                    entry.type,
                    entry.netvalue,
                    entry.day_growth,
                    entry.netvalue_date,
                    entry.rate_sevenday,
                    entry.rate_thounds,
                    entry.rate_thounds_date,
                    entry.rate_threemonth,
                    entry.rate_thisyear,
                    entry.rate_nearyear,
                    entry.is_fav,
                    now);
        }
        mDatabaseAdapter.close_funds();
        select_action();

    }

    private void select_action() {
        if (left_bean != null || right_bean != null) {
            if (left_bean != null) {
                fund_type = left_bean.type_sort;
            }
            if (right_bean != null) {
                sort = right_bean.type_sort;
            }
        }
        selection();
    }

    /**
     * 加载标题栏
     */
    private void selection() {
        isDefaultSort = false;
        getDataBase();
        List<FundBean> list = new ArrayList<FundBean>();
        if (entrys != null && entrys.size() > 0) {
            for (FundBean f : entrys) {
                if (fund_type == 0) {
                    switch (sort) {
                        case 0:// 默认排序
                            isDefaultSort = true;
                            list.add(f);
                            break;
                        case 1:// 日涨跌幅
                            if (f.day_growth != null && !f.day_growth.equals("")) {
                                if (!f.type.equals("6")) {
                                    f.title03 = "日涨跌幅";
                                    f.show_value = f.day_growth;
                                    list.add(f);
                                }
                            }
                            break;
                        case 2:// 最近三个月
                            if (f.rate_threemonth != null && !f.rate_threemonth.equals("")) {
                                if (!f.type.equals("6")) {
                                    f.title03 = "最近三个月";
                                    f.show_value = f.rate_threemonth;
                                    list.add(f);
                                }
                            }
                            break;
                        case 3:// 今年以来
                            if (f.rate_thisyear != null && !f.rate_thisyear.equals("")) {
                                if (!f.type.equals("6")) {
                                    f.title03 = "今年以来";
                                    f.show_value = f.rate_thisyear;
                                    list.add(f);
                                }
                            }
                            break;
                        case 4:// 最近一年
                            if (f.rate_nearyear != null && !f.rate_nearyear.equals("")) {
                                if (!f.type.equals("6")) {
                                    f.title03 = "最近一年";
                                    f.show_value = f.rate_nearyear;
                                    list.add(f);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                } else {
                    if (f.type.equals(fund_type + "")) {
                        switch (sort) {
                            case 0://0：默认排序
                                isDefaultSort = true;
                                if (fund_type == 6) {
                                    f.title02 = "万份收益";
                                    f.title03 = "七日年化";
                                    f.show_value = f.rate_sevenday;
                                } else {
                                    f.title02 = "单位净值";
                                    f.title03 = "日涨跌幅";
                                    f.show_value = f.day_growth;
                                }
                                list.add(f);
                                break;
                            case 1://1：当天涨跌幅
                                if (fund_type == 6) {
                                    f.title02 = "万份收益";
                                    f.title03 = "七日年化";
                                    f.show_value = f.rate_sevenday;
                                    list.add(f);
                                } else {
                                    if (f.day_growth != null && !f.day_growth.equals("")) {
                                        f.title02 = "单位净值";
                                        f.title03 = "日涨跌幅";
                                        f.show_value = f.day_growth;
                                        list.add(f);
                                    }
                                }
                                break;
                            case 2://2：最近三个月收益率
                                if (fund_type == 6) {
                                    f.title02 = "万份收益";
                                    f.title03 = "七日年化";
                                    f.show_value = f.rate_sevenday;
                                    list.add(f);
                                } else {
                                    if (f.rate_threemonth != null && !f.rate_threemonth.equals("")) {
                                        f.title02 = "单位净值";
                                        f.title03 = "最近三个月";
                                        f.show_value = f.rate_threemonth;
                                        list.add(f);
                                    }
                                }
                                break;
                            case 3://3：今年以来收益率
                                if (fund_type == 6) {
                                    f.title02 = "万份收益";
                                    f.title03 = "七日年化";
                                    f.show_value = f.rate_sevenday;
                                    list.add(f);
                                } else {
                                    if (f.rate_thisyear != null && !f.rate_thisyear.equals("")) {
                                        f.title02 = "单位净值";
                                        f.title03 = "今年以来";
                                        f.show_value = f.rate_thisyear;
                                        list.add(f);
                                    }
                                }

                                break;
                            case 4://4：最近一年收益率
                                if (fund_type == 6) {
                                    f.title02 = "万份收益";
                                    f.title03 = "七日年化";
                                    f.show_value = f.rate_sevenday;
                                    list.add(f);
                                } else {
                                    if (f.rate_nearyear != null && !f.rate_nearyear.equals("")) {
                                        f.title02 = "单位净值";
                                        f.title03 = "最近一年";
                                        f.show_value = f.rate_nearyear;
                                        list.add(f);
                                    }
                                }
                                break;

                            default:
                                break;
                        }
                    }
                }
            }
            entrys.clear();
            entrys.addAll(list);
            if (!isDefaultSort) {
                entrys = ConfigUtil.bubbleSort(entrys);
            }

            adapter = new TitledListAdapter(context, entrys);
            mListView.setAdapter(adapter);
            mListView.setOnScrollListener(adapter);
        }
    }

    @Override
    public void onRefresh() {
        loadFundData(false);
    }

    @Override
    public void onLoadMore() {
        onLoad();
    }

    /**
     * 暂停刷新和读取
     */
    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    /**
     * 跳转到基金信息页
     *
     * @param p 页码
     */
    private void gotoFundDetail(int p) {
        onLoad();
        Intent intent = new Intent();
        intent.setClass(context, FundDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Config.LIST_POSITION, p);
        intent.putExtra("fund", entrys.get(p));
        ((Activity) context).startActivityForResult(intent, Config.ACTIVITY_REQUEST_CODE);
    }

    /**
     * - - - - - - - - 跳转到  "收藏基金" 的  Activity  - - - - - - - -
     */
    private void gotoFundFav() {
        onLoad();
        Intent i = new Intent();
        i.setClass(context, FundFavActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ((Activity) context).startActivityForResult(i, Config.FAV_ACTIVITY_REQUEST_CODE);
    }

    /**
     * - - - - - - - - 跳转到  "网络错误" 的  Activity  - - - - - - - -
     */
    private void gotoNetErr() {
        onLoad();
        Intent i = new Intent();
        i.setClass(context, NetErrActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    /**
     * 显示 popupWindow 监听器
     */
    private void showPopListener() {
        View v = LayoutInflater.from(context).inflate(R.layout.item_fund_type_pop, null);
        ListView list_left = (ListView) v.findViewById(R.id.list_left);
        ListView list_right = (ListView) v.findViewById(R.id.list_right);

        adapter_left = new Text_Adapter(context, data_left, mOnClick_l, true);
        adapter_right = new Text_Adapter(context, data_right, mOnClick_r, false);
        list_left.setAdapter(adapter_left);
        list_right.setAdapter(adapter_right);

        int h = container.getHeight();
        popupWindow = new PopupWindow(v, displayWidth, h, true);
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(left_btn, 0, 0);
        left_btn.setText("取消");
        right_btn.setVisibility(View.GONE);
        right_btn_sure.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏 popupWindow
     */
    private void disPopListener() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                popupWindow = null;
            }
        }
    }

    public void onActivityResult(int request_code, int resultCode, Intent data) {
        if (resultCode == ((Activity) context).RESULT_OK) {
            if (request_code == Config.ACTIVITY_REQUEST_CODE) {
                int position = data.getIntExtra(Config.LIST_POSITION, 0);
                entrys.get(position).is_fav = data.getStringExtra(Config.FAV_FLAG);
                adapter.notifyDataSetChanged();
            } else if (request_code == Config.FAV_ACTIVITY_REQUEST_CODE) {
                select_action();
            }
        }
    }

    private void initChoose() {
        data_left = new ArrayList<FundTitleBean>();
        data_left.add(new FundTitleBean(true, "全部", 0));
        data_left.add(new FundTitleBean(false, "股票型", 1));
        data_left.add(new FundTitleBean(false, "混合型", 2));
        data_left.add(new FundTitleBean(false, "指数型", 3));
        data_left.add(new FundTitleBean(false, "QDII", 4));
        data_left.add(new FundTitleBean(false, "债券型", 5));
        data_left.add(new FundTitleBean(false, "货币型", 6));
        data_left.add(new FundTitleBean(false, "保本型", 7));

        data_right = new ArrayList<FundTitleBean>();
        data_right.add(new FundTitleBean(true, "默认排序", 0));
        data_right.add(new FundTitleBean(false, "当天涨跌幅", 1));
        data_right.add(new FundTitleBean(false, "最近三个月收益率", 2));
        data_right.add(new FundTitleBean(false, "今年以来收益率", 3));
        data_right.add(new FundTitleBean(false, "最近一年收益率", 4));
    }

    public void disPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            open_ever = true;
            disPopListener();
            changeRightBtnStatus();
        }
    }

    public void showPop() {
        if (open_ever) {
            open_ever = false;
            showPopListener();
        }
    }

    /**
     * 返回键回调方法
     */
    public void onKeyBack() {
        if (popupWindow != null && popupWindow.isShowing()) {
            open_ever = false;
            disPopListener();
            changeRightBtnStatus();
        } else {
            if (((MainActivity) context).currentBottomPosition != 0) {
                ((MainActivity) context).gotoFirst();
            } else {
                showDoubleAlertDlg(context.getString(R.string.dlg_exit_ask),
                        context.getString(R.string.dlg_sure),
                        context.getString(R.string.dlg_cancel));
            }
        }
    }

    @Override
    public void yesBtnWork() {
        super.yesBtnWork();
        GFFApp.getInstance().exit();
    }

    /**
     * left_bean: 左边筛选器对象
     */
    private void changeRightBtnStatus() {
        for (FundTitleBean bean : data_left) {
            bean.isSelected = false;
        }
        if (left_bean != null) {
            data_left.get(left_bean.type_sort).isSelected = true;
        } else {
            data_left.get(0).isSelected = true;
        }
        for (FundTitleBean bean : data_right) {
            bean.isSelected = false;
        }
        if (right_bean != null) {
            data_right.get(right_bean.type_sort).isSelected = true;
        } else {
            data_right.get(0).isSelected = true;
        }
        left_btn.setText("筛选");
        if (left_bean != null) {
            if (left_bean.type_sort == 0) {
                title.setText("全部基金");
            } else {
                title.setText(left_bean.name);
            }
        }
        right_btn.setVisibility(View.VISIBLE);
        right_btn_sure.setVisibility(View.INVISIBLE);
    }

    /**
     * - - - - - - - - -  显示网络通告   - - - - - - - - -
     */
    public void showNetNotice() {
        if (!ConfigUtil.isConnect(context)) {
            net_err_view.setVisibility(View.VISIBLE);
        } else {
            net_err_view.setVisibility(View.GONE);
        }
    }
}
