package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

/**
 * 文章基本信息
 * Created by Alashi on 2017/2/6.
 */

public class Article {
    @SerializedName("article_id")
    public int articleId; //文章ID
    @SerializedName("categoryId")
    public String category_id; //文章类型ID，ssc 115 ssq
    public String title; //; //标题
    public String content; //; //内容
    @SerializedName("create_time")
    public String createTime; //发布日期
    public String status; //; //状态
    @SerializedName("user_id")
    public String userId; //; //用户ID
    @SerializedName("is_like")
    public boolean isLike; //; //是否点赞
    @SerializedName("is_collect")
    public boolean isCollect; //是否收藏
    @SerializedName("is_view")
    public boolean isView; //; //是否已浏览
    @SerializedName("view_num")
    public int viewNum;
    @SerializedName("collect_num")
    public int collectNum;
    @SerializedName("like_num")
    public int likeNum;
}
