package com.shrinktool.app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shrinktool.Analyzer.AnalyzerType;
import com.shrinktool.Analyzer.CodeFormView;
import com.shrinktool.Analyzer.TrendProcessor;
import com.shrinktool.Analyzer.Config;
import com.shrinktool.R;
import com.shrinktool.base.Preferences;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.component.TitleBarHelper;
import com.shrinktool.component.Utils;
import com.shrinktool.data.TendencyCommand;
import com.shrinktool.game.GameConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 图表走势
 * Created by Alashi on 2016/7/12.
 */
public class TrendActivity extends Activity{
    private static final String TAG = "TrendActivity";
    private static final String PREFERENCES_KEY = "trend_activity_analyzer_";
    private static final String PREFERENCES_NUM_KEY = "trend_activity_analyzer_num_";

    private TitleBarHelper titleBarHelper;

    private HorizontalScrollView horizontalScrollView;
    private ScrollView verticalScrollview;

    private TextView typeNameLeft;
    private TextView typeNameMiddle;
    private TextView typeNameRight;
    private CodeFormView codeFormView;
    private CodeFormView titleFormView;

    private int typeIndex = -1;
    //private Analyzer analyzer;
    private TrendProcessor trendProcessor;

    private int lotteryId;

    public static void launch(Activity activity, int lotteryId) {
        activity.startActivity(new Intent(activity, TrendActivity.class)
                .putExtra("id", lotteryId));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_bar_fragment);
        lotteryId = getIntent().getIntExtra("id", -1);
        Utils.statusColor(this);
        RelativeLayout topLayout = (RelativeLayout) findViewById(R.id.titleBarLayout);
        titleBarHelper = new TitleBarHelper(this, topLayout, true);
        LayoutInflater.from(this).inflate(R.layout.activity_trend,
                (ViewGroup) topLayout.findViewById(R.id.title_bar_fragment_content), true);
        titleBarHelper.setTitle(GameConfig.getLotteryName(lotteryId));
        //titleBarHelper.addMenuItem("期数", this::showIssueSetting);
        initMenuLayout();
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scrollview_to);
        verticalScrollview = (ScrollView) findViewById(R.id.synchor_double_red);
        codeFormView = (CodeFormView) findViewById(R.id.codeForm);
        titleFormView = (CodeFormView) findViewById(R.id.titleForm);
        typeNameLeft = (TextView) findViewById(R.id.typeNameLeft);
        typeNameMiddle = (TextView) findViewById(R.id.typeNameMiddle);
        typeNameRight = (TextView) findViewById(R.id.typeNameRight);
        typeNameLeft.setOnClickListener(view ->applyAnalyzer(typeIndex - 1, false));
        typeNameRight.setOnClickListener(view ->applyAnalyzer(typeIndex + 1, false));
        typeNameMiddle.setOnClickListener(this::showDialog);

        Log.d(TAG, "onCreate: lotteryId -> " + lotteryId);
        if (lotteryId == -1) {
            Toast.makeText(TrendActivity.this, "无效彩种", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //TODO:后续这里需要methodId
            trendProcessor = Config.getTrendProcessor(lotteryId);

            typeIndex = Preferences.getInt(TrendActivity.this, PREFERENCES_KEY + lotteryId, 0);
            int num = Preferences.getInt(TrendActivity.this, PREFERENCES_NUM_KEY + lotteryId, 10);
            loadData(num, true);
        }
    }

    private void initMenuLayout() {
        LinearLayout spinnerLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.trend_menu,
                titleBarHelper.getActionBarMenuLayout(), false);
        Spinner spinner = (Spinner) spinnerLayout.findViewById(R.id.spinner);
        titleBarHelper.addMenuItem(spinnerLayout);

        String[] name = new String[]{"10期", "20期", "50期","100期"};
        int [] data = new int[]{10, 20, 50, 100};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.trend_menu_spinner_item, name);
        arrayAdapter.setDropDownViewResource(R.layout.trend_menu_spinner_drop_item);
        spinner.setAdapter(arrayAdapter);
        int num = Preferences.getInt(TrendActivity.this, PREFERENCES_NUM_KEY + lotteryId, 10);
        for (int i = 0; i < data.length; i++) {
            if (data[i] == num) {
                spinner.setSelection(i);
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstTime = true;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (firstTime) {
                    firstTime = false;
                    return;
                }
                loadData(data[i], false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void loadData(int issueNums, boolean cache) {
        Log.w(TAG, "loadData: ", new Throwable());
        if (trendProcessor == null) {
            Toast.makeText(TrendActivity.this, "不支持的类型, lottery " + lotteryId, Toast.LENGTH_SHORT).show();
            return;
        }
        Preferences.saveInt(TrendActivity.this, PREFERENCES_NUM_KEY + lotteryId, issueNums);
        TendencyCommand command = new TendencyCommand();
        command.setIssueNums(issueNums);
        command.setLotteryId(lotteryId);
        command.setMethodId(trendProcessor.getMethodId());
        RestRequest restRequest = RestRequestManager.createRequest(this, command, trendProcessor.getTypeToken(),
                callback, 0, this);
        if (cache) {
            RestResponse restResponse = restRequest.getCache();
            if (restResponse != null && restResponse.getData() instanceof List) {
                trendProcessor.setJson((List) restResponse.getData());
                applyAnalyzer(typeIndex, true);
            }
        }
        restRequest.execute();
    }

    private RestCallback callback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            trendProcessor.setJson((List) response.getData());
            applyAnalyzer(typeIndex, true);
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (state == RestRequest.RUNNING) {
                titleBarHelper.showProgress("加载中");
            } else {
                titleBarHelper.hideProgress();
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void showDialog(View view) {
        if (trendProcessor == null) {
            return;
        }
        ArrayList<AnalyzerType> types = trendProcessor.getSupportType();
        String[] name = new String[types.size()];
        for (int i = 0; i < name.length; i++) {
            name[i] = types.get(i).getDisplay();
        }
        new AlertDialog.Builder(this)
                .setTitle("选择走势类型")
                .setItems(name, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    applyAnalyzer(i, false);
                })
                .show();
    }

    private void applyAnalyzer(int index, boolean force) {
        if (trendProcessor == null) {
            return;
        }
        if (!force && index == typeIndex) {
            return;
        }
        Preferences.saveInt(this, PREFERENCES_KEY + lotteryId, index);
        ArrayList<AnalyzerType> types = trendProcessor.getSupportType();
        if (types.size() == 0) {
            return;
        }
        AnalyzerType analyzerType;
        if (index < 0 || index >= types.size()) {
            analyzerType = types.get(0);
        } else {
            analyzerType = types.get(index);
        }
        typeIndex = index;
        trendProcessor.process(analyzerType);
        titleFormView.setRowInfo(trendProcessor.getTitleInfo());
        codeFormView.setRowInfo(trendProcessor.getRowInfo());
        typeNameMiddle.setText(analyzerType.getDisplay());
        if (index - 1 >= 0) {
            typeNameLeft.setEnabled(true);
            typeNameLeft.setText(types.get(index - 1).getDisplay());
        } else {
            typeNameLeft.setText("");
            typeNameLeft.setEnabled(false);
        }

        if (index + 1 < types.size()) {
            typeNameRight.setEnabled(true);
            typeNameRight.setText(types.get(index + 1).getDisplay());
        } else {
            typeNameRight.setText("");
            typeNameRight.setEnabled(false);
        }

        horizontalScrollView.scrollTo(0, 0);
        verticalScrollview.scrollTo(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        titleBarHelper.onDestroyView();
        RestRequestManager.cancelAll(this);
        ButterKnife.unbind(this);
    }
}
