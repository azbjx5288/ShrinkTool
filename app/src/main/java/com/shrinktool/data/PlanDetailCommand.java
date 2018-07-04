package com.shrinktool.data;

import com.android.volley.Request;
import com.shrinktool.base.net.RequestConfig;

/**
 * 过滤方案详情
 * Created by Alashi on 2016/8/10.
 */
@RequestConfig(api = "?c=game&a=planDetail", method = Request.Method.GET, response = PlanDetail.class)
public class PlanDetailCommand {
    private int filterId;
    private int type = 1;

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
