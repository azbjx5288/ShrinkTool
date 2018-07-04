package com.shrinktool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.WebViewActivity;
import com.shrinktool.base.net.GsonHelper;
import com.shrinktool.game.GameConfig;
import com.shrinktool.material.RefiningCart;
import com.shrinktool.material.SpecialHelper;
import com.shrinktool.material.SscSpecialHelper;
import com.shrinktool.material.SscSpecialHelper2;
import com.shrinktool.material.SsqSpecialHelper;
import com.shrinktool.rule.ActivityRuleConfig;
import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleManager;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleRecord;
import com.shrinktool.rule.RuleRecordDialog;
import com.shrinktool.rule.RuleRecordHelper;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 规则选择页面
 * Created by Alashi on 2016/5/26.
 */
public class RuleSettingFragment extends BaseFragment {
    private static final String TAG = "RuleSettingFragment";

    @Bind(R.id.list) ListView listView;

    private LayoutInflater inflater;

    private MyAdapter myAdapter;
    private SparseArray<RuleButtonHolder> holderMap = new SparseArray<>();
    private ArrayList<DisplayData> displayDatas = new ArrayList<>();
    private RefiningCart refiningCart;
    private int numberType;
    private SpecialHelper specialHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflateView(inflater, container, "选号过滤", R.layout.rule_setting_fragment);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refiningCart = RefiningCart.getInstance();
        numberType = GameConfig.getNumberType(refiningCart.getLottery());
        if (refiningCart.isEmpty()) {
            showToast("未选择号码");
            getActivity().finish();
            return;
        }

        addMenuItem("方案", this::showRecord);
        addMenuItem("说明", this::showHelp);
        int numberCount = 0;
        switch (numberType){
            case RuleSet.TYPE_1_11_SDRX5:
                specialHelper = new SscSpecialHelper2(this, refiningCart);
                break;
            case RuleSet.TYPE_0_9_SXZX:
            case RuleSet.TYPE_WXZX:
                numberCount = refiningCart.getTicket().getCodes().split(",").length;
                specialHelper = new SscSpecialHelper(this, refiningCart, numberCount);
                break;
            case RuleSet.TYPE_SSQ:
                numberCount = 7;
                specialHelper = new SsqSpecialHelper(this);
                break;
        }

        listView.addHeaderView(specialHelper.getHeaderView());

