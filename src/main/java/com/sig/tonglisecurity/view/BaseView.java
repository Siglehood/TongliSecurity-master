package com.sig.tonglisecurity.view;


import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.task.Controller;


/**
 * 基视图  BaseView
 */
public class BaseView {

    protected Controller mController;
    protected View v;
    protected Context context;
    protected Dialog dialog_p;

    public BaseView(FragmentActivity activity, View v) {
        super();
        mController = Controller.getInstance();   //单例
        this.context = activity;
        this.v = v;
    }

    /**
     * 初始化基视图
     *
     * @param v 视图对象
     */
    protected void initBaseView(View v) {
    }

    /**
     * 显示进度条
     *
     * @param context 上下文对象
     * @param msg     显示内容
     */
    protected void showProgressDialog(Context context, String msg) {
        dialog_p = new ProgressDialog(context);
        ((ProgressDialog) dialog_p)
                .setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
     * 关闭进度条
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
     * 单选对话框
     *
     * @param msg 显示信息
     */
    public void showSingleAlertDlg(String msg) {
        new Builder(context)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.dlg_i_konw),
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
     * 双选对话框
     *
     * @param msg 显示信息
     * @param yes 确定信息
     * @param no  取消信息
     */
    public void showDoubleAlertDlg(String msg, String yes, String no) {

        final LayoutInflater layout = LayoutInflater.from(context);
        final View v = layout.inflate(R.layout.item_dlg_content_view, null);
        TextView content = (TextView) v.findViewById(R.id.content);
        content.setText(msg);

        new Builder(context)
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
     * toast 展示
     *
     * @param msg 显示信息
     */
    protected void showToast(String msg) {
        Toast tr = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        tr.setGravity(Gravity.CENTER, 0, 0);
        tr.show();
    }

}
