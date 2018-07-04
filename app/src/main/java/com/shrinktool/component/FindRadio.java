package com.shrinktool.component;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.shrinktool.app.BaseFragment;

/**
 * 发现页的各种TAB
 * Created by Alashi on 2017/2/10.
 */

public abstract class FindRadio {
    protected BaseFragment fragment;
    protected SwipeRefreshLayout refreshLayout;
    protected BaseAdapter adapter;

    public FindRadio(BaseFragment fragment, SwipeRefreshLayout refreshLayout, BaseAdapter adapter) {
        this.fragment = fragment;
        this.refreshLayout = refreshLayout;
        this.adapter = adapter;
    }

    public abstract void reload();
    public abstract void onItemClick(int position);
    public abstract int getCount();
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
