package com.shrinktool.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.app.GoldenLogin;
import com.shrinktool.base.net.GsonHelper;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.SavePlanCommand;
import com.shrinktool.game.GameConfig;
import com.shrinktool.material.RefiningCart;
import com.shrinktool.record.ResultFormater;
import com.shrinktool.rule.Plan;
import com.shrinktool.rule.ResultRecord;
import com.shrinktool.rule.RuleExecuter;
import com.shrinktool.rule.RuleSet;
import com.shrinktool.user.UserCentre;

import java.io.FileOutputStream;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 使用规则进行过滤，并显示结果。RuleSet内为“并集”，RuleSet间为“交集”，
 * Created by Alashi on 2016/5/9.
 */
public class ResultFragment extends BaseFragment implements RuleExecuter.OnResultLister {
    private static final String TAG = ResultFragment.class.getSimpleName();

    private static final int REQUEST_LOGIN_CODE = 1;

    @Bind(R.id.resultPickAll) TextView resultPickAll;
    @Bind(R.id.filterTotal) TextView totalCount;
    @Bind(R.id.count) TextView count;
    @Bind(R.id.ratio) TextView ratio;
    @Bind(R.id.pick) TextView pick;
    @Bind(R.id.list) ListView listView;
    @Bind(R.id.bottom) View bottom;

    /** 由选号产生的待过滤的号码 */
    private int[][] srcCode;
    private int[][] result;
    private boolean[] pickIndex;
    private int pickCount;
    private RefiningCart refiningCart;
    private int lotteryId;
    private int numberType;

    private UserCentre userCentre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflateView(inflater, container, "过滤结果", R.layout.results_fragment);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userCentre = GoldenAsiaApp.getUserCentre();
        listView.setAdapter(new MyAdapter());
        refiningCart = RefiningCart.getInstance();
        lotteryId = refiningCart.getLotteryId();
        numberType = GameConfig.getNumberType(refiningCart.getLottery());
        showProgress("正在过滤...");

