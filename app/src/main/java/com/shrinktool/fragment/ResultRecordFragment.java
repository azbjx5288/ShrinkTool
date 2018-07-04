package com.shrinktool.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.FragmentLauncher;
import com.shrinktool.app.GameActivity;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.net.GsonHelper;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.Lottery;
import com.shrinktool.data.PlanDetail;
import com.shrinktool.data.PlanDetailCommand;
import com.shrinktool.game.GameConfig;
import com.shrinktool.material.ConstantInformation;
import com.shrinktool.material.Ticket;
import com.shrinktool.record.RecordAnalyze;
import com.shrinktool.record.RecordAnalyzeSDRX5;
import com.shrinktool.record.RecordAnalyzeSSQ;
import com.shrinktool.record.RecordAnalyzeSXZX;
import com.shrinktool.rule.Plan;
import com.shrinktool.rule.ResultRecord;
import com.shrinktool.rule.RuleRecord;
import com.shrinktool.rule.RuleSet;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 过滤记录页面
 * Created by Alashi on 2016/6/3.
 */
public class ResultRecordFragment extends BaseFragment{
    private static final String TAG = "ResultRecordFragment";

    @Bind(R.id.resultContent) View resultContent;
    @Bind(R.id.lotteryIco) ImageView lotteryIco;
    @Bind(R.id.lotteryName) TextView lotteryName;
    @Bind(R.id.lotteryIssue) TextView lotteryIssue;
    @Bind(R.id.state) TextView state;
    @Bind(R.id.time) TextView time;
    @Bind(R.id.replay) TextView replay;
    //@Bind(R.id.fiferLayout) LinearLayout fiferLayout;
    //@Bind(R.id.pickOriginalLayout) LinearLayout pickOriginalLayout;
    //@Bind(R.id.findRadioGroup) RadioGroup findRadioGroup;
    @Bind(R.id.notOpenLayout) ViewGroup notOpenLayout;
    @Bind(R.id.openCodeLayout) ViewGroup openCodeLayout;

    private ResultRecord resultRecord;
    private RecordAnalyze recordAnalyze;
    private int filterId;

    public static void launch(BaseFragment fragment, int filterId){
        Bundle bundle = new Bundle();
        bundle.putInt("filterId", filterId);
        FragmentLauncher.launch(fragment.getActivity(), ResultRecordFragment.class, bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, "方案详情", R.layout.result_record);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resultContent.setVisibility(View.GONE);
        filterId = getArguments().getInt("filterId");
        /*findRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton1) {
                fiferLayout.setVisibility(View.VISIBLE);
                pickOriginalLayout.setVisibility(View.GONE);
            } else {
                fiferLayout.setVisibility(View.GONE);
                pickOriginalLayout.setVisibility(View.VISIBLE);
            }
        });*/
        loadDataOnline();
    }

    private void refreshWebView() {

    }

    private void loadDataOnline() {
        PlanDetailCommand command  = new PlanDetailCommand();
        command.setFilterId(filterId);
        executeCommand(command, restCallback);
    }

    private void updateOpenCode(PlanDetail planDetail) {
        if (planDetail.isPrize == -1) {
            state.setText("未开奖");
            notOpenLayout.setVisibility(View.VISIBLE);
            openCodeLayout.setVisibility(View.GONE);
            replay.setVisibility(View.GONE);
            return;
        }

        state.setText(planDetail.isPrize > 0 ? "中奖" : "未中奖");
        replay.setVisibility(planDetail.isPrize > 0 ? View.GONE : View.VISIBLE);
        notOpenLayout.setVisibility(View.GONE);
        openCodeLayout.setVisibility(View.VISIBLE);
        String openCode = planDetail.getCode();
        int childCount = openCodeLayout.getChildCount();
        if (openCode.contains(" ")) {
            String[] codes = openCode.split(" ");
            for (int i = 0; i < childCount; i++) {
                TextView ball = (TextView) openCodeLayout.getChildAt(i);
                if (i < codes.length) {
                    ball.setVisibility(View.VISIBLE);
                    ball.setText(codes[i].length() == 1? "0" + codes[i] : codes[i]);
                } else {
                    ball.setVisibility(View.GONE);
                }
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                TextView ball = (TextView) openCodeLayout.getChildAt(i);
                if (i < openCode.length()) {
                    ball.setVisibility(View.VISIBLE);
                    ball.setText(String.valueOf(openCode.charAt(i) - 48));
                } else {
                    ball.setVisibility(View.GONE);
                }
            }
        }
    }

