package com.shrinktool.component;

import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.Article;
import com.shrinktool.data.ArticleListCommand;
import com.shrinktool.data.ArticleListRespon;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 发现页--预测
 * Created by Alashi on 2017/2/10.
 */
public class FindRadioArticle extends FindRadio {
    private static final String TAG = "FindRadioArticle";
    private ArrayList<Article> articles;
    private ArticleDialog articleDialog;

    public FindRadioArticle(BaseFragment fragment, SwipeRefreshLayout refreshLayout, BaseAdapter adapter) {
        super(fragment, refreshLayout, adapter);
    }


    @Override
    public void reload() {
        ArticleListCommand command = new ArticleListCommand();
        command.setCurPage(1);
        command.setCategory(null);
        RestRequestManager.executeCommand(fragment.getActivity(), command, restCallback, 0, this);
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            articles = ((ArticleListRespon) response.getData()).getArticles();
            adapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            refreshLayout.setRefreshing(state == RestRequest.RUNNING);
        }
    };

    @Override
    public void onItemClick(int position) {
        if (articleDialog == null) {
            articleDialog = new ArticleDialog(fragment, newArticle -> {
                int update = -1;
                for (int i = 0, size = articles.size(); i < size; i++) {
                    if (articles.get(i).articleId == newArticle.articleId) {
                        update = i;
                        break;
                    }
                }
                if (update != -1) {
                    articles.set(update, newArticle);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        articleDialog.show(articles.get(position));
    }

    @Override
    public int getCount() {
        return articles == null? 0 : articles.size();
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
