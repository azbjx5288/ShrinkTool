package com.shrinktool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.shrinktool.BuildConfig;
import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.FragmentLauncher;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.Preferences;
import com.shrinktool.base.net.GsonHelper;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.component.GameIssue;
import com.shrinktool.component.HistoryIssueHelper;
import com.shrinktool.component.HistoryPopupWindow;
import com.shrinktool.data.Lottery;
import com.shrinktool.data.Method;
import com.shrinktool.data.MethodList;
import com.shrinktool.data.MethodListCommand;
import com.shrinktool.game.Game;
import com.shrinktool.game.GameConfig;
import com.shrinktool.game.MenuController;
import com.shrinktool.game.OnSelectedListener;
import com.shrinktool.material.RefiningCart;
import com.shrinktool.material.Ticket;
import com.shrinktool.rule.RuleSet;
import com.shrinktool.rule.ssq.SsqAssistInfo;
import com.shrinktool.view.IssueInfoDropDown;
import com.shrinktool.view.TableMenu;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 选号界面
 * Created by Alashi on 2016/2/17.
 */
public class GameFragment extends BaseFragment implements OnSelectedListener, TableMenu.OnClickMethodListener, GameIssue.OnIssueChangedListener {
    private static final String TAG = GameFragment.class.getSimpleName();

    private static final int ID_METHOD_LIST = 1;

    private TextView titleView;
    private View titleAssist;
    private View titleTextLayout;
    private WebView webView;

    @Bind(R.id.pick_notice) TextView pickNoticeView;
    @Bind(R.id.pick_game_layout) LinearLayout pickGameLayout;
    @Bind(R.id.choose_done_button) Button chooseDoneButton;
    @Bind(R.id.lottery_choose_bottom) RelativeLayout chooseBottomLayout;
    @Bind(R.id.game_issue_info) LinearLayout gameIssueInfoLayout;
    @Bind(R.id.game_issue_info_drop_down) IssueInfoDropDown issueInfoDropDown;
    @Bind(R.id.game_issue_info_list) View gameIssueInfoList;
    @Bind(R.id.gameScrollView) ScrollView scrollView;
    @Bind(R.id.gameScrollViewPlaceholder) View scrollViewPlaceholder;

    private Lottery lottery;
    private Game game;
    private MenuController menuController;
    private RefiningCart refiningCart;

    private PopupWindow settingPopupWindow;
    private CheckBox showMissCheckBox;
    private int[] miss;
    private boolean showMiss = true;

    private HistoryPopupWindow historyPopupWindow;
    private GameIssue gameIssue;
    private HistoryIssueHelper historyIssueHelper;

    public static void launch(BaseFragment fragment, Lottery lottery) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("lottery", lottery);
        FragmentLauncher.launch(fragment.getActivity(), GameFragment.class, bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        applyArguments();

        Activity activity = getActivity();
        titleView = (TextView) activity.findViewById(R.id.methodName);
        titleAssist = activity.findViewById(R.id.titleAssist);
        titleTextLayout = activity.findViewById(R.id.title_text_layout);
        activity.findViewById(R.id.modify).setOnClickListener(this::onClickModify);
        titleTextLayout.setOnClickListener(v -> showOrHideMenu());
        historyIssueHelper = new HistoryIssueHelper(activity, lottery.getLotteryId(),
                issueInfoDropDown, this::addPlaceholderIfNeed);
        issueInfoDropDown.setScrollView(scrollView);

        initMenu();
        loadMethodFromXml();
        //loadMenuFromFile();
        loadMenuOnline();

        gameIssue = new GameIssue(lottery.getLotteryId(), this, gameIssueInfoLayout, this);
    }

    private void applyArguments() {
        refiningCart = RefiningCart.getInstance();
        lottery = (Lottery) getArguments().getSerializable("lottery");
        Log.d(TAG, "applyArguments: lottery = " + lottery);
    }

    private void loadWebViewIfNeed() {
        if (webView != null) {
            return;
        }
        chooseBottomLayout.postDelayed(() -> {
            synchronized (chooseBottomLayout) {
                if (isFinishing()) {
                    return;
                }
                if (webView != null) {
                    update2WebView();
                    return;
                }
                webView = new WebView(getActivity());
                chooseBottomLayout.addView(webView, 1, 1);
                initWebView();
                webView.loadUrl("file:///android_asset/web/game.html");
            }
        }, 400);
    }

    private void loadMethodFromXml() {
        String key = "game_config_method_" + GoldenAsiaApp.getUserCentre().getUserID() + "_" + lottery.getLotteryId();
        Method method = GsonHelper.fromJson(Preferences.getString(getContext(), key, null), Method.class);

        if (method != null) {
            changeGameMethod(method);
        }
    }

    private void saveMethod2Xml(Method method) {
        String key = "game_config_method_" + GoldenAsiaApp.getUserCentre().getUserID() + "_" + lottery.getLotteryId();
        Preferences.saveString(getContext(), key, GsonHelper.toJson(method));
    }

    private void initMenu() {
        menuController = new MenuController(getActivity(), lottery);
        menuController.setOnClickMethodListener(this);
    }

