package com.sig.tonglisecurity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {
    private android.widget.BaseAdapter adapter;
    private OnClickListener onClickListener = null;

    public LinearLayoutForListView(Context context) {
        super(context);
    }

    public LinearLayoutForListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void fillLinearLayout() {
        removeAllViews();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View v = adapter.getView(i, null, null);
            v.setOnClickListener(this.onClickListener);
            v.setTag(i);
            addView(v, i);
        }
    }

    public android.widget.BaseAdapter getAdpater() {
        return adapter;
    }

    public void setAdapter(android.widget.BaseAdapter adpater) {
        setOrientation(LinearLayout.VERTICAL);
        this.adapter = adpater;
        fillLinearLayout();
    }

    public OnClickListener getOnclickListner() {
        return onClickListener;
    }

    public void setOnclickLinstener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
