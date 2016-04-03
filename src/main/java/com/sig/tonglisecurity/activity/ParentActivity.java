package com.sig.tonglisecurity.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.app.GFFApp;
import com.sig.tonglisecurity.bean.AccountInfo;
import com.sig.tonglisecurity.task.Controller;
import com.sig.tonglisecurity.widget.CustomProgressDlg;


/**
 * 基类
 */
public class ParentActivity extends FragmentActivity {
    private static final long TIME_OUT = 10L * 60 * 1000;
    protected static long pause_millisecond = 0;
    protected Controller mController;
    protected Activity context;
    protected String last_refresh_date;
    protected Dialog dialog_p;
    CustomProgressDlg c_dialog_p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mController = Controller.getInstance();
        context = this;
        GFFApp.getInstance().addActivity(this);
    }

    /**
     * 显示进度框
     *
     * @param context 上下文对象
     * @param msg     显示信息
     */
    protected void showCustomProgressDialog(Context context, String msg) {
        c_dialog_p = new CustomProgressDlg(context, R.style.CustomDialog, msg);
        if (!c_dialog_p.isShowing()) {
            try {
                c_dialog_p.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭进度框
     */
    protected void dismissCustomProgressDialog() {
        if (null != c_dialog_p && c_dialog_p.isShowing()) {
            try {
                c_dialog_p.dismiss();
                c_dialog_p = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示进度框
     *
     * @param context Activity
     * @param msg     显示信息
     */
    protected void showProgressDialog(Context context, String msg) {
        dialog_p = new ProgressDialog(context);
        ((ProgressDialog) dialog_p)
                .setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //	dialog_p.setTitle(title);
        ((ProgressDialog) dialog_p).setMessage(msg);
        if (null != dialog_p && !dialog_p.isShowing()) {
            try {
                dialog_p.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 隐藏进度框
     */
    protected void dismissProgressDialog() {
        if (null != dialog_p && dialog_p.isShowing()) {
            try {
                dialog_p.dismiss();
                dialog_p = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示单选提示框
     *
     * @param msg 显示的信息
     */
    public void showSingleAlertDlg(String msg) {
        new Builder(this)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.dlg_i_konw),
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                singleBtnWork();
                            }
                        }).create().show();
    }

    public void singleBtnWork() {
    }

    /**
     * 显示双选提示框
     *
     * @param msg 提示信息
     * @param yes 确定
     * @param no  取消
     */
    public void showDoubleAlertDlg(String msg, String yes, String no) {
        final LayoutInflater layout = LayoutInflater.from(this);
        final View v = layout.inflate(R.layout.item_dlg_content_view, null);
        TextView content = (TextView) v.findViewById(R.id.content);
        content.setText(msg);
        new Builder(this)
                .setView(v)
                .setPositiveButton(yes, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        yesBtnWork();
                    }
                })
                .setNegativeButton(no, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        noBtnWork();
                    }
                })
                .create().show();
    }

    public void yesBtnWork() {
    }

    public void noBtnWork() {
    }

    /**
     * 显示消息
     */
    protected void showToast(String msg) {
        Toast tr = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        tr.setGravity(Gravity.CENTER, 0, 0);
        tr.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkLoginTimeOut();
    }

    @Override
    protected void onPause() {
        super.onPause();

        checkLoginTimeOut();
        pause_millisecond = System.currentTimeMillis();
    }

    /**
     * 检查是否登录超时
     */
    protected void checkLoginTimeOut() {
        if (pause_millisecond != 0 && System.currentTimeMillis() - pause_millisecond > TIME_OUT) {
            if (!MainActivity.MyAccount.getSession_id().equals("")) {
                MainActivity.MyAccount = new AccountInfo();
                context.sendBroadcast(new Intent(
                        "com.hctforgf.gff.signin"));
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                showToast("登录超时，请重新登录!");
            }
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        checkLoginTimeOut();
        pause_millisecond = System.currentTimeMillis();
    }
}
