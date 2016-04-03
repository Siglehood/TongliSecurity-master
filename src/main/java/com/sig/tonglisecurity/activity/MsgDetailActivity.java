package com.sig.tonglisecurity.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.sig.tonglisecurity.R;


/**
 * 消息详情界面
 */
public class MsgDetailActivity extends ParentActivity implements OnClickListener {
    private TextView title;
    private TextView left_btn, right_btn;
    private WebView webview;
    private Thread thread;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_msg_detail);
        initView();
    }

    private void initView() {
        Intent i = getIntent();

        if (i != null) {
            url = i.getStringExtra("url");
        }

        title = (TextView) findViewById(R.id.title_txt);
        title.setText(getString(R.string.settings_msg_detail));
        left_btn = (TextView) findViewById(R.id.left_btn);
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        right_btn = (TextView) findViewById(R.id.right_btn);
        right_btn.setVisibility(View.VISIBLE);
        right_btn.setText("刷新");
        right_btn.setOnClickListener(this);

        webview = (WebView) findViewById(R.id.website_view);
        loadUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadUrl() {
        showProgressDialog(context, context.getString(R.string.dlg_loading));
        thread = new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setPluginState(WebSettings.PluginState.OFF);
                webview.getSettings().setSupportZoom(true);
                webview.getSettings().setBuiltInZoomControls(true);
                //加载url前，设置图片阻塞
                webview.getSettings().setBlockNetworkImage(true);
                webview.loadUrl(url);
                MyWebViewClient myWebView = new MyWebViewClient();
                webview.setWebViewClient(myWebView);
            }
        });

        thread.start();
    }

    /**
     * 标题栏点击事件
     *
     * @param v 点击的控件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                loadUrl();
                break;
            case R.id.left_btn:
                finish();
                break;
            default:
                break;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //加载完毕后，关闭图片阻塞
            webview.getSettings().setBlockNetworkImage(false);
            dismissProgressDialog();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            dismissProgressDialog();
        }
    }

}
