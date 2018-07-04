package com.shrinktool.data;

import com.android.volley.Request;
import com.google.gson.annotations.SerializedName;
import com.shrinktool.base.net.RequestConfig;

/**
 * 文章点赞
 * Created by Alashi on 2017/2/8.
 */
@RequestConfig(method = Request.Method.GET, api = "?a=articleLike&c=article")
public class ArticleLikeCommand {
    @SerializedName("article_id")
    private int articleId;

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
}