        buildDisplayData(numberCount);
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
    }

    private View.OnClickListener onRuleButtonClick = (v)-> {
        onRuleClick((int) v.getTag(R.drawable.menu_icon_xuanhao), (int) v.getTag(R.drawable.menu_icon_faxian));
    };

    private void onRuleClick(int pos, int index) {
        DisplayData displayData = displayDatas.get(pos);
        Log.i(TAG, "onRuleClick: " + displayData.rule[index]);
        RuleButtonHolder holder = holderMap.get(displayData.index);
        List<String> ruleList = holder.rule;
        if (ruleList.contains(displayData.rule[index])) {
            ruleList.remove(displayData.rule[index]);
            displayData.selected[index] = false;
        } else {
            if (displayData.unlimited[index]) {
                ruleList.clear();
                for (DisplayData data: holder.all) {
                    for (int i = 0; i < 4; i++) {
                        data.selected[i] = false;
                    }
                }
            } else {
                holder.unlimited.selected[0] = false;
                ruleList.remove(holder.unlimited.rule[0]);
            }

            ruleList.add(displayData.rule[index]);
            displayData.selected[index] = true;
        }

        myAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.submit)
    public void onSubmitClick() {
        List<ArrayList<String>> rules = new ArrayList<>();

        if (!bindSpecialRule(rules)) {
            showToast("胆码数量不符合要求，请选择更多的胆码，或改变“胆码出现个数”");
            return;
        }

        for (int i = 0, size = holderMap.size(); i < size; i++) {
            RuleButtonHolder holder = holderMap.get(i);
            if (!holder.rule.isEmpty() && !(holder.rule.get(0).endsWith(RuleObject.UNLIMITED))) {
                rules.add(holder.rule);
            }
        }

        if (rules.size() > 0) {
            refiningCart.setRuleList(rules);
            refiningCart.setRuleRecord(buildRuleRecord());
            launchFragmentForResult(ResultFragment.class, null, 0);
        } else {
            showToast("没有选择可用的规则");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            getActivity().finish();
        }
    }

    @OnClick(R.id.save_rule)
    public void onSaveRuleClick() {
        EditText editText = new EditText(getContext());
        editText.setHint("输入方案名词");
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("保存方案").setView(editText).setNegativeButton("取消", null)
                .setPositiveButton("保存", (d, w)-> saveOut(editText.getText().toString())).show();
    }

    private void saveOut(String name) {
        if (TextUtils.isEmpty(name)) {
            name = "新方案";
        }
        RuleRecord record = buildRuleRecord();
        record.setName(name);
        Log.i(TAG, "onSaveRuleClick: " + GsonHelper.toJson(record));

        RuleRecordHelper.save(getContext(), record);

        showToast("已保存过滤方案");
    }

    private RuleRecord buildRuleRecord() {
        RuleRecord record = new RuleRecord();
        record.setLotteryId(refiningCart.getLottery().getLotteryId());
        record.setMethodId(refiningCart.getTicket().getChooseMethod().getMethodId());
        record.setIssue(refiningCart.getIssue());
        if (refiningCart.getAssist() != null) {
            record.setAssist(GsonHelper.toJson(refiningCart.getAssist()));
        }

        record.setSpecial(specialHelper.saveOut());

        ArrayList<String> rules = new ArrayList<>();
        for (int i = 0, size = holderMap.size(); i < size; i++) {
            RuleButtonHolder holder = holderMap.get(i);
            if (!holder.rule.isEmpty() && !(holder.rule.get(0).endsWith(RuleObject.UNLIMITED))) {
                rules.addAll(holder.rule);
            }
        }
        record.setRuleList(rules);
        return record;
    }

    private void showHelp(View v){
        WebViewActivity.start(getActivity(), GameConfig.getRuleHelp(refiningCart.getLottery().getLotteryId()));
    }

    private void showRecord(View v){
        int lotteryId = refiningCart.getLottery().getLotteryId();
        int methodId = refiningCart.getTicket().getChooseMethod().getMethodId();

        RuleRecordDialog dialog = new RuleRecordDialog(getContext(), lotteryId, methodId,
                this::useRuleRecord);
        if (dialog.isSupportShow()) {
            dialog.show();
        } else {
            showToast("当前玩法没有已保存的方案");
        }
    }

    private void useRuleRecord(RuleRecord ruleRecord) {
        specialHelper.useRuleRecord(ruleRecord.getSpecial());

        ArrayList<String> rules = ruleRecord.getRuleList();
        for (int i = 0, size = holderMap.size(); i < size; i++) {
            RuleButtonHolder holder = holderMap.get(i);
            holder.rule.clear();
            for (DisplayData displayData: holder.all) {
                for (int j = 0; j < 4; j++) {
                    displayData.selected[j] = rules.contains(displayData.rule[j]);
                    if (displayData.selected[j]) {
                        holder.rule.add(displayData.rule[j]);
                    }
                }
            }
        }
        myAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.del_reset)
    public void onResetClick() {
        specialHelper.reset();

        for (int i = 0, size = holderMap.size(); i < size; i++) {
            RuleButtonHolder holder = holderMap.get(i);
            holder.rule.clear();
            for (DisplayData displayData: holder.all) {
                displayData.selected[0] = false;
                displayData.selected[1] = false;
                displayData.selected[2] = false;
                displayData.selected[3] = false;
            }
        }
        myAdapter.notifyDataSetChanged();
    }

    private boolean bindSpecialRule(List<ArrayList<String>> rules) {
        return specialHelper.getRule(rules);
    }

    private void buildDisplayData(int numberCount){
        List<ActivityRuleConfig.Info> ruleSets = ActivityRuleConfig.getRuleType(refiningCart, numberType);

        if (ruleSets == null) {
            Log.w(TAG, "buildDisplayData: 不支持的配置类型");
            return;
        }

        int index = 0;
        RuleManager ruleManager = RuleManager.getInstance();
        for (ActivityRuleConfig.Info info: ruleSets) {
            RuleSet ruleSet = ruleManager.getRuleSet(Path.fromString(info.rule));
            RuleButtonHolder holder = new RuleButtonHolder();
            holderMap.put(index, holder);
            DisplayData displayData = new DisplayData();
            displayData.type = 0;
            displayData.text = new String[]{ ruleSet.getName() + ruleSet.getHint()};
            displayDatas.add(displayData);
            ArrayList<String[]> ruleList = ruleSet.getRuleList(numberCount);
            for (int i = 0, size = ruleList.size(); i < size; i += info.buttonCount) {
                displayData = new DisplayData();
                displayData.type = info.buttonCount;
                displayData.text = new String[info.buttonCount];
                displayData.rule = new String[info.buttonCount];
                displayData.selected = new boolean[info.buttonCount];
                displayData.unlimited = new boolean[info.buttonCount];
                displayData.index = index;
                for (int j = 0; j < info.buttonCount; j++) {
                    if (i + j < size) {
                        String[] tmp = ruleList.get(i + j);
                        displayData.rule[j] = tmp[0];
                        displayData.text[j] = tmp[1];
                        displayData.selected[j] = false;
                        displayData.unlimited[j] = tmp[0].endsWith(RuleObject.UNLIMITED);
                        if (displayData.unlimited[j]){
                            holder.unlimited = displayData;
                        }
                    }
                }
                holder.all.add(displayData);
                displayDatas.add(displayData);
            }
            index++;
        }
    }

    private class DisplayData {
        private int type;//标题或按钮列
        private String[] text;
        private String[] rule;
        private boolean[] unlimited;
        private boolean[] selected;
        public int index;
    }

    private class RuleButtonHolder {
        private DisplayData unlimited;
        private ArrayList<DisplayData> all = new ArrayList<>();
        private ArrayList<String> rule = new ArrayList<>();
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getViewTypeCount() {
            return 7;
        }

        @Override
        public int getItemViewType(int position) {
            return displayDatas.get(position).type;
        }

        @Override
        public int getCount() {
            return displayDatas.size();
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
            int viewType = getItemViewType(position);
            if (null == convertView) {
                holder = new Holder();
                if (viewType == 0) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.rule_item_title, parent, false);
                    holder.textView = (TextView) convertView.findViewById(R.id.title);
                } else {
                    LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.rule_item_button, parent, false);
                    holder.button = new Button[viewType];
                    for (int i = 0, childCount = layout.getChildCount(); i < childCount; i++) {
                        if (i < viewType) {
                            holder.button[i] = (Button) layout.getChildAt(i);
                            holder.button[i].setOnClickListener(onRuleButtonClick);
                        } else {
                            layout.getChildAt(i).setVisibility(View.GONE);
                        }
                    }
                    convertView = layout;
                }
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            DisplayData displayData = displayDatas.get(position);
            if (holder.textView != null) {
                holder.textView.setText(displayData.text[0]);
            } else {
                for (int i = 0; i < viewType; i++) {
                    if (displayData.text[i] != null) {
                        holder.button[i].setVisibility(View.VISIBLE);
                        holder.button[i].setText(displayData.text[i]);
                        holder.button[i].setSelected(displayData.selected[i]);
                        holder.button[i].setTag(R.drawable.menu_icon_xuanhao, position);
                        holder.button[i].setTag(R.drawable.menu_icon_faxian, i);
                    } else {
                        holder.button[i].setVisibility(View.INVISIBLE);
                    }
                }
            }

            return convertView;
        }
    }

    private class Holder {
        private TextView textView;
        private Button[] button;
    }
}
