package com.sig.tonglisecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.adapter.MsgListAdapter;
import com.sig.tonglisecurity.app.Config;
import com.sig.tonglisecurity.bean.GetMessageByPageResultBean;
import com.sig.tonglisecurity.bean.MessageBean;
import com.sig.tonglisecurity.database.DatabaseAdapter;
import com.sig.tonglisecurity.interfaces.LoadNextPageListener;
import com.sig.tonglisecurity.task.BaseHandlerUI;
import com.sig.tonglisecurity.task.UIAsyncTask;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.widget.XListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息中心界面
 */
public class MsgCenterActivity extends ParentActivity implements OnClickListener, XListView.IXListViewListener, OnItemClickListener {

    public static final String TAG = "MsgCenterActivity";

    private TextView title;
    private TextView left_btn, right_btn;
    private int currentPage = 1;
    private GetMessageByPageResultBean mGetMessageByPageBeanResult = new GetMessageByPageResultBean();
    private List<MessageBean> msgList = new ArrayList<MessageBean>();
    private XListView mXListView;
    private MsgListAdapter adapter;
    private DatabaseAdapter mDatabaseAdapter;
    private Handler getMessageByPageHandler = new Handler() {
        public void handleMessage(Message msg) {
            dismissProgressDialog();
            switch (msg.what) {
                case BaseHandlerUI.TASK_NOTIFY_RETURN_DATA:
                    if (msg.obj != null) {
                        try {
                            mGetMessageByPageBeanResult = (GetMessageByPageResultBean) msg.obj;
                            if (mGetMessageByPageBeanResult.state.equals("0")) {
                                boolean isEnd;
                                if (currentPage < mGetMessageByPageBeanResult.totalPages) {
                                    isEnd = false;
                                } else {
                                    isEnd = true;
                                }
                                if (adapter == null || currentPage == 1) {
                                    msgList.clear();
                                    msgList.addAll(mGetMessageByPageBeanResult.msgList);
                                    adapter = new MsgListAdapter(msgList, context, mLoadNextPage);
                                    adapter.isEnd = isEnd;
                                    mXListView.setAdapter(adapter);
                                } else {
                                    msgList.addAll(mGetMessageByPageBeanResult.msgList);
                                    adapter.isEnd = isEnd;
                                    adapter.notifyDataSetChanged();
                                }
                            } else if (mGetMessageByPageBeanResult.state.equals("-1")) {//û����Ϣ
                                showToast(getString(R.string.dlg_no_msg));
                            } else {
                                if (currentPage > 1) {
                                    currentPage--;
                                }
                                showToast(getString(R.string.dlg_load_err_try_again));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                    last_refresh_date = ConfigUtil.getNowTime();
                    mXListView.setRefreshTime(ConfigUtil.getRefreshTime(last_refresh_date));
                    onLoad();
                    break;
            }
        }
    };
    LoadNextPageListener mLoadNextPage = new LoadNextPageListener() {

        @Override
        public void loadNextPage(int flag) {
            currentPage++;
            if (currentPage > 0) {
                getMsgList();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "MsgCenterActivity()");

        setContentView(R.layout.a_msg_center);
        initView();
    }

    public void initView() {
        mDatabaseAdapter = new DatabaseAdapter(context, Config.database_version);
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(getString(R.string.settings_msg_center));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        right_btn = (TextView) findViewById(R.id.right_btn);
        right_btn.setBackgroundResource(R.drawable.common_btn_l_drawable);
        right_btn.setVisibility(View.VISIBLE);
        right_btn.setText(getString(R.string.settings_msg_right));
        right_btn.setOnClickListener(this);
        mXListView = (XListView) findViewById(R.id.msg_center_listview);
        mXListView.setPullLoadEnable(true);
        mXListView.setXListViewListener(this);

        mXListView.setRefreshTime(ConfigUtil.getRefreshTime(last_refresh_date));
        mXListView.setOnItemClickListener(this);
        showProgressDialog(context, context.getString(R.string.dlg_loading));
        getMsgList();
    }

    /**
     * 获取信息列表
     */
    private void getMsgList() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("currentPage", Integer.toString(currentPage)));
        mController.execute(new UIAsyncTask(context, getMessageByPageHandler,
                params, BaseHandlerUI.REQUEST_getMessageByPage, mDatabaseAdapter));
    }

    private void addMsgDB(MessageBean bean) {
        mDatabaseAdapter.open_fund();
        mDatabaseAdapter.insertMsgData(bean);
        mDatabaseAdapter.close_funds();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                if (msgList != null && msgList.size() > 0) {
                    for (MessageBean bean : msgList) {
                        if (!bean.hasRead) {
                            bean.hasRead = true;
                            addMsgDB(bean);
                        }
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.left_btn:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        getMsgList();
    }

    @Override
    public void onLoadMore() {
        onLoad();
    }

    private void onLoad() {
        mXListView.stopRefresh();
        mXListView.stopLoadMore();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int p, long id) {
        msgList.get(p - 1).hasRead = true;
        adapter.notifyDataSetChanged();
        addMsgDB(msgList.get(p - 1));
        if (msgList.get(p - 1).url != null && !msgList.get(p - 1).url.equals("")) {
            Intent i = new Intent();
            i.setClass(context, MsgDetailActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(Config.LIST_POSITION, p - 1);
            i.putExtra("url", msgList.get(p - 1).url);
            context.startActivityForResult(i, Config.ACTIVITY_REQUEST_CODE);
        }
    }
}