    private void loadMenuOnline() {
        MethodListCommand methodListCommand = new MethodListCommand();
        methodListCommand.setLotteryID(lottery.getLotteryId());
        TypeToken typeToken = new TypeToken<RestResponse<ArrayList<Method>>>() {};
        RestRequestManager.executeCommand(getActivity(), methodListCommand, typeToken,
                restCallback, ID_METHOD_LIST, this);
    }

    private void loadMenuFromFile() {
        int lotteryID = lottery.getLotteryId();
        String json = loadAssets("json/methodList"+ lotteryID +".json");

        if (json == null) {
            loadMenuOnline();
            return;
        }

        ArrayList<Method> methodList =  GsonHelper.fromJson(json,
                new TypeToken<ArrayList<Method>>(){}.getType());
        if (game == null) {
            Method method = defaultGameMethod(methodList);
            saveMethod2Xml(method);
            changeGameMethod(method);
        }
        updateMenu(methodList);
    }

    private void updateMenu(ArrayList<Method> methodList) {
        MethodList methodList1 = new MethodList();
        methodList1.setChilds(methodList);
        ArrayList<MethodList> lists = new ArrayList<>();
        lists.add(methodList1);
        menuController.setMethodList(lists);
        if (methodList.size() > 1) {
            titleAssist.setVisibility(View.VISIBLE);
            titleTextLayout.setEnabled(true);
        } else {
            titleAssist.setVisibility(View.GONE);
            titleTextLayout.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        if (gameIssue != null) {
            gameIssue.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
        if (gameIssue != null) {
            gameIssue.onResume();
        }
    }

    @Override
    public void onDestroyView() {
        if (game != null) {
            game.destroy();
        }
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroyView();
    }

    public void onClickModify(View view){
        if (settingPopupWindow == null) {
            View layout = LayoutInflater.from(getContext()).inflate(R.layout.game_setting, null);
            settingPopupWindow = new PopupWindow(getActivity());
            settingPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            settingPopupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            settingPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            settingPopupWindow.setContentView(layout);
            settingPopupWindow.setFocusable(true);
            //settingPopupWindow.setAnimationStyle(R.style.pulldown);
            showMissCheckBox = (CheckBox) layout.findViewById(R.id.gameShowMiss);
            showMissCheckBox.setChecked(showMiss);
            showMissCheckBox.setOnClickListener(view1 -> {
                showMiss = showMissCheckBox.isChecked();
                game.setMiss(showMiss, miss);
                addPlaceholderIfNeed();
                settingPopupWindow.dismiss();
            });
        }

        settingPopupWindow.showAsDropDown(view);
    }

    //@OnClick(R.id.history)
    public void onClickHistory(View view){
        if (historyPopupWindow == null) {
            historyPopupWindow = new HistoryPopupWindow(getActivity(), lottery.getLotteryId());
        }

        historyPopupWindow.show(view);
    }

    //@OnClick(title_text_layout)
    public void showOrHideMenu() {
        Log.d(TAG, "showOrHideMenu: ");
        if (menuController.isShowing()) {
            menuController.hide();
        } else {
            menuController.show(titleTextLayout);
        }
    }

    @OnClick(R.id.pick_clear)
    public void onClearClick() {
        if (game != null) {
            game.reset();
        }
    }

    @OnClick(R.id.choose_done_button)
    public void onChooseDone() {
        if (game == null || game.getSingleNum() <= 0) {
            return;
        }

        refiningCart.clear();
        if (game.getSingleNum() > 0) {
            String codes = game.getSubmitCodes();
            Ticket ticket = new Ticket();
            ticket.setChooseMethod(game.getMethod());
            ticket.setChooseNotes(game.getSingleNum());
            ticket.setCodes(codes);
            refiningCart.setLottery(lottery);
            refiningCart.setTicket(ticket);
            refiningCart.setIssue(gameIssue.getIssue());
            if (GameConfig.getNumberType(lottery) == RuleSet.TYPE_SSQ) {
                //"01,05,07,08,19,27,12"
                String lastCode = gameIssue.getLastIssueCode();
                refiningCart.setAssist(null);
                if (lastCode != null && lastCode.length() > 0) {
                    int[] lastCodes = new int[7];
                    int i = 0;
                    for (String tmp: lastCode.split(" ")) {
                        lastCodes[i++] = Integer.parseInt(tmp);
                    }
                    SsqAssistInfo ssqAssistInfo = new SsqAssistInfo();
                    ssqAssistInfo.setLastIssueCode(lastCodes);
                    refiningCart.setAssist(ssqAssistInfo);
                }
            }
        }

        launchFragmentForResult(RuleSettingFragment.class, null, 1);
    }

    private void initWebView() {
        webView.setOverScrollMode(WebView.OVER_SCROLL_ALWAYS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsInterface(), "androidJs");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refiningCart.clear();
        /*if (game != null) {
            game.reset();
        }*/
    }

    @Override
    public void onClickMethod(Method method) {
        menuController.hide();
        changeGameMethod(method);
    }

    //GameIssue.OnIssueChangedListener的回调
    @Override
    public void onIssueChanged() {
        historyIssueHelper.refresh();
        historyIssueHelper.setGameIssueInfo(gameIssue.getGameIssueInfo());
        if (game != null) {
            miss = gameIssue.getMissCode(game.getMethod().getMethodId());
            if (game != null) {
                game.setMiss(showMiss, miss);
            }
            addPlaceholderIfNeed();
        }
    }

    private class JsInterface {
        @JavascriptInterface
        public String getData() {
            if (game == null) {
                return "";
            }
            return game.getWebViewCode();
        }

        @JavascriptInterface
        public String getMethodName() {
            if (game == null) {
                return "";
            }
            if (GameConfig.getNumberType(lottery) == RuleSet.TYPE_SSQ) {
                return "SSQ";
            }
            return game.getMethod().getName();
        }

        @JavascriptInterface
        public void result(int singleNum, boolean isDup) {
            Log.d(TAG, "result() called with: " + "singleNum = [" + singleNum + "], isDup = [" + isDup + "]");
            if (game == null) {
                return;
            }
            game.setNumState(singleNum, isDup);
            webView.post(updatePickNoticeRunnable);
        }
    }

    private void update2WebView() {
        if (webView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("calculate();", null);
        } else {
            webView.loadUrl("javascript:calculate();");
        }
    }

    private Runnable updatePickNoticeRunnable = new Runnable() {
        @Override
        public void run() {
            if (game == null) {
                return;
            }
            updatePickNoticeView(game.getSingleNum());
            if (game.getSingleNum() > 0) {
                chooseDoneButton.setEnabled(true);
            } else {
                chooseDoneButton.setEnabled(!refiningCart.isEmpty());
            }
        }
    };

    private void updatePickNoticeView(int num) {
        pickNoticeView.setText(Html.fromHtml(String.format("共<font color=#FFA423>%d</font>注<font " +
                "color=#FFA423>%d</font>元",
                num, 2 * num)));
    }

    private void changeGameMethod(Method method) {
        if (method == null) {
            return;
        }

        if (game == null) {
            pickGameLayout.removeAllViews();
        } else {
            if (method.getName().equals(game.getMethod().getName())) {
                //同一个玩法，不用切换
                return;
            }
            game.destroy();
            menuController.addPreference(method);
            saveMethod2Xml(method);
        }
        refiningCart.clear();
        menuController.setCurrentMethod(method);
        titleView.setText(method.getCname());
        updatePickNoticeView(0);
        chooseDoneButton.setEnabled(!refiningCart.isEmpty());
        game = GameConfig.createGame(method);
        game.inflate(pickGameLayout);
        game.setOnSelectedListener(this);

        loadWebViewIfNeed();

        game.setMiss(showMiss, miss);

        addPlaceholderIfNeed();
    }

    //为保证下拉显示最近开奖历史，要将scrollView的内部View的高度大于scrollView的高度
    public void addPlaceholderIfNeed(){
        if (scrollViewPlaceholder == null) {
            return;
        }
        int placeholderHeight = scrollViewPlaceholder.getHeight();
        int innerHeight = scrollView.getChildAt(0).getHeight();
        //Log.i(TAG, "addPlaceholderIfNeed: " + innerHeight + ", " + scrollView.getHeight() + ", " + placeholderHeight);
        if (innerHeight == 0 || scrollView.getHeight() == 0) {
            scrollViewPlaceholder.postDelayed(this::addPlaceholderIfNeed, 100);
            return;
        }

        if (innerHeight - placeholderHeight < scrollView.getHeight()) {
            ViewGroup.LayoutParams params = scrollViewPlaceholder.getLayoutParams();
            params.height = scrollView.getHeight() - (innerHeight - placeholderHeight) + 20;
            //Log.i(TAG, "addPlaceholderIfNeed: params.height " + params.height);
            scrollViewPlaceholder.setLayoutParams(params);
        }
    }

    private Method defaultGameMethod(ArrayList<Method> methodList) {
        return methodList.get(0);
    }

    //game.setOnSelectedListener(this)的回调
    @Override
    public void onChanged(Game game) {
        Log.i(TAG, "onChanged: ");
        loadWebViewIfNeed();
        update2WebView();
    }

    /** 上次选择的玩法可能已经不支持 */
    private void judgeMethod(ArrayList<Method> methodList){
        if (game == null) {
            return;
        }
        boolean find = false;
        String last = game.getMethod().getName();
        for (Method method: methodList) {
            if (method.getName().equals(last)) {
                find = true;
                break;
            }
        }

        if (!find) {
            changeGameMethod(defaultGameMethod(methodList));
        }
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            if (request.getId() == ID_METHOD_LIST) {
                ArrayList<Method> methodList = (ArrayList<Method>) response.getData();
                //Log.i(TAG, "onRestComplete: " + GsonHelper.toJson(methodList));
                if (game == null) {
                    Method method = defaultGameMethod(methodList);
                    saveMethod2Xml(method);
                    menuController.addPreference(method);
                    changeGameMethod(method);
                } else {
                    judgeMethod(methodList);
                }
                updateMenu(methodList);
            }
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
        }
    };
}
