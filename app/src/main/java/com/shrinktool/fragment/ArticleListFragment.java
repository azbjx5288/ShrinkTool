package com.shrinktool.fragment;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.component.ArticleDialog;
import com.shrinktool.component.Utils;
import com.shrinktool.data.Article;
import com.shrinktool.data.ArticleDeleteHistoryCommand;
import com.shrinktool.data.ArticleListCommand;
import com.shrinktool.data.ArticleListRespon;
import com.shrinktool.game.GameConfig;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


/**
 * 预测文章列表
 * Created by Alashi on 2017/2/6.
 */
public class ArticleListFragment extends BaseFragment {

    private static final String TAG = "ArticleListFragment";
    private static final int TYPE_GENERAL = 0;
    private static final int TYPE_COLLECT = 1;
    private static final int TYPE_HISTORY = 2;

    private static final int ID_LOAD = 1;
    private static final int ID_DELETE = 2;

    @Bind(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.list) ListView listView;

    /** 文章类型，0：按彩种的Category获取；1：我的收藏；2：流量历史 */
    private int articleType;

    private int page;
    private ArrayList<Article> articles = new ArrayList<>();
    private MyAdapter myAdapter = new MyAdapter();
    private int lotteryId;
    private ArticleDialog articleDialog;
    private PopupWindow deleteHistoryPopupWindow;

    public static void launch(BaseFragment fragment, int articleType) {
        Bundle bundle = new Bundle();
        bundle.putInt("articleType", articleType);
        fragment.launchFragment(ArticleListFragment.class, bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        articleType = getArguments().getInt("articleType", TYPE_GENERAL);
        switch (articleType) {
            case TYPE_GENERAL:
                return inflater.inflate(R.layout.article_list, container, false);
            case TYPE_COLLECT:
                return inflateView(inflater, container, "我的收藏", R.layout.article_list);
            case TYPE_HISTORY:
                return inflateView(inflater, container, "浏览历史", R.layout.article_list);
            default:
                return null;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lotteryId = getArguments().getInt("id", 0);
        refreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"), Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
        refreshLayout.setOnRefreshListener(()->{
            page = 1;
            loadPage();
        });

        //test();

        listView.setAdapter(myAdapter);
        loadPage();
        if (articleType == TYPE_HISTORY) {
            titleBarHelper.addMenuItem(R.drawable.lljl_delete, this::showDeleteHistory);
        }
    }

    private void showDeleteHistory(View view){
        if (deleteHistoryPopupWindow == null) {
            View layout = LayoutInflater.from(getContext()).inflate(R.layout.delete_history, null);
            deleteHistoryPopupWindow = new PopupWindow(getActivity());
            deleteHistoryPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            deleteHistoryPopupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            deleteHistoryPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            deleteHistoryPopupWindow.setContentView(layout);
            deleteHistoryPopupWindow.setFocusable(true);
            layout.findViewById(R.id.del).setOnClickListener(v -> {
                deleteHistory();
                deleteHistoryPopupWindow.dismiss();
            });
        }

        deleteHistoryPopupWindow.showAsDropDown(view);
    }

    private void deleteHistory(){
        ArticleDeleteHistoryCommand command = new ArticleDeleteHistoryCommand();
        executeCommand(command, restCallback, ID_DELETE);
    }

    @OnClick(R.id.loadMore)
    public void onButton(){
        page++;
        loadPage();
    }

    @OnItemClick(R.id.list)
    public void onItemClick(int position) {
        showArticle(articles.get(position));
    }

    private void loadPage(){
        ArticleListCommand command = new ArticleListCommand();
        command.setCurPage(page);
        switch (articleType) {
            case TYPE_GENERAL:
                command.setCategory(GameConfig.getCategory(lotteryId));
                break;
            case TYPE_COLLECT://收藏的
                command.setCollect(true);
                break;
            case TYPE_HISTORY://流量历史的
                command.setView(true);
                break;
        }
        executeCommand(command, restCallback, ID_LOAD);
    }

    private void showArticle(Article target) {
        if (articleDialog == null) {
            articleDialog = new ArticleDialog(this, newArticle -> {
                if (TYPE_COLLECT == articleType && newArticle.isCollect){
                    articles.add(0, newArticle);
                    myAdapter.notifyDataSetChanged();
                    return;
                }

                int update = -1;
                for (int i = 0, size = articles.size(); i < size; i++) {
                    if (articles.get(i).articleId == newArticle.articleId) {
                        update = i;
                        break;
                    }
                }
                if (update != -1) {
                    if (TYPE_COLLECT == articleType && !newArticle.isCollect){
                        articles.remove(update);
                    } else {
                        articles.set(update, newArticle);
                    }
                    myAdapter.notifyDataSetChanged();
                }
            });
        }
        articleDialog.show(target);
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            if (request.getId() == ID_LOAD) {
                if (page == 1) {
                    articles.clear();
                }
                articles.addAll(((ArticleListRespon) response.getData()).getArticles());
            } else {
                page = 1;
                articles.clear();
                showToast("已清空历史记录");
            }
            myAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (request.getId() == ID_LOAD) {
                refreshLayout.setRefreshing(state == RestRequest.RUNNING);
            }
        }
    };

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return articles.size();
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_item, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Article article = articles.get(position);
            holder.title.setText(article.title);
            holder.content.setText(Html.fromHtml(article.content));
            holder.count.setText(String.valueOf(article.likeNum));
            Utils.setDrawableLeft(holder.count, article.isLike? R.drawable.wode_llls_hc_dz_on
                    :R.drawable.wode_llls_hc_dz);
            if (article.createTime != null && article.createTime.length() > 10) {
                holder.time.setText(article.createTime.substring(0, 10));
            } else {
                holder.time.setText(article.createTime);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.count)
        TextView count;
        @Bind(R.id.time)
        TextView time;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
            convertView.setTag(this);
        }
    }
}
