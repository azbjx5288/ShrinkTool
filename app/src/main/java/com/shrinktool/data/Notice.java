package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

/**
 * 公告与banner的列表项
 * Created by Alashi on 2016/1/19.
 */
public class Notice {


    /**
     * notice_id : 2
     * type : 3
     * title : 第二个Banner
     * img_path : images/mobile//201612020523352212.jpg
     * link : http://sscapp/test.html
     * content : <p>测试内容</p>
     * create_time : 2016-12-02 17:23:35
     * start_time : 2016-12-02 17:24:08
     * expire_time : 2016-12-09 17:24:10
     * status : 1
     * is_stick : 0
     * ts : 2016-12-02 17:23:35
     */

    @SerializedName("notice_id")
    public String noticeId;
    public String type;
    public String title;
    @SerializedName("img_path")
    public String imgPath;
    public String link;
    public String content;
    public String create_time;
    public String start_time;
    public String expire_time;
    public String status;
    public String is_stick;
    public String ts;

    public String getNoticeId() {
        return noticeId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }
}
