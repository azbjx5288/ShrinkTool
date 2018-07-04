package com.shrinktool.data;

import com.android.volley.Request;
import com.google.gson.annotations.SerializedName;
import com.shrinktool.base.net.RequestConfig;

/**
 * 手机客户端登出
 * Created by User on 2016/1/15.
 */
@RequestConfig(method = Request.Method.GET, api = "?a=articleList&c=article", response = ArticleListRespon.class)
public class ArticleListCommand {
    //彩种类型英文名： 例如 时时彩 ：   ssc; 11选5 ：115; 双色球： ssq
    private String category;
    //当前页数, 从 1 开始可不传
    private int curPage;
    //每页显示条数默20 可不传
    private int perPage = 30;
    //是否获取自己的历史记录,不传 默认0	是否获取自己的历史记录    0 否 ， 1 是
    @SerializedName("is_view")
    private boolean isView;
    //不传 默认0	是否获取自己的收藏记录    0 否 ， 1 是
   @SerializedName("is_collect")
    private boolean isCollect;

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }
}
