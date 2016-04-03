package com.sig.tonglisecurity.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sig.tonglisecurity.utils.LogUtil;
import com.sig.tonglisecurity.widget.ExtensibleListItemView;
import com.sig.tonglisecurity.widget.ExtensibleListItemViewForCapital;
import com.sig.tonglisecurity.widget.ExtensibleListItemViewforProfit;
import com.sig.tonglisecurity.widget.ExtensibleListItemViewforTrade;

import java.util.List;

/**
 * XListView 适配器
 */
public class ExtensibleListViewAdapter extends BaseAdapter {

    public static final String TAG = "ExtensibleListViewAdapter";

    private List<Object> mList;
    private int unfoldPosition = -1;
    private AdapterType mType;

    public ExtensibleListViewAdapter(List<Object> list, AdapterType type) {

        LogUtil.i(TAG, "FundDetailAdapter 构造器");

        mList = list;
        mType = type;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            ExtensibleListItemView item = null;
            switch (mType) {
                case Type_Capital:
                    item = new ExtensibleListItemViewForCapital(parent.getContext());
                    break;
                case Type_Profit:
                    item = new ExtensibleListItemViewforProfit(parent.getContext());
                    break;
                case Type_Trade:
                    item = new ExtensibleListItemViewforTrade(parent.getContext());
                    break;
                default:
                    break;
            }
            convertView = item;
        }
        ((ExtensibleListItemView) convertView).setData(mList.get(position), position == unfoldPosition);
        return convertView;
    }

    public void setUnfoldPosition(int position) {
        if (this.unfoldPosition == position) {
            this.unfoldPosition = -1;
        } else {
            this.unfoldPosition = position;
        }
        this.notifyDataSetChanged();
    }

    public enum AdapterType {
        Type_Capital,
        Type_Trade,
        Type_Profit
    }
}
