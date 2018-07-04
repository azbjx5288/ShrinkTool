package com.shrinktool.data;

import com.android.volley.Request;
import com.google.gson.annotations.SerializedName;
import com.shrinktool.base.net.RequestConfig;

/**
 * 删除浏览记录
 * Created by Alashi on 2017/2/8.
 */
@RequestConfig(method = Request.Method.GET, api = "?c=article&a=deleteView")
public class ArticleDeleteHistoryCommand {
    //要删除浏览记录的文章ID,不传或者传0表示删除全部浏览记录
    @SerializedName("article_id")
    private int articleId;

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
}
