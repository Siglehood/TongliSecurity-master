package com.sig.tonglisecurity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.FundTitleBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 全部基金
 */
@SuppressLint("ResourceAsColor")
public class Text_Adapter
        extends BaseAdapter {

    List<FundTitleBean> items = new ArrayList<FundTitleBean>();
    Context context;
    private LayoutInflater mInflater;
    private OnClickListener mOnClick;
    private boolean left = true;

    public Text_Adapter(Context context, List<FundTitleBean> items,
                        OnClickListener mOnClick, boolean left) {
        super();
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.items = items;
        this.mOnClick = mOnClick;
        this.left = left;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;     // ViewHolder 是内部类, 只有一个成员(name) TextView
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_list_txt_view, null);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);   // 这里的 name 是一个 TextView
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();  //getTag 返回: 一个 Object 引用
        }
        holder.name.setOnClickListener(mOnClick);
        holder.name.setTag(position);
        holder.name.setText(items.get(position).name);

        if (items.get(position).isSelected) {
            if (left) {
                convertView.setBackgroundResource(R.drawable.choose_s_left);
            } else {
                convertView.setBackgroundResource(R.drawable.choose_s_right);
            }
        } else {
            convertView.setBackgroundResource(R.drawable.choose_list_selector);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView name;
    }
}
