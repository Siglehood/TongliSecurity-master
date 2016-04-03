package com.sig.tonglisecurity.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sig.tonglisecurity.R;

public class TitledListView extends XListView {
    private View mTitle;

    public TitledListView(Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    public TitledListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitledListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mTitle != null) {
            measureChild(mTitle, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mTitle != null) {
            mTitle.layout(0, 0, mTitle.getMeasuredWidth(), mTitle.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        drawChild(canvas, mTitle, getDrawingTime());
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        mTitle = inflater.inflate(R.layout.item_fund_list_title_view, this, false);
    }

    public void moveTitle() {
        View bottomChild = getChildAt(0);

        if (bottomChild != null) {
            int bottom = bottomChild.getBottom();
            int height = mTitle.getMeasuredHeight();
            int y = 0;
            if (bottom < height) {
                y = bottom - height;
            }
            mTitle.layout(0, y, mTitle.getMeasuredWidth(), mTitle.getMeasuredHeight() + y);

        }
    }

    public void updateTitle(String type, String txt03) {
        mTitle.setBackgroundResource(R.drawable.fund_title_bg);
        TextView fund_title_tab01 = (TextView) mTitle.findViewById(R.id.fund_title_tab01);
        TextView fund_title_tab02 = (TextView) mTitle.findViewById(R.id.fund_title_tab02);
        TextView fund_title_tab03 = (TextView) mTitle.findViewById(R.id.fund_title_tab03);
        if (type != null) {
            fund_title_tab01.setText("基金简称");
            if (type.equals("6")) {
                fund_title_tab02.setText("万份收益");
            } else {
                fund_title_tab02.setText("单位净值");
            }
            fund_title_tab03.setText(txt03);
        } else {
            fund_title_tab01.setText("");
            fund_title_tab02.setText("");
            fund_title_tab03.setText("");
            mTitle.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
        mTitle.layout(0, 0, mTitle.getMeasuredWidth(), mTitle.getMeasuredHeight());
    }

}
