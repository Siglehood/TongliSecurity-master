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
import com.sig.tonglisecurity.bean.TopicInfoBean;

/**
 * 用户帮助界面
 */
public class HeadDetailActivity extends ParentActivity {
    Thread thread;
    OnClickListener clickEvent = new OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.left_btn) {
                finish();
            }
        }
    };
    private TextView btn_left, btn_right;
    private TextView title_txt;
    private WebView webview;
    private TopicInfoBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_head_detail);
        init();
    }

    private void init() {
        Intent i = getIntent();

        if (i != null) {
            bean = i.getParcelableExtra("topic_bean");
        }

        title_txt = (TextView) findViewById(R.id.title_txt);
        title_txt.setText(bean.title);
        btn_left = (TextView) findViewById(R.id.left_btn);
        btn_left.setVisibility(View.VISIBLE);
        btn_left.setOnClickListener(clickEvent);
        btn_right = (TextView) findViewById(R.id.right_btn);
        btn_right.setVisibility(View.GONE);
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
                webview.loadUrl(bean.url);
                MyWebViewClient myWebView = new MyWebViewClient();
                webview.setWebViewClient(myWebView);
            }
        });
        thread.start();

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