    private void applyPlanDetail(PlanDetail planDetail) {
        resultContent.setVisibility(View.VISIBLE);
        resultRecord = null;
        if (planDetail == null) {
            showToast("无效数据！");
            return;
        }

        Plan plan = planDetail.filterPlan;
        if (plan.getVersion() == ResultRecord.VERSION){
            resultRecord = GsonHelper.fromJson(plan.getRecord(), ResultRecord.class);
            Log.i(TAG, "applyPlanDetail: " + GsonHelper.toJson(resultRecord));
        } else {
            showToast("不支持的过滤方案版本！");
            return;
        }

        if (planDetail.inputTime != null && planDetail.inputTime.length() > 16) {
            time.setText("开奖时间：" + planDetail.inputTime.substring(0, 16));
        } else {
            time.setText("开奖时间：" + planDetail.inputTime);
        }

        Lottery lottery = resultRecord.getLottery();
        Ticket ticket = resultRecord.getTicket();
        RuleRecord record = resultRecord.getRuleRecord();
        if (false) {
            //TODO:测试的假数据
            planDetail.isPrize = 0;
            String methodName = ticket.getChooseMethod().getName();
            switch (methodName) {
                case "SXZX":
                    planDetail.code = "34510";
                    if (lottery.getLotteryId() == 9 || lottery.getLotteryId() == 10) {
                        planDetail.code = "365";
                    }
                    break;
                case "SDRX5":
                    planDetail.code = "02 04 03 08 09";
                    break;
                case "FCSSQ":
                    planDetail.code = "07 13 15 27 28 29 13";
                    break;
            }
        }
        lotteryIco.setImageResource(ConstantInformation.getLotteryLogo(lottery.getLotteryId()));
        lotteryName.setText(lottery.getCname());
        lotteryIssue.setText("第" + record.getIssue() + "期  "
                + ticket.getChooseMethod().getCname());
        //count.setText();
        updateOpenCode(planDetail);

        int numberType = GameConfig.getNumberType(lottery);
        switch (numberType) {
            case RuleSet.TYPE_0_9_SXZX:
                recordAnalyze = new RecordAnalyzeSXZX(getView(), planDetail, resultRecord);
                break;
            case RuleSet.TYPE_1_11_SDRX5:
                recordAnalyze = new RecordAnalyzeSDRX5(getView(), planDetail, resultRecord);
                break;
            case RuleSet.TYPE_SSQ:
                recordAnalyze = new RecordAnalyzeSSQ(getView(), planDetail, resultRecord);
                break;
            default:
                Log.e(TAG, "onViewCreated: 不支持的类型：" + numberType);
                showToast("不支持的类型！");
                return;
        }
        recordAnalyze.apply();
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            applyPlanDetail((PlanDetail) response.getData());
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (state == RestRequest.RUNNING) {
                showProgress("加载中...");
            } else {
                hideProgress();
            }
        }
    };

    @OnClick({R.id.go2game, R.id.resultCopy, R.id.resultSaveOut, R.id.replay, R.id.pickNumberMore,
            R.id.pickNumberLayout })
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.go2game:
                GameActivity.launch(getActivity(), resultRecord.getLottery().getLotteryId(), 0);
                break;
            case R.id.replay:
                recordAnalyze.replay();
                view.setVisibility(View.GONE);
                break;
            case R.id.resultCopy:
                copyOrSave(true);
                break;
            case R.id.resultSaveOut:
                copyOrSave(false);
                break;
            case R.id.pickNumberMore:
            case R.id.pickNumberLayout:
                ResultRecordCodeFragment.launch(this, recordAnalyze.getCopyCode());
                break;
        }
    }
    private void copyOrSave(boolean isCopy) {
        showProgress(isCopy? "正在复制" : "正在保存");
        GoldenAsiaApp.getThreadPool().submit(jc -> {
            if (isCopy) {
                return recordAnalyze.getCopyCode();
            } else {
                return com.shrinktool.component.Utils.writeOutFilterResult(getContext(),
                        resultRecord.getLottery().getCname(), recordAnalyze.getCopyCode());
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
}
