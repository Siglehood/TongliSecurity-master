package com.sig.tonglisecurity.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.utils.LogUtil;


/**
 * 网络不可用
 */
public class NetErrActivity extends ParentActivity implements OnClickListener {

    public static final String TAG = "NetErrActivity";

    private TextView title;
    private TextView left_btn, right_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "NetErrActivity--> onCreate");

        setContentView(R.layout.a_net_err);
        initView();
    }

    public void initView() {
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(getString(R.string.dlg_net_err));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        left_btn.setVisibility(View.VISIBLE);
        right_btn = (TextView) findViewById(R.id.right_btn);
        right_btn.setVisibility(View.GONE);
    }

    /**
     * 菜单栏点击事件
     *
     * @param v 点击控件
     */
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
