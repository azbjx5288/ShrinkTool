package com.shrinktool.data;

import com.android.volley.Request;
import com.google.gson.annotations.SerializedName;
import com.shrinktool.base.net.RequestConfig;

/**
 * 文章详情
 * Created by Alashi on 2017/2/8.
 */
@RequestConfig(method = Request.Method.GET, api = "?a=articleDetail&c=article", response = Article.class)
public class ArticleDetailCommand {
    @SerializedName("article_id")
    private int articleId;

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
}
