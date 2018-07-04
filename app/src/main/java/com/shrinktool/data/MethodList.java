package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 玩法返回信息
 * Created by ACE-PC on 2016/1/21.
 */
public class MethodList {


    /**
     * mg_id : 155
     * mg_name : 后一
     * childs : [{"method_id":"528","lottery_id":"15","name":"YXZX","cname":"后一直选","mg_id":"155","mg_name":"后一","can_input":"0","levels":"1"},{"method_id":"456","lottery_id":"15","name":"WXDW","cname":"五星定位","mg_id":"155","mg_name":"后一","can_input":"0","levels":"5"}]
     */

    @SerializedName("mg_id")
    private String mgId;
    @SerializedName("mg_name")
    private String mgName;
    /**
     * method_id : 528
     * lottery_id : 15
     * name : YXZX
     * cname : 后一直选
     * mg_id : 155
     * mg_name : 后一
     * can_input : 0
     * levels : 1
     */

    @SerializedName("childs")
    private List<Method> childs;

    public void setMgId(String mgId) {
        this.mgId = mgId;
    }

    public void setMgName(String mgName) {
        this.mgName = mgName;
    }

    public void setChilds(List<Method> childs) {
        this.childs = childs;
    }

    public String getMgId() {
        return mgId;
    }

    public String getMgName() {
        return mgName;
    }

    public List<Method> getChilds() {
        return childs;
    }

}
