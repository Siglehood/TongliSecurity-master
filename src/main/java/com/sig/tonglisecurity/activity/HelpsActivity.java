package com.sig.tonglisecurity.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.adapter.TreeViewAdapter;
import com.sig.tonglisecurity.app.Config;
import com.sig.tonglisecurity.bean.GetHelpsResultBean;
import com.sig.tonglisecurity.bean.HelpsBean;
import com.sig.tonglisecurity.database.DatabaseAdapter;
import com.sig.tonglisecurity.task.BaseHandlerUI;
import com.sig.tonglisecurity.task.UIAsyncTask;

import java.util.List;

/**
 * 帮助界面
 */
public class HelpsActivity extends ParentActivity implements OnClickListener {
    private TextView title;
    private TextView left_btn, right_btn;
    private ExpandableListView expandableList;
    private TreeViewAdapter adapter;
    private GetHelpsResultBean bean = new GetHelpsResultBean();
    private DatabaseAdapter mDatabaseAdapter;
    /**
     * 获取帮助消息
     */
    private Handler getHelpsHandler = new Handler() {
        public void handleMessage(Message msg) {
            dismissProgressDialog();
            switch (msg.what) {
                case BaseHandlerUI.TASK_NOTIFY_RETURN_DATA:
                    if (msg.obj != null) {
                        try {
                            bean = (GetHelpsResultBean) msg.obj;
                            if (bean.state.equals("0")) {
                                mDatabaseAdapter.open_fund();
                                mDatabaseAdapter.deleteAllHelpsData();
                                List<TreeViewAdapter.TreeNode> treeNode = adapter.GetTreeNode();
                                treeNode.clear();
                                for (int i = 0; i < bean.list.size(); i++) {
                                    mDatabaseAdapter.insertHelpsData(bean.list.get(i).ask, bean.list.get(i).reply);
                                    TreeViewAdapter.TreeNode node = new TreeViewAdapter.TreeNode();
                                    node.parent = bean.list.get(i).ask;
                                    for (int ii = 0; ii < 1; ii++) {
                                        node.childs.add(bean.list.get(i).reply);
                                    }
                                    treeNode.add(node);
                                }
                                adapter.UpdateTreeNode(treeNode);
                                expandableList.setAdapter(adapter);
                                mDatabaseAdapter.close_funds();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_helps);
        mDatabaseAdapter = new DatabaseAdapter(context, Config.database_version);
        initView();
    }

    public void initView() {
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(getString(R.string.settings_help));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        left_btn.setVisibility(View.VISIBLE);
        right_btn = (TextView) findViewById(R.id.right_btn);
        right_btn.setVisibility(View.GONE);
        adapter = new TreeViewAdapter(this);
        expandableList = (ExpandableListView) findViewById(R.id.ExpandableListView);
        showProgressDialog(context, context.getString(R.string.dlg_loading));
        mController.execute(new UIAsyncTask(context, getHelpsHandler, BaseHandlerUI.REQUEST_getHelps));
        mDatabaseAdapter.open_fund();
        Cursor c = mDatabaseAdapter.fetchAllHelpsData();
        if (c != null) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    HelpsBean entry = new HelpsBean();
                    entry.ask = c.getString(c.getColumnIndex("ask"));
                    entry.reply = c.getString(c.getColumnIndex("reply"));
                    bean.list.add(entry);
                }
            }
        }
        mDatabaseAdapter.close_funds();
        List<TreeViewAdapter.TreeNode> treeNode = adapter.GetTreeNode();
        for (int i = 0; i < bean.list.size(); i++) {
            TreeViewAdapter.TreeNode node = new TreeViewAdapter.TreeNode();
            node.parent = bean.list.get(i).ask;
            for (int ii = 0; ii < 1; ii++) {
                node.childs.add(bean.list.get(i).reply);
            }
            treeNode.add(node);
        }
        adapter.UpdateTreeNode(treeNode);
        expandableList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                break;
            case R.id.left_btn:
                finish();
                break;
            default:
                break;
        }

    }

}
