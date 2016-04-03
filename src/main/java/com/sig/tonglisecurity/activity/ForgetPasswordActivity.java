package com.sig.tonglisecurity.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.utils.LogUtil;


/**
 * 忘记密码界面
 */
public class ForgetPasswordActivity extends Activity {

    private static final String TAG = "ForgetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "ForgetPasswordActivity--> onCreate");

        setContentView(R.layout.activity_forget_password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forget_password, menu);
        return true;
    }

}
