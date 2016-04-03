package com.sig.tonglisecurity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.Profit;
import com.sig.tonglisecurity.utils.ConfigUtil;


public class ExtensibleListItemViewforProfit extends ExtensibleListItemView {

    LinearLayout mTitle, mDetail;
    ImageView mTitle_img;
    Profit mData;
    TextView fund_name;
    TextView profit;
    TextView profit_rate;
    TextView capital_begin;
    TextView capital_end;
    TextView bqtzsd;

    public ExtensibleListItemViewforProfit(Context context) {
        super(context);
        init();
    }

    public ExtensibleListItemViewforProfit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(R.layout.item_account_profit_list, this, true);
        mTitle = (LinearLayout) this.findViewById(R.id.item_title_gain_loss);
        fund_name = (TextView) mTitle.findViewById(R.id.fund_name);
        profit = (TextView) mTitle.findViewById(R.id.profit);
        profit.setGravity(Gravity.RIGHT);
        profit_rate = (TextView) mTitle.findViewById(R.id.profit_rate);
        mTitle_img = (ImageView) mTitle.findViewById(R.id.title_image);
    }

    public void setData(Object obj) {
        mData = (Profit) obj;
        if (mData == null) {
            return;
        }
        fund_name.setText(mData.getFund_code());
        profit.setText(ConfigUtil.getFormatAmount(mData.getProfit()));
        profit_rate.setText(mData.getProfit_rate());
        if (capital_begin != null) {
            capital_begin.setText(ConfigUtil.getFormatAmount(mData.getCapital_begin()));
        }
        if (capital_end != null) {
            capital_end.setText(ConfigUtil.getFormatAmount(mData.getCapital_end()));
        }
        if (bqtzsd != null) {
            bqtzsd.setText(ConfigUtil.getFormatAmount(mData.getProfit()));
        }
    }

    @Override
    public void setData(Object data, boolean unfold) {
        if (data != null) {
            setData(data);
        }
        unfold(unfold);
    }

    private void unfold(boolean unfold) {
        if (unfold) {
            if (mDetail == null) {
                mDetail = (LinearLayout) ((ViewStub) findViewById(R.id.stub)).inflate();
                capital_begin = (TextView) mDetail.findViewById(R.id.capital_begin);
                capital_end = (TextView) mDetail.findViewById(R.id.capital_end);
                bqtzsd = (TextView) mDetail.findViewById(R.id.bqtzsd);
                mTitle.setBackgroundResource(R.drawable.account_listitem_selected);
                mTitle_img.setImageResource(R.drawable.ic_arrow_up);
                setData(mData);
                return;
            } else {
                mTitle.setBackgroundResource(R.drawable.account_listitem_selected);
                mTitle_img.setImageResource(R.drawable.ic_arrow_up);
                mDetail.setVisibility(VISIBLE);
            }
        } else {
            if (mDetail != null) {
                mTitle.setBackgroundResource(R.drawable.account_listitem_rest);
                mTitle_img.setImageResource(R.drawable.ic_arrow_down);
                mDetail.setVisibility(GONE);
            }
        }
    }

}
