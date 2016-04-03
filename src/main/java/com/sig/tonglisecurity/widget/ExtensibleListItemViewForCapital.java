package com.sig.tonglisecurity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.Capital;
import com.sig.tonglisecurity.bean.SalesChannel;
import com.sig.tonglisecurity.utils.ConfigUtil;

import java.util.List;

public class ExtensibleListItemViewForCapital extends ExtensibleListItemView {

    private LinearLayout mTitle, mDetail;
    private ImageView mTitle_img;
    private Capital mData;
    private TextView fund_name;
    private TextView fund_share;
    private TextView fund_capital;

    public ExtensibleListItemViewForCapital(Context context) {
        super(context);
        init();
    }

    public ExtensibleListItemViewForCapital(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                infService);
        li.inflate(R.layout.item_account_capital_list, this, true);
        mTitle = (LinearLayout) this.findViewById(R.id.item_title_gain_loss);
        fund_name = (TextView) mTitle.findViewById(R.id.fund_name);
        fund_share = (TextView) mTitle.findViewById(R.id.profit);
        fund_share.setGravity(Gravity.RIGHT);
        fund_capital = (TextView) mTitle.findViewById(R.id.profit_rate);
        mTitle_img = (ImageView) mTitle.findViewById(R.id.title_image);
    }

    public void setData(Object obj) {
        mData = (Capital) obj;
        if (mData == null) {
            return;
        }
        fund_name.setText(mData.getFund_code());
        fund_share.setText(ConfigUtil.getFormatAmount(mData.getFund_share()));
        fund_capital.setText(ConfigUtil.getFormatAmount(mData.getFund_capital()));

        if (mDetail != null) {
            updateDetailView();
        }
    }

    private void updateDetailView() {
        mDetail.removeAllViews();
        List<SalesChannel> salesChannels = mData.getSalsChannels();
        for (int i = 0; i < salesChannels.size(); i++) {
            View exItem = LayoutInflater.from(this.getContext()).inflate(
                    R.layout.item_account_capital_list_detail_item, null);
            String name = salesChannels.get(i).getFund_channel();
            ((TextView) exItem.findViewById(R.id.name)).setText(name);
            ((TextView) exItem.findViewById(R.id.share)).setText(ConfigUtil.getFormatAmount(salesChannels.get(i).getFund_share()));
            ((TextView) exItem.findViewById(R.id.capitals)).setText(ConfigUtil.getFormatAmount(salesChannels.get(i).getFund_capital()));
            ImageView logo = ((ImageView) exItem.findViewById(R.id.logoimg));
            logo.setVisibility(View.VISIBLE);
            if (name.contains("icbc")) {
                logo.setImageResource(R.drawable.ic_icbc);
            } else if (name.contains("gd")) {
                logo.setImageResource(R.drawable.ic_gd);
            } else if (name.contains("gf")) {
                logo.setImageResource(R.drawable.ic_gf);
            } else if (name.contains("hftx")) {
                logo.setImageResource(R.drawable.ic_hftx);
            } else if (name.contains("js")) {
                logo.setImageResource(R.drawable.ic_js);
            } else if (name.contains("jt")) {
                logo.setImageResource(R.drawable.ic_jt);
            } else if (name.contains("jh")) {
                logo.setImageResource(R.drawable.ic_jh);
            } else if (name.contains("nj")) {
                logo.setImageResource(R.drawable.ic_nj);
            } else if (name.contains("ny")) {
                logo.setImageResource(R.drawable.ic_ny);
            } else if (name.contains("pf")) {
                logo.setImageResource(R.drawable.ic_pf);
            } else if (name.contains("shns")) {
                logo.setImageResource(R.drawable.ic_shns);
            } else if (name.contains("sh")) {
                logo.setImageResource(R.drawable.ic_sh);
            } else if (name.contains("icbc")) {
                logo.setImageResource(R.drawable.ic_icbc);
            } else if (name.contains("tltx")) {
                logo.setImageResource(R.drawable.ic_tltx);
            } else if (name.contains("xy")) {
                logo.setImageResource(R.drawable.ic_xy);
            } else if (name.contains("cs")) {
                logo.setImageResource(R.drawable.ic_cs);
            } else if (name.contains("cmbc")) {
                logo.setImageResource(R.drawable.ic_cmbc);
            } else if (name.contains("bc")) {
                logo.setImageResource(R.drawable.ic_bc);
            } else if (name.contains("zfb")) {
                logo.setImageResource(R.drawable.ic_zfb);
            } else if (name.contains("zgms")) {
                logo.setImageResource(R.drawable.ic_zgms);
            } else if (name.contains("zgpa")) {
                logo.setImageResource(R.drawable.ic_zgpa);
            } else if (name.contains("zx")) {
                logo.setImageResource(R.drawable.ic_zx);
            } else {
                logo.setVisibility(View.INVISIBLE);
            }
            if (i == 0) {
                exItem.setBackgroundResource(R.drawable.account_listitem_ex_first);
            }
            mDetail.addView(exItem);
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
                mTitle.setBackgroundResource(R.drawable.account_listitem_selected);
                mTitle_img.setImageResource(R.drawable.ic_arrow_up);
                setData(mData);
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
