package com.shrinktool.component;

import android.app.Dialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.shrinktool.BuildConfig;
import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.Article;
import com.shrinktool.data.ArticleCollectCommand;
import com.shrinktool.data.ArticleDetailCommand;
import com.shrinktool.data.ArticleLikeCommand;
import com.shrinktool.data.ArticleViewCommand;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 文章的详情的对话框及效果
 * Created by Alashi on 2017/2/6.
 */

public class ArticleDialog {
    private static final String TAG = "ArticleDialog";
    private static final int ID_LIKE = 1;
    private static final int ID_COLLECT = 2;
    private static final int ID_VIEW = 3;
    private static final int ID_RELOAD = 4;

    @Bind(R.id.title) TextView title;
    @Bind(R.id.title2) TextView title2;
    @Bind(R.id.content) TextView content;
    @Bind(R.id.content2) TextView content2;
    @Bind(R.id.contentScrollView) ScrollView contentScrollView;
    @Bind(R.id.contentOriginal) View contentOriginal;
    @Bind(R.id.contentReplace) View contentReplace;
    @Bind(R.id.time) TextView time;
    @Bind(R.id.time2) TextView time2;
    @Bind(R.id.collect) TextView collect;
    @Bind(R.id.like) TextView like;
    @Bind(R.id.titleBarLayout) View titleBarLayout;
    @Bind(R.id.placeholder) View placeholder;

    private float downY;
    private int downScrollY;
    private int placeholderHeight;

    private BaseFragment fragment;
    private Dialog dialog;
    private Article article;
    private ShareToDialog shareToDialog;
    private OnArticleChangedListener listener;

    public interface OnArticleChangedListener{
        void onChanged(Article newArticle);
    }

    public ArticleDialog(BaseFragment baseFragment, OnArticleChangedListener listener) {
        this.fragment = baseFragment;
        this.listener = listener;
    }

    public void show(Article article) {
        dialog = new Dialog(fragment.getActivity(), R.style.Dialog_Fullscreen_Transparent);
        dialog.setContentView(R.layout.article_dialog);
        ButterKnife.bind(this, dialog);
        content.setOnTouchListener(onTouchListener);
        contentScrollView.setOnTouchListener(onScrollTouchListener);
        setData(article);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window .setGravity(Gravity.BOTTOM);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = fragment.getActivity().getWindow().getDecorView().getHeight();
        window.setAttributes(lp);
        dialog.show();

        placeholderHeight = placeholder.getHeight();
    }

    private void setData(Article article){
        this.article = article;
        title.setText(article.title);
        title2.setText(article.title);
        if (article.createTime != null && article.createTime.length() > 10) {
            time.setText(article.createTime.substring(0, 10));
            time2.setText(article.createTime.substring(0, 10));
        } else {
            time.setText(article.createTime);
            time2.setText(article.createTime);
        }
        content.setText(Html.fromHtml(article.content));
        content2.setText(Html.fromHtml(article.content));
        like.setText(String.valueOf(article.likeNum));
        Utils.setDrawableLeft(like, article.isLike? R.drawable.wode_llls_hc_dz_on
                :R.drawable.wode_llls_hc_dz);
        Utils.setDrawableLeft(collect, article.isCollect? R.drawable.wode_llls_hc_sc_on
                :R.drawable.wode_llls_hc_sc);
        if (!article.isView && isLogin()) {
            view();
        }
    }

    private boolean isLogin() {
        return GoldenAsiaApp.getUserCentre().isLogin();
    }

