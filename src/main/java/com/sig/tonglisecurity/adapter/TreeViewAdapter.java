package com.sig.tonglisecurity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sig.tonglisecurity.R;

import java.util.ArrayList;
import java.util.List;

public class TreeViewAdapter extends BaseExpandableListAdapter {

    List<TreeNode> treeNodes = new ArrayList<TreeNode>();
    Context parentContext;

    public TreeViewAdapter(Context view) {
        parentContext = view;
    }

    public List<TreeNode> GetTreeNode() {
        return treeNodes;
    }

    public void UpdateTreeNode(List<TreeNode> nodes) {
        treeNodes = nodes;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return treeNodes.get(groupPosition).childs.get(childPosition);
    }

    public int getChildrenCount(int groupPosition) {
        return treeNodes.get(groupPosition).childs.size();
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parentContext).inflate(
                    R.layout.item_list_child, parent);
        }
        TextView name = (TextView) convertView.findViewById(R.id.help_content);
        name.setText(getChild(groupPosition, childPosition).toString());
        return convertView;
    }

    @SuppressLint("ResourceAsColor")
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parentContext).inflate(
                    R.layout.item_list_parent, parent);
        }
        TextView name = (TextView) convertView.findViewById(R.id.help_title);
        name.setText(getGroup(groupPosition).toString());
        ImageView img = (ImageView) convertView.findViewById(R.id.logo);

        img.setImageResource(R.drawable.arrow_up);
        if (!isExpanded) {
            img.setImageResource(R.drawable.arrow_down);
            convertView.setBackgroundResource(R.drawable.help_item_drawable);
        } else {
            convertView.setBackgroundResource(R.drawable.help_item_open_drawable);
        }
        return convertView;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public Object getGroup(int groupPosition) {
        return treeNodes.get(groupPosition).parent;
    }

    public int getGroupCount() {
        return treeNodes.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    static public class TreeNode {
        public Object parent;
        public List<Object> childs = new ArrayList<Object>();
    }
}
