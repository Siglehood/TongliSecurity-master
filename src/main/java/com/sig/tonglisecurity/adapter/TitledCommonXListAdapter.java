package com.sig.tonglisecurity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.FundBean;
import com.sig.tonglisecurity.utils.ConfigUtil;
import com.sig.tonglisecurity.widget.TitledCommonXListView;

import java.util.List;


/**
 * 菜单列表适配器
 */
public class TitledCommonXListAdapter extends BaseAdapter implements OnScrollListener {
    private List<FundBean> datas;
    private Context context;

    public TitledCommonXListAdapter(Context context, List<FundBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_fund_list_view, null);
            holder.mTitle = (LinearLayout) convertView
                    .findViewById(R.id.title);
            holder.fund_title_tab01 = (TextView) holder.mTitle.findViewById(R.id.fund_title_tab01);
            holder.fund_title_tab02 = (TextView) holder.mTitle.findViewById(R.id.fund_title_tab02);
            holder.fund_title_tab03 = (TextView) holder.mTitle.findViewById(R.id.fund_title_tab03);

            holder.fund_type = (TextView) convertView
                    .findViewById(R.id.fund_type);
            holder.fund_name = (TextView) convertView
                    .findViewById(R.id.fund_name);
            holder.value = (TextView) convertView
                    .findViewById(R.id.value);
            holder.date = (TextView) convertView
                    .findViewById(R.id.date);
            holder.rate = (TextView) convertView
                    .findViewById(R.id.rate);
        }
        holder.fund_type.setText(ConfigUtil.getFundTypeTxt(datas.get(position).type));

        if (datas.get(position).is_fav.equals("0")) {
            holder.fund_type.setBackgroundResource(R.drawable.type_bg);
        } else if (datas.get(position).is_fav.equals("1")) {
            holder.fund_type.setBackgroundResource(R.drawable.type_bg_s);
        } else {
            holder.fund_type.setBackgroundResource(R.drawable.type_bg);
        }
        holder.fund_name.setText(datas.get(position).fund_name);
       /* if(datas.get(position).fund_name.length() > 8){
            holder.fund_name.setTextSize(12f);
        }else{
        	holder.fund_name.setTextSize(14f);
        }*/
        if (datas.get(position).type.equals("6")) {//只有6类型才有七日年化
            holder.date.setText(datas.get(position).rate_thounds_date);
            holder.value.setText(ConfigUtil.formatDouble(datas.get(position).rate_thounds, 4));
        } else {
            holder.date.setText(datas.get(position).netvalue_date);
            holder.value.setText(ConfigUtil.formatDouble(datas.get(position).netvalue, 4));
        }
        if (datas.get(position).type.equals("6")) {
            holder.rate.setText(ConfigUtil.formatDouble(datas.get(position).show_value, 2) + "%");
        } else {
            holder.rate.setText(ConfigUtil.formatDouble(datas.get(position).show_value, 2) + "%");
        }

        try {
            if ((Double.parseDouble(datas.get(position).show_value)) >= 0) {
                holder.rate.setBackgroundResource(R.drawable.red_bg);
            } else {
                holder.rate.setBackgroundResource(R.drawable.green_bg);
            }
        } catch (Exception e) {
            holder.rate.setBackgroundResource(R.drawable.red_bg);
        }

        holder.fund_title_tab01.setText(datas.get(position).title01);
        holder.fund_title_tab02.setText(datas.get(position).title02);
        holder.fund_title_tab03.setText(datas.get(position).title03);
        if (position == 0) {
            holder.mTitle.setVisibility(View.VISIBLE);
        } else if (position < getCount() && !datas.get(position).title02.equals(datas.get(position - 1).title02)) {
            holder.mTitle.setVisibility(View.VISIBLE);
        } else {
            holder.mTitle.setVisibility(View.GONE);
        }
        if (datas.get(position).fund_code.equals("")) {
            convertView.setVisibility(View.GONE);
        } else {
            convertView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (getCount() > 1) {
            if (firstVisibleItem > 0) {
                if (!datas.get(firstVisibleItem - 1).title02.equals(datas.get(firstVisibleItem).title02)) {
                    ((TitledCommonXListView) view).moveTitle();
                } else {
                    ((TitledCommonXListView) view).updateTitle(datas.get(firstVisibleItem - 1).type, datas.get(firstVisibleItem - 1).title03);
                }
            } else {
                ((TitledCommonXListView) view).updateTitle(null, null);
            }
        } else {
            ((TitledCommonXListView) view).updateTitle(null, null);
        }
    }

    private class ViewHolder {
        LinearLayout mTitle;
        TextView fund_type;
        TextView fund_name;
        TextView value;
        TextView date;
        TextView rate;
        TextView fund_title_tab01;
        TextView fund_title_tab02;
        TextView fund_title_tab03;
    }
}
