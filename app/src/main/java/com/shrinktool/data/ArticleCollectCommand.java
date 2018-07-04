package com.shrinktool.data;

import com.android.volley.Request;
import com.google.gson.annotations.SerializedName;
import com.shrinktool.base.net.RequestConfig;

/**
 * 文章收藏
 * Created by Alashi on 2017/2/8.
 */
@RequestConfig(method = Request.Method.GET, api = "?a=articleCollect&c=article")
public class ArticleCollectCommand {
    @SerializedName("article_id")
    private int articleId;
    @SerializedName("is_collect")
    private boolean isCollect;

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }
}
