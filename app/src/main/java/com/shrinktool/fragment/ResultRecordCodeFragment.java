package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 方案详情的号码页
 * Created by Alashi on 2017/1/19.
 */

public class ResultRecordCodeFragment extends BaseFragment {
    //号码可能太长，不能同Bundle传输
    private static String srcCodeString;

    @Bind(R.id.list) ListView listView;
    private MyAdapter myAdapter = new MyAdapter();
    private String[] code;

    public static void launch(BaseFragment fragment, String codeString) {
        ResultRecordCodeFragment.srcCodeString = codeString;
        fragment.launchFragment(ResultRecordCodeFragment.class, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, "号码", R.layout.result_record_code_list);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        code = ResultRecordCodeFragment.srcCodeString.split("\n");
        listView.setAdapter(myAdapter);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return code.length;
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_pick_code_ssq, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.index.setText(String.valueOf(position + 1));
            int childCount = holder.ballList.getChildCount();
            String[] codes = code[position].split(",");
            for (int i = 0; i < childCount; i++) {
                TextView ball = (TextView) holder.ballList.getChildAt(i);
                if (i < codes.length) {
                    ball.setVisibility(View.VISIBLE);
                    ball.setText(codes[i]);
                } else {
                    ball.setVisibility(View.GONE);
                }
            }
            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.index)
        TextView index;
        @Bind(R.id.openCodeLayout)
        LinearLayout ballList;
        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
            convertView.setTag(this);
        }
    }
}