    private View.OnTouchListener onScrollTouchListener = (v, event) -> {
        Log.d(TAG, ": " + contentScrollView.getScrollY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY();
                downScrollY = contentScrollView.getScrollY();
                break;
            case MotionEvent.ACTION_MOVE: {
                if (event.getRawY() - downY > 0
                        && downScrollY == 0) {
                    ViewGroup.LayoutParams params = placeholder.getLayoutParams();
                    params.height = (int) (event.getRawY() - downY);
                    placeholder.setLayoutParams(params);
                    if (params.height < placeholderHeight) {
                        titleBarLayout.setAlpha(1 - (float)params.height / placeholderHeight);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (placeholder.getHeight() > 0
                        || (event.getRawY() - downY > 0 && downScrollY == 0)) {
                    dialog.dismiss();
                }
            }
        }
        return false;
    };

    private View.OnTouchListener onTouchListener = (v, event) -> {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY();
                if (placeholderHeight == 0) {
                    placeholderHeight = placeholder.getHeight();
                }
                break;

            case MotionEvent.ACTION_MOVE: {
                float offsetY = event.getRawY() - downY;
                //offsetY 大于0时，手势先下滑；
                ViewGroup.LayoutParams params = placeholder.getLayoutParams();
                params.height = (int) (placeholderHeight + offsetY);
                if (params.height < 0) {
                    params.height = 0;
                }
                placeholder.setLayoutParams(params);
                if (params.height < placeholderHeight) {
                    titleBarLayout.setAlpha(1 - (float)params.height / placeholderHeight);
                }
                if (params.height == 0) {
                    contentOriginal.setVisibility(View.GONE);
                    contentReplace.setVisibility(View.VISIBLE);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (event.getRawY() - downY > 0) {
                    dialog.dismiss();
                }
                break;
            }
        }
        return true;
    };


    @OnClick({R.id.collect, R.id.like, R.id.share, R.id.titleBarLike
            , R.id.titleBarShare, R.id.titleBarCollect})
    public void onButton(View view){
        switch (view.getId()) {
            case R.id.collect:
            case R.id.titleBarCollect:
                if (isLogin()) {
                    collect();
                }
                break;

            case R.id.like:
            case R.id.titleBarLike:
                if (!article.isLike && isLogin()) {
                    like();
                }
                break;

            case R.id.share:
            case R.id.titleBarShare:
                if (shareToDialog == null) {
                    shareToDialog = new ShareToDialog(fragment.getActivity());
                }
                String summary = article.content.replaceAll("<[a-zA-Z]{1,}.*?>", "")
                        .replaceAll("</[a-zA-Z]{1,}>", "")
                        .replaceAll("&ldquo;", "\"")
                        .replaceAll("&rdquo;", "\"")
                        .replaceAll("&nbsp;", "");
                shareToDialog.setTitle(article.title);
                if (summary.length() > 100) {
                    shareToDialog.setSummary(summary.substring(0, 100));
                } else {
                    shareToDialog.setSummary(summary);
                }
                shareToDialog.setUrl(BuildConfig.BASE_URL + "/index.jsp?share_article_id=" + article.articleId);
                shareToDialog.show();
                break;
        }
    }

    private void view() {
        ArticleViewCommand command = new ArticleViewCommand();
        command.setArticleId(article.articleId);
        RestRequestManager.executeCommand(fragment.getContext(), command, restCallback, ID_VIEW, fragment);
    }

    private void collect() {
        ArticleCollectCommand command = new ArticleCollectCommand();
        command.setArticleId(article.articleId);
        command.setCollect(!article.isCollect);
        RestRequestManager.executeCommand(fragment.getContext(), command, restCallback, ID_COLLECT, fragment);
    }

    private void like() {
        ArticleLikeCommand command = new ArticleLikeCommand();
        command.setArticleId(article.articleId);
        RestRequestManager.executeCommand(fragment.getContext(), command, restCallback, ID_LIKE, fragment);
    }

    private void reload() {
        ArticleDetailCommand command = new ArticleDetailCommand();
        command.setArticleId(article.articleId);
        RestRequestManager.executeCommand(fragment.getContext(), command, restCallback, ID_RELOAD, fragment);
    }
    //已分享该文章
    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            switch (request.getId()) {
                case ID_COLLECT:
                    fragment.showToast(article.isCollect? "已取消收藏该文章":"已收藏该文章");
                    article.isCollect = !article.isCollect;
                    reload();
                    break;
                case ID_LIKE:
                    article.isLike = true;
                    fragment.showToast("+1");
                    reload();
                    break;
                case ID_RELOAD:
                    setData((Article) response.getData());
                    if (listener != null) {
                        listener.onChanged(article);
                    }
                    break;
                case ID_VIEW:
                    article.isView = true;
                    if (listener != null) {
                        listener.onChanged(article);
                    }
                    break;
            }
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (state == RestRequest.RUNNING) {
                if (request.getId() == ID_LIKE) {
                    fragment.showProgress("正在点赞...");
                } else if (request.getId() == ID_COLLECT) {
                    fragment.showProgress("正在收藏...");
                }
            } else {
                fragment.hideProgress();
            }
        }
    };
}