        GoldenAsiaApp.getThreadPool().submit(jc -> {
            srcCode = refiningCart.buildNumbers();
            return null;
        }, future -> new RuleExecuter(srcCode, refiningCart, this).execute(), true);
    }

    @Override
    public void onResult(int[][] result) {
        if (isFinishing()) {
            return;
        }
        this.result = result;
        pickIndex = new boolean[result.length];
        getActivity().runOnUiThread(()->{
            hideProgress();
            totalCount.setText(srcCode.length + "注");
            count.setText(result.length + "注");
            ratio.setText(100 * result.length / srcCode.length + "%");
            updateUI();
            if (result.length > 0) {
                bottom.setVisibility(View.VISIBLE);
            }
        });
    }

    private void copyOrSave(boolean isCopy) {
        if (pickCount == 0) {
            showToast("未选择号码");
            return;
        }
        showProgress(isCopy? "正在复制" : "正在保存");
        GoldenAsiaApp.getThreadPool().submit(jc -> {
            int[][] pickNum = buildPickNumber();
            StringBuilder buffer = new StringBuilder(pickNum.length * (pickNum[0].length + 1) * 2);
            for (int[] nums : pickNum) {
                for (int i = 0; i < nums.length; i++) {
                    int code = nums[i];
                    if (code < 10) {
                        if (numberType == RuleSet.TYPE_1_11_SDRX5 || numberType == RuleSet.TYPE_SSQ) {
                            buffer.append("0");
                        }
                    }
                    buffer.append(code);
                    if (i != nums.length - 1) {
                        buffer.append(",");
                    }
                }
                buffer.append("\n");
            }
            if (isCopy) {
                return buffer.toString();
            } else {
                return com.shrinktool.component.Utils.writeOutFilterResult(getContext(),
                        refiningCart.getLottery().getCname(), buffer.toString());
            }
        }, future -> {
            hideProgress();
            if (isCopy) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("过滤结果", future.get());
                clipboard.setPrimaryClip(clip);
                showToast("已复制到粘贴板");
            } else {
                String path = future.get();
                if (path != null) {
                    showToast("已经保存到：" + path, Toast.LENGTH_LONG);
                } else {
                    showToast("存储不可写，导出失败");
                }
            }
        }, true);
    }

    @OnClick({R.id.resultSave2Server, R.id.resultCopy, R.id.resultSaveOut, R.id.resultPickAll})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.resultSave2Server:
                if (userCentre.isLogin()) {
                    save2server();
                } else {
                    startActivityForResult(new Intent(getContext(), GoldenLogin.class), REQUEST_LOGIN_CODE);
                }
                break;

            case R.id.resultCopy:
                copyOrSave(true);
                break;
            case R.id.resultSaveOut:
                copyOrSave(false);
                break;
            case R.id.resultPickAll:
                if (pickCount != pickIndex.length) {
                    pickCount = pickIndex.length;
                    Arrays.fill(pickIndex, true);
                } else {
                    pickCount = 0;
                    Arrays.fill(pickIndex, false);
                }
                updateUI();
                break;
        }
    }

    @OnItemClick(R.id.list)
    public void onItemClick(int position) {
        if (pickIndex[position]) {
            pickCount--;
            pickIndex[position] = false;
        } else {
            pickCount++;
            pickIndex[position] = true;
        }
        updateUI();
    }

    private void updateUI(){
        if (pickCount == pickIndex.length) {
            resultPickAll.setText("取消全选");
            Drawable drawable = getResources().getDrawable(R.drawable.yc_gljg_check_box_checked);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            resultPickAll.setCompoundDrawables(drawable, null, null, null);
        } else {
            resultPickAll.setText("全选");
            Drawable drawable = getResources().getDrawable(R.drawable.yc_gljg_check_box_normal);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            resultPickAll.setCompoundDrawables(drawable, null, null, null);
        }
        pick.setText(Html.fromHtml(String.format("共<font color=#FFA423>%d</font>注<font " +
                "color=#FFA423>%d</font>元", pickCount, 2 * pickCount)));
        ((MyAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " + requestCode + ", " + userCentre.isLogin());
        if (requestCode == REQUEST_LOGIN_CODE && userCentre.isLogin()) {
            save2server();
        }
    }

    private Plan createPlanData() {
        ResultRecord record = new ResultRecord();
        record.setLottery(refiningCart.getLottery());
        record.setTicket(refiningCart.getTicket());
        record.setRuleRecord(refiningCart.getRuleRecord());
        record.setOnline(!TextUtils.isEmpty(refiningCart.getIssue()));
        if (false) {
            //不保存文件
            String fileName = "result_" + System.currentTimeMillis();
            record.setResultFile(fileName);
            try {
                FileOutputStream os = getContext().openFileOutput(fileName,
                        Context.MODE_PRIVATE);
                os.write(GsonHelper.toJson(result).getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                showToast("保存失败");
                return null;
            }
        }

        Plan plan = new Plan();
        plan.setVersion(ResultRecord.VERSION);//****重点****
        plan.setLotteryId(record.getLottery().getLotteryId());
        plan.setIssue(record.getRuleRecord().getIssue());
        plan.setMethodId(record.getTicket().getChooseMethod().getMethodId());
        plan.setRecord(GsonHelper.toJson(record));
        plan.setFilterTotal(srcCode.length);
        plan.setFilterCount(result.length);
        return plan;
    }

    private int[][] buildPickNumber(){
        int[][] pickNum = new int[pickCount][];
        for (int i = 0, j = 0; i < result.length && j < pickCount; i++) {
            if (pickIndex[i]) {
                pickNum[j++] = result[i];
            }
        }
        return pickNum;
    }

    private void save2server() {
        int[][] pickNum = buildPickNumber();
        if (pickNum == null || pickNum.length == 0) {
            showToast("没有注单，不能保存");
            return;
        } else if (pickNum.length > 10000) {
            showToast("注单太多，不能保存");
            return;
        }

        Plan plan = createPlanData();
        if (plan == null) {
            return;
        }
        plan.setPickCount(pickNum.length);
        SavePlanCommand command = new SavePlanCommand();
        command.setLotteryId(plan.getLotteryId());
        command.setMethodId(plan.getMethodId());
        command.setIssue(plan.getIssue());
        command.setPlan(plan);
        //TODO:注数太多的时候，这里的getSubmitString会慢，需要优化
        command.setResult(new ResultFormater(pickNum, plan.getLotteryId(), plan.getMethodId()).getSubmitString());
        command.setAmount(pickNum.length * 2);

        executeCommand(command, restCallback);
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            Integer filterId = (Integer) response.getData();
            showSaveOK(filterId);
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (state == RestRequest.RUNNING) {
                showProgress("正在保存...");
            } else {
                hideProgress();
            }
        }
    };

    private void showSaveOK(int filterId) {
        new AlertDialog.Builder(getContext())
                .setTitle("保存成功")
                .setMessage("保存成功，请不要忘记购买，以免错过中奖！")
                .setNegativeButton("继续选号", (dialogInterface, i) -> {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                })
                .setPositiveButton("查看过滤方案详情", (dialogInterface1, i1) -> {
                    ResultRecordFragment.launch(this, filterId);
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                })
                .show();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return result == null? 0 : result.length;
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
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.result_item, parent, false);
                holder = new ViewHolder(convertView, lotteryId);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.pick.setImageResource(pickIndex[position]?
                    R.drawable.yc_gljg_check_box_checked : R.drawable.yc_gljg_check_box_normal);
            holder.index.setText(String.valueOf(position + 1));
            if (lotteryId == 100) {
                String red = "";
                int code;
                for (int i = 0; i < 6; i++) {
                    code = result[position][i];
                    if (code < 10) {
                        red += "0" + code + " ";
                    } else {
                        red += code + " ";
                    }
                }
                holder.codeRed.setText(red);
                code = result[position][6];
                if (code < 10) {
                    holder.codeBlue.setText("0" + code);
                } else {
                    holder.codeBlue.setText(String.valueOf(code));
                }
            } else if (numberType == RuleSet.TYPE_1_11_SDRX5){
                String code = "";
                int[] codes = result[position];
                for (int code1 : codes) {
                    if (code1 < 10) {
                        code += "0" + code1 + " ";
                    } else {
                        code += code1 + " ";
                    }
                }
                holder.codeRed.setText(code);
            } else {
                String code = "";
                int[] codes = result[position];
                for (int code1 : codes) {
                    code += code1 + " ";
                }
                holder.codeRed.setText(code);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.index)
        TextView index;
        /*@Bind(R.id.code)
        TextView code;*/
        @Bind(R.id.codeRed)
        TextView codeRed;
        @Bind(R.id.codeBlue)
        TextView codeBlue;
        @Bind(R.id.pick)
        ImageView pick;

        public ViewHolder(View convertView, int lotteryId) {
            ButterKnife.bind(this, convertView);
            //code.setVisibility(lotteryId != 100? View.VISIBLE : View.GONE);
            codeBlue.setVisibility(lotteryId == 100? View.VISIBLE : View.GONE);
            //codeRed.setVisibility(lotteryId == 100? View.VISIBLE : View.GONE);
            convertView.setTag(this);
        }
    }
}
