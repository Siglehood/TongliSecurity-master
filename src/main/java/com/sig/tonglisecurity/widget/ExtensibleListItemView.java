package com.sig.tonglisecurity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class ExtensibleListItemView extends LinearLayout {
    public ExtensibleListItemView(Context context) {
        super(context);
    }

    public ExtensibleListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void setData(Object data);

    public abstract void setData(Object data, boolean unfold);
}
