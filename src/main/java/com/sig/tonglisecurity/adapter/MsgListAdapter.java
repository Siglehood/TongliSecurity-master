package com.sig.tonglisecurity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.sig.tonglisecurity.R;
import com.sig.tonglisecurity.bean.MessageBean;
import com.sig.tonglisecurity.interfaces.LoadNextPageListener;

import java.util.ArrayList;
import java.util.List;

public class MsgListAdapter extends BaseAdapter {
    public boolean isEnd = true;
    private List<MessageBean> list = new ArrayList<MessageBean>();
    private Context context;
    private LayoutInflater mInflater;
    private LoadNextPageListener loadNextPageListener;

    public MsgListAdapter(List<MessageBean> list, Context context, LoadNextPageListener loadNextPageListener) {
        super();
        this.list = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.loadNextPageListener = loadNextPageListener;
    }

    @Override
    public int getCount() {
        int size = list.size();
        if (!isEnd) {
            return size + 1;
        } else {
            return size;
        }
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
        ViewHolder holder;
        if (position >= list.size()) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_list_wait, null, true);
            convertView.setTag(null);
            convertView.setEnabled(false);
            loadNextPageListener.loadNextPage(1);
            return convertView;
        } else {
            if (convertView != null && convertView.getTag() != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_msg_list_view, null, true);
                holder.title = (TextView) convertView
                        .findViewById(R.id.title);
                holder.time = (TextView) convertView
                        .findViewById(R.id.time);
                convertView.setTag(holder);
            }
            holder.title.setText(list.get(position).title);
            if (list.get(position).hasRead) {
                holder.title.setTextColor(context.getResources().getColor(R.color.gray));
            } else {
                holder.title.setTextColor(context.getResources().getColor(R.color.black));
            }
            holder.time.setText(list.get(position).date);
            return convertView;
        }
    }

    private class ViewHolder {
        TextView title;
        TextView time;
    }
}
