package com.shrinktool.rule;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shrinktool.R;

import java.util.ArrayList;

/**
 * 用于显示“选择方案”
 * Created by Alashi on 2016/6/2.
 */
public class RuleRecordDialog extends AlertDialog {
    public interface OnPickListener{
        void onPicked(RuleRecord ruleRecord);
    }

    private ArrayList<RuleRecord> list;
    private OnPickListener listener;
    private MyAdapter myAdapter;

    public RuleRecordDialog(Context context, int lotteryId, int methodId, OnPickListener listener) {
        super(context);
        this.listener = listener;
        setTitle("选择过滤方案");
        list = RuleRecordHelper.getRuleRecords(getContext(), lotteryId, methodId);
        ListView listView = new ListView(context);
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        setButton(BUTTON_NEGATIVE, "取消", (w, v) -> {dismiss();});
        setView(listView);
    }

    public boolean isSupportShow() {
        return list.size() > 0;
    }

    private View.OnClickListener onDelClickListener = (v) ->{
        RuleRecord record = list.remove((int) v.getTag());
        RuleRecordHelper.delete(getContext(), record);
        if (list.size() == 0){
            dismiss();
        } else {
            myAdapter.notifyDataSetChanged();
        }
    };

    private View.OnClickListener onItemClickListener = (v) ->{
        listener.onPicked(list.get((int) v.getTag()));
        dismiss();
    };

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.rule_record, parent, false);
                holder = new Holder();
                holder.name = (TextView) layout.findViewById(R.id.name);
                holder.del = (ImageButton) layout.findViewById(R.id.del);
                holder.name.setOnClickListener(onItemClickListener);
                holder.del.setOnClickListener(onDelClickListener);
                convertView = layout;
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            RuleRecord record = list.get(position);
            holder.name.setText(record.getName());
            holder.name.setTag(position);
            holder.del.setTag(position);
            return convertView;
        }

        private class Holder {
            private TextView name;
            private ImageButton del;
        }
    }
}
