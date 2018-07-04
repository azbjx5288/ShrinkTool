package com.shrinktool.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shrinktool.R;
import com.shrinktool.base.Preferences;
import com.shrinktool.base.thread.FutureListener;
import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleManager;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * Created by Alashi on 2015/12/22.
 */
public class DebugActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = DebugActivity.class.getSimpleName();

    private TextView logView;
    private SparseArray<Object> array = new SparseArray<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        logView = (TextView)findViewById(R.id.text);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> launch(array.keyAt(arg2)));
        loadData();
        findViewById(R.id.fab).setOnClickListener(this);
        GoldenAsiaApp.getNetStateHelper().addWeakListener(isConnected -> {
            String connect = GoldenAsiaApp.getNetStateHelper().isConnected()?
                    "网络连接：可以": "网络连接：无网络";
            logView.setText(connect);
        });
        String connect = GoldenAsiaApp.getNetStateHelper().isConnected()?
                "网络连接：可以": "网络连接：无网络";
        logView.setText(connect);

        TrendActivity.launch(this, 2);
    }

    private void loadData() {
        array.append(0, "登录");
        array.append(1, "清除session");
        array.append(2, "清除session&用户名&密码");
        array.append(3, "访问lotteryList接口");

        array.append(4, "rule");
    }

    private void launch(int key) {
        switch (key) {
            case 0:
                break;
            case 1:
                GoldenAsiaApp.getUserCentre().saveSession(null);
                break;
            case 2:
                GoldenAsiaApp.getUserCentre().saveSession(null);
                GoldenAsiaApp.getUserCentre().saveLoginInfo(null, null);
                GoldenAsiaApp.getUserCentre().setUserInfo(null);
                break;
            case 3:
                break;
            case 4:
                testRule();
                break;
            default: {
                Object object = array.get(key);
                if (object instanceof DataItem) {
                    FragmentLauncher.launch(DebugActivity.this, ((DataItem)object).fragment);
                } else {
                    Log.e(TAG, "launch: 未处理的列表项, key=" + key + ", v=" + object);
                }
                break;
            }
        }
    }

    private void testRule() {
        RuleSet ruleSet = RuleManager.getInstance()
                .getRuleSet(Path.fromString("/modularRatio"));
        ArrayList<String[]> list;
        list = ruleSet.onCreateRuleList(3);
        Log.d("XXX", "testRule: list.size=" + list.size());
        list = ruleSet.onCreateRuleList(4);
        Log.d("XXX", "testRule: list.size=" + list.size());
        list = ruleSet.onCreateRuleList(5);
        Log.d("XXX", "testRule: list.size=" + list.size());
    }

    private void testThreadPool() {
        GoldenAsiaApp.getThreadPool().submit(jc -> {
            Log.i(TAG, "run: ThreadPool Job running!");
            return null;
        }, (FutureListener<Void>) future -> Toast.makeText(DebugActivity.this, "线程池，回调到UI线程", Toast.LENGTH_SHORT).show(), true);
    }

    private void addItem(String text, Class<? extends Fragment> fragment) {
        array.append(array.size(), new DataItem(text, fragment));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            String name = Preferences.getString(this, "debug_last_launch_fragment", null);
            try {
                if (name != null && getClassLoader().loadClass(name) != null) {
                    FragmentLauncher.launch(this, name);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static class DataItem {
        String text;
        Class<? extends Fragment> fragment;

        public DataItem(String text, Class<? extends Fragment> fragment) {
            this.text = text;
            this.fragment = fragment;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(DebugActivity.this).inflate(android.R.layout.simple_list_item_1, null);
            }

            TextView view = (TextView) convertView.findViewById(android.R.id.text1);
            Object data = array.get(array.keyAt(position));
            view.setText(position +": "+ data.toString());

            return convertView;
        }
    }
}
