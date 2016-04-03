package com.sig.tonglisecurity.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.activity.AboutUsActivity;
import com.sig.tonglisecurity.activity.FeedbackActivity;
import com.sig.tonglisecurity.activity.HelpsActivity;
import com.sig.tonglisecurity.activity.MsgCenterActivity;
import com.sig.tonglisecurity.app.Config;
import com.sig.tonglisecurity.bean.GetMessageByPageResultBean;
import com.sig.tonglisecurity.bean.MessageBean;
import com.sig.tonglisecurity.database.DatabaseAdapter;
import com.sig.tonglisecurity.task.BaseHandlerUI;
import com.sig.tonglisecurity.task.UIAsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class SettingsView extends BaseView {
    OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.settings_feedback:
                    context.startActivity(new Intent(context, FeedbackActivity.class));
                    break;
                case R.id.settings_msg_center:
                    context.startActivity(new Intent(context, MsgCenterActivity.class));
                    break;
                case R.id.settings_help:
                    context.startActivity(new Intent(context, HelpsActivity.class));
                    break;
                case R.id.settings_about:
                    context.startActivity(new Intent(context, AboutUsActivity.class));
                    break;
                case R.id.settings_customer:
                    showDoubleAlertDlg(context.getString(R.string.tel_num),
                            context.getString(R.string.dlg_tel),
                            context.getString(R.string.dlg_cancel));
                    break;
                default:
                    break;
            }
        }
    };
    private TextView title;
    private ImageView notice_logo;
    private DatabaseAdapter mDatabaseAdapter;
    private Handler getMessageByPageHandler = new Handler() {
        public void handleMessage(Message msg) {
            dismissProgressDialog();
            switch (msg.what) {
                case BaseHandlerUI.TASK_NOTIFY_RETURN_DATA:
                    if (msg.obj != null) {
                        try {
                            GetMessageByPageResultBean mGetMessageByPageBeanResult = (GetMessageByPageResultBean) msg.obj;
                            if (mGetMessageByPageBeanResult.state.equals("0")) {
                                for (MessageBean bean : mGetMessageByPageBeanResult.msgList) {
                                    if (!bean.hasRead) {
                                        notice_logo.setVisibility(View.VISIBLE);
                                        return;
                                    }
                                }

                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                    notice_logo.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public SettingsView(FragmentActivity activity, View v) {
        super(activity, v);
    }

    @Override
    public void initBaseView(View v) {
        super.initBaseView(v);
        mDatabaseAdapter = new DatabaseAdapter(context, Config.database_version);
        title = (TextView) v.findViewById(R.id.title_txt);
        title.setText(context.getString(R.string.tab_title_04));
        this.v.findViewById(R.id.settings_feedback).setOnClickListener(mOnClick);
        this.v.findViewById(R.id.settings_msg_center).setOnClickListener(mOnClick);
        this.v.findViewById(R.id.settings_help).setOnClickListener(mOnClick);
        this.v.findViewById(R.id.settings_about).setOnClickListener(mOnClick);
        this.v.findViewById(R.id.settings_customer).setOnClickListener(mOnClick);
        notice_logo = (ImageView) v.findViewById(R.id.notice_logo_msg);
        notice_logo.setVisibility(View.GONE);
    }

    public void yesBtnWork() {
        String tel_num = context.getString(R.string.widget_tel_num);
        Uri uri = Uri.parse(tel_num);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    @Override
    public void noBtnWork() {
        // TODO Auto-generated method stub
        super.noBtnWork();
    }

    public void setNoticeShow() {
        getMsgList();
    }

    private void getMsgList() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("currentPage", Integer.toString(1)));
        mController.execute(new UIAsyncTask(context, getMessageByPageHandler,
                params, BaseHandlerUI.REQUEST_getMessageByPage, mDatabaseAdapter));
    }
}

