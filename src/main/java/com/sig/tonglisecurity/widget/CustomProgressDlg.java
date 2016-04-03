package com.sig.tonglisecurity.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.sig.tonglisecurity.R;


public class CustomProgressDlg extends Dialog {
    Context context;
    private String content;

    public CustomProgressDlg(Context context, int theme, String content) {
        super(context, theme);
        this.context = context;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.item_progressdlg_view);
    }
}
