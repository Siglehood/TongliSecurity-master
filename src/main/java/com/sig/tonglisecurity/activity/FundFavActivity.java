package com.sig.tonglisecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.adapter.TitledCommonXListAdapter;
import com.sig.tonglisecurity.app.Config;
import com.sig.tonglisecurity.bean.FundBean;
import com.sig.tonglisecurity.database.DatabaseAdapter;
import com.sig.tonglisecurity.task.BaseHandlerUI;
import com.sig.tonglisecurity.task.UIAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.widget.TitledCommonXListView;
import com.sig.tonglisecurity.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * - - - - - - - - - -  基金收藏列表   - - - - - - - - - -
 */
public class FundFavActivity extends ParentActivity implements XListView.IXListViewListener {

    public static final String TAG = "FundFavActivity";
    OnClickListener mOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.left_btn) {
                finish();
            } else if (id == R.id.right_btn) {
                finish();
            }
        }
    };
    private TextView left_btn;
    private TextView title;
    private TitledCommonXListAdapter adapter;
    private DatabaseAdapter mDatabaseAdapter;
    private TitledCommonXListView mListView;
    private List<FundBean> fav_entrys = new ArrayList<FundBean>();
    OnItemClickListener itemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i,
                                long l) {
            int p = 1;
            if (i > 0) {
                p = i - 1;
            }
            if (parent == mListView) {
                gotoFundDetail(p);
            }
        }
    };
    private Handler getFavHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseHandlerUI.TASK_NOTIFY_RETURN_DATA:
                    dismissProgressDialog();
                    if (msg.obj != null) {
                        try {
                            fav_entrys = (List<FundBean>) msg.obj;
                            adapter = new TitledCommonXListAdapter(context, fav_entrys);
                            mListView.setAdapter(adapter);
                            mListView.setOnScrollListener(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    last_refresh_date = ConfigUtil.getNowTime();
                    mListView.setRefreshTime(ConfigUtil.getRefreshTime(last_refresh_date));
                    onLoad();
                    break;
            }
        }
    };
    private int position;
    OnItemLongClickListener itemLongClick = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int i, long l) {
            int p = 1;
            if (i > 0) {
                p = i - 1;
            }
            if (parent == mListView) {
                position = p;
                showDoubleAlertDlg(getString(R.string.dlg_del_fav_question),
                        getString(R.string.dlg_sure),
                        getString(R.string.dlg_cancel));
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "FundFavActivity--> onCreate");

        setContentView(R.layout.a_fund_fav);
        initView();
    }

    private void initView() {
        mDatabaseAdapter = new DatabaseAdapter(context, Config.database_version);
        left_btn = (TextView) findViewById(R.id.left_btn);
        title = (TextView) findViewById(R.id.title_txt);
        title.setText("titleText");

        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(mOnClick);
        mListView = (TitledCommonXListView) findViewById(R.id.xListView);

        adapter = new TitledCommonXListAdapter(context, fav_entrys);
        mListView.setAdapter(adapter);
        mListView.setOnScrollListener(adapter);
        mListView.setOnItemClickListener(itemClick);
        mListView.setOnItemLongClickListener(itemLongClick);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(ConfigUtil.getRefreshTime(last_refresh_date));

        mController.execute(new UIAsyncTask(getFavHandler, mDatabaseAdapter,
                BaseHandlerUI.REQUEST_GET_FAV));
    }

    public void yesBtnWork() {
        try {
            mDatabaseAdapter.open_fund();
            mDatabaseAdapter.deleteFavData(fav_entrys.get(position).f_id);
            mDatabaseAdapter.close_funds();
            fav_entrys.remove(position);
            adapter.notifyDataSetChanged();
            setResult(RESULT_OK, getIntent());
            if (fav_entrys.size() <= 0) {
                mListView.setVisibility(View.GONE);
                finish();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    /**
     * 跳转到基金信息页
     *
     * @param p 页码
     */
    private void gotoFundDetail(int p) {
        Intent i = new Intent();
        i.setClass(context, FundDetailActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra(Config.LIST_POSITION, p);
        i.putExtra("fund", fav_entrys.get(p));
        context.startActivityForResult(i, Config.ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int position = data.getIntExtra(Config.LIST_POSITION, 0);
            String fav_flag = data.getStringExtra(Config.FAV_FLAG);
            if (fav_flag.equals("0")) {
                fav_entrys.remove(position);
                adapter.notifyDataSetChanged();
                setResult(RESULT_OK, getIntent());
            }
            if (fav_entrys.size() <= 0) {
                mListView.setVisibility(View.GONE);
                showSingleAlertDlg(getString(R.string.dlg_no_fav));
            } else {
                mListView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        mController.execute(new UIAsyncTask(getFavHandler, mDatabaseAdapter, BaseHandlerUI.REQUEST_GET_FAV));
    }

    @Override
    public void onLoadMore() {
        onLoad();
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }
}
