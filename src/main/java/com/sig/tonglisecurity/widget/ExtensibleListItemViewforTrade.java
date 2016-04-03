package com.sig.tonglisecurity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.Trade;
import com.sig.tonglisecurity.utils.ConfigUtil;


public class ExtensibleListItemViewforTrade
        extends ExtensibleListItemView {

    LinearLayout mTitle, mDetail;
    ImageView mTitle_img;
    Trade mData;
    TextView fund_name;
    TextView time;
    TextView busi_name;
    TextView confirm_amount;

    TextView state;
    TextView channel;
    TextView fee_type;
    TextView fee;
    TextView confirm_amount_detail;
    TextView confirm_share;

    public ExtensibleListItemViewforTrade(Context context) {
        super(context);
        init();
    }

    public ExtensibleListItemViewforTrade(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                infService);
        li.inflate(R.layout.item_account_trade_list, this, true);
        mTitle = (LinearLayout) this.findViewById(R.id.item_title_gain_loss);
        fund_name = (TextView) mTitle.findViewById(R.id.fund_name);
        time = (TextView) mTitle.findViewById(R.id.time);
        busi_name = (TextView) mTitle.findViewById(R.id.profit);
        confirm_amount = (TextView) mTitle.findViewById(R.id.profit_rate);
        mTitle_img = (ImageView) mTitle.findViewById(R.id.title_image);
    }

    public void setData(Object obj) {
        mData = (Trade) obj;
        if (mData == null) {
            return;
        }
        fund_name.setText(mData.getFund_code());
        time.setText(mData.getTime());
        busi_name.setText(mData.getBusi_name());
        confirm_amount.setText(ConfigUtil.getFormatAmount(mData.getConfirm_amount()));
        if (mDetail != null) {
            state.setText(mData.getState());
            channel.setText(mData.getChannel());
            fee_type.setText(mData.getFee_type());
            fee.setText(mData.getFee());
            confirm_amount_detail.setText(ConfigUtil.getFormatAmount(mData.getConfirm_amount()));
            confirm_share.setText(ConfigUtil.getFormatAmount(mData.getConfirm_share()));
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
                mDetail = (LinearLayout) ((ViewStub) findViewById(R.id.stub))
                        .inflate();
                state = (TextView) mDetail.findViewById(R.id.account_state);
                channel = (TextView) mDetail.findViewById(R.id.account_channel);
                fee_type = (TextView) mDetail
                        .findViewById(R.id.account_fee_type);
                fee = (TextView) mDetail.findViewById(R.id.account_fee);
                confirm_amount_detail = (TextView) mDetail
                        .findViewById(R.id.account_confirm_amount);
                confirm_share = (TextView) mDetail
                        .findViewById(R.id.account_confirm_share);
                mTitle.setBackgroundResource(R.drawable.account_listitem_selected);
                mTitle_img.setImageResource(R.drawable.ic_arrow_up);
                setData(mData);
                return;
            }

            mTitle.setBackgroundResource(R.drawable.account_listitem_selected);
            mTitle_img.setImageResource(R.drawable.ic_arrow_up);
            mDetail.setVisibility(VISIBLE);
        } else {
            if (mDetail != null) {
                mTitle.setBackgroundResource(R.drawable.account_listitem_rest);
                mTitle_img.setImageResource(R.drawable.ic_arrow_down);
                mDetail.setVisibility(GONE);
            }
        }
    }

}
