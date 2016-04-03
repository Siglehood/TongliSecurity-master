package com.sig.tonglisecurity.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.utils.LogUtil;


/**
 * 帮助界面
 */
public class AboutUsActivity extends ParentActivity implements OnClickListener {

    public static final String TAG = "AboutUsActivity";

    private TextView title, text_14_m_b;
    private TextView left_btn, right_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "AboutUsActivity--> onCreate");

        setContentView(R.layout.a_about_us);
        initView();
    }

    /**
     * 初始化控件
     */
    public void initView() {
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(getString(R.string.settings_about));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        left_btn.setVisibility(View.VISIBLE);
        right_btn = (TextView) findViewById(R.id.right_btn);
        right_btn.setVisibility(View.GONE);
        text_14_m_b = (TextView) findViewById(R.id.version_txt);
        text_14_m_b.setText(text_14_m_b.getText().toString() + ConfigUtil.getVersionName(context));
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
